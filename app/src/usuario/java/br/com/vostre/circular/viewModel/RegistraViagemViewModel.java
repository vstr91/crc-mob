package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import androidx.databinding.ObservableField;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.ViagemItinerario;
import br.com.vostre.circular.model.api.CircularAPI;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.utils.DataHoraUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RegistraViagemViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<ItinerarioPartidaDestino> itinerario;

    public ParadaItinerarioBairro parada;

    public LiveData<List<ParadaBairro>> paradas;
    public MutableLiveData<Location> localAtual;
    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    public static MutableLiveData<Integer> retorno;

    public LiveData<List<ViagemItinerario>> registros;

    public LiveData<ItinerarioPartidaDestino> getItinerario() {
        return itinerario;
    }

    public void setItinerario(String itinerario) {
        this.itinerario = appDatabase.itinerarioDAO().carregar(itinerario);
        paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro(itinerario);
        registros = appDatabase.viagemItinerarioDAO().carregarPorItinerario(itinerario);
//        paradasItinerario.setValue(appDatabase.paradaItinerarioDAO()
//                .listarTodosAtivosPorItinerarioComBairro(itinerario).getValue());
    }

    public ParadaItinerarioBairro getParada() {
        return parada;
    }

    public void setParada(ParadaItinerarioBairro parada) {
        this.parada = parada;
    }

    public LiveData<List<ParadaBairro>> getParadas() {
        return paradas;
    }

    public void setParadas(LiveData<List<ParadaBairro>> paradas) {
        this.paradas = paradas;
    }

    public RegistraViagemViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerario = appDatabase.itinerarioDAO().carregar("");
        paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro("");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());

        localAtual = new MutableLiveData<>();
        localAtual.setValue(new Location(LocationManager.GPS_PROVIDER));

        retorno = new MutableLiveData<>();
    }

    // fim editar

    public void iniciarAtualizacoesPosicao(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    if(location.getAccuracy() <= 20){
                        localAtual.setValue(location);

                        if(localAtual.getValue() != null){
                            localAtual.getValue().setLatitude(localAtual.getValue().getLatitude());
                            localAtual.getValue().setLongitude(localAtual.getValue().getLongitude());
                        }

                    }

                }
            }
        };
    }

    public void carregaDirections(MapView map, List<GeoPoint> paradas) {

        new directionsAsyncTask(map, paradas, getApplication().getApplicationContext()).execute();
    }

    private static class directionsAsyncTask extends AsyncTask<String, Void, Void> {

        MapView map;
        List<GeoPoint> pontos;
        Polyline rota;
        Context ctx;

        directionsAsyncTask(MapView map, List<GeoPoint> pontos, Context ctx) {
            this.map = map;
            this.pontos = pontos;
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(final String... params) {
            RoadManager roadManager = new OSRMRoadManager(ctx);

            ArrayList<GeoPoint> points = new ArrayList<>();

            for(GeoPoint g : pontos){
                points.add(g);
            }

            Road road = roadManager.getRoad(points);
            rota = RoadManager.buildRoadOverlay(road);

            KmlDocument kml = new KmlDocument();
            kml.mKmlRoot.addOverlay(rota, kml);
            File localFile = kml.getDefaultPathForAndroid("kml_teste.kml");
            kml.saveAsKML(localFile);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            map.getOverlays().add(rota);
            map.invalidate();
        }
    }

    public void salvarViagem(ViagemItinerario viagemItinerario){

        if(viagemItinerario.valida(viagemItinerario)){
            add(viagemItinerario);
        } else{
            retorno.setValue(0);
        }

    }

    // adicionar

    public void add(final ViagemItinerario viagemItinerario) {

        viagemItinerario.setDataCadastro(new DateTime());
        viagemItinerario.setUltimaAlteracao(new DateTime());
        viagemItinerario.setEnviado(false);
        viagemItinerario.setAtivo(true);

        new addAsyncTask(appDatabase).execute(viagemItinerario);
    }

    private static class addAsyncTask extends AsyncTask<ViagemItinerario, Void, Void> {

        private AppDatabase db;
        private boolean valido = false;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ViagemItinerario... params) {

            db.viagemItinerarioDAO().inserir(params[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim adicionar

}
