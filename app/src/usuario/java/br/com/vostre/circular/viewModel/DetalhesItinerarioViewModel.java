package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.database.Observable;
import android.databinding.ObservableField;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;

public class DetalhesItinerarioViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<ItinerarioPartidaDestino> itinerario;

    public LiveData<List<ParadaBairro>> paradas;
    public LiveData<List<HorarioItinerarioNome>> horarios;
    public LiveData<List<SecaoItinerario>> secoes;
    public MutableLiveData<Location> localAtual;
    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    public static MutableLiveData<Integer> retorno;
    Location l;

    public void setItinerario(String itinerario) {
        this.itinerario = appDatabase.itinerarioDAO().carregar(itinerario);
        this.paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro(itinerario);
        this.horarios = appDatabase.horarioItinerarioDAO().listarApenasAtivosPorItinerario(itinerario);
        this.secoes = appDatabase.secaoItinerarioDAO().listarTodosPorItinerario(itinerario);
    }

    public LiveData<List<ParadaBairro>> getParadas() {
        return paradas;
    }

    public void setParadas(LiveData<List<ParadaBairro>> paradas) {
        this.paradas = paradas;
    }

    public DetalhesItinerarioViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerario = appDatabase.itinerarioDAO().carregar("");
        paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro("");
        horarios = appDatabase.horarioItinerarioDAO().listarApenasAtivosPorItinerario("");
        secoes = appDatabase.secaoItinerarioDAO().listarTodosPorItinerario("");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());

        localAtual = new MutableLiveData<>();

        l = new Location(LocationManager.NETWORK_PROVIDER);
        l.setLatitude(0);
        l.setLongitude(0);

        localAtual.setValue(l);

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

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

    public void atualizaPontoMapa(){
        List<ParadaBairro> p = paradas.getValue();
        ParadaBairro paradaInicial = p.get(0);
        ParadaBairro paradaFinal = p.get(p.size()-1);

        midPoint(paradaInicial.getParada().getLatitude(), paradaInicial.getParada().getLongitude(), paradaFinal.getParada().getLatitude(), paradaFinal.getParada().getLongitude());

    }

    private void midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        GeoPoint origin = new GeoPoint(lat1,lon1);
        //create destination geopoints from parameters
        GeoPoint destination = new GeoPoint(lat2,lon2);
        //calculate and return center
        GeoPoint point = GeoPoint.fromCenterBetween(origin, destination);

        l.setLatitude(point.getLatitude());
        l.setLongitude(point.getLongitude());

        localAtual.postValue(l);

    }

    public void carregaDirections(MapView map, List<ParadaBairro> paradas) {

        new directionsAsyncTask(map, paradas, getApplication().getApplicationContext()).execute();
    }

    private static class directionsAsyncTask extends AsyncTask<String, Void, Void> {

        MapView map;
        List<ParadaBairro> paradas;
        Polyline rota;
        Context ctx;

        directionsAsyncTask(MapView map, List<ParadaBairro> paradas, Context ctx) {
            this.map = map;
            this.paradas = paradas;
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(final String... params) {
            RoadManager roadManager = new OSRMRoadManager(ctx);

            ArrayList<GeoPoint> points = new ArrayList<>();
            points.add(new GeoPoint(paradas.get(0).getParada().getLatitude(), paradas.get(0).getParada().getLongitude()));
            points.add(new GeoPoint(paradas.get(paradas.size()-1).getParada().getLatitude(), paradas.get(paradas.size()-1).getParada().getLongitude()));

            Road road = roadManager.getRoad(points);
            rota = RoadManager.buildRoadOverlay(road);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            map.getOverlays().add(rota);
            map.invalidate();
        }
    }

}
