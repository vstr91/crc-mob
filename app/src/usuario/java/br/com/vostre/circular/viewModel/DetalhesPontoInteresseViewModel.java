package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;
import android.content.Context;
import androidx.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseBairro;
import br.com.vostre.circular.utils.DataHoraUtils;
import br.com.vostre.circular.utils.LocationUtils;

public class DetalhesPontoInteresseViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<PontoInteresseBairro> poi;
    public ParadaBairro umPoi;
    public MutableLiveData<Location> localAtual;

    public ParadaBairro paradaBairro;
    public PontoInteresse pontoInteresse;

    public Bitmap foto;

    public LiveData<List<ParadaBairro>> paradas;
    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;

    public PontoInteresse getPontoInteresse() {
        return pontoInteresse;
    }

    public void setPontoInteresse(PontoInteresse pontoInteresse) {
        this.pontoInteresse = pontoInteresse;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public void setPontoInteresse(final String poi) {
        //foto = BitmapFactory.decodeFile(parada.getParada().getImagem());
        this.poi = appDatabase.pontoInteresseDAO().carregarComBairro(poi);
    }

    public DetalhesPontoInteresseViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        poi = appDatabase.pontoInteresseDAO().carregarComBairro("");
        localAtual = new MutableLiveData<>();
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorEmpresa("");
    }

    @BindingAdapter("srcCompat")
    public static void setImagemFoto(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        } else{
            imageView.setBackgroundResource(R.mipmap.ic_onibus_azul);
        }

    }

//    public void carregaParadaQRCode(String uf, String cidade, String bairro, String parada){
//        this.poi = appDatabase.pontoInteresseDAO().carregarComBairroPorUFCidadeEBairro(uf.toUpperCase(), cidade, bairro, parada);
//    }

//    public void carregarDadosVinculadosQRCode(String parada){
//        itinerarios.postValue(appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada, DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime())).getValue());
//    }

    public void carregaDirections(MapView map, ParadaBairro parada, PontoInteresse pontoInteresse) {

        new directionsAsyncTask(map, parada, pontoInteresse, getApplication().getApplicationContext()).execute();
    }

    private static class directionsAsyncTask extends AsyncTask<String, Void, Void> {

        MapView map;
        ParadaBairro parada;
        PontoInteresse pontoInteresse;
        Polyline rota;
        Context ctx;

        directionsAsyncTask(MapView map, ParadaBairro parada, PontoInteresse pontoInteresse, Context ctx) {
            this.map = map;
            this.parada = parada;
            this.pontoInteresse = pontoInteresse;
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(final String... params) {
            RoadManager roadManager = new OSRMRoadManager(ctx);

            ArrayList<GeoPoint> points = new ArrayList<>();
            points.add(new GeoPoint(parada.getParada().getLatitude(), parada.getParada().getLongitude()));
            points.add(new GeoPoint(pontoInteresse.getLatitude(), pontoInteresse.getLongitude()));

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

    public void buscarParadasProximas(Context ctx, Location location){

        paradas = LocationUtils.buscaParadasProximas(ctx, location, 200);

    }

    public void listarTodosAtivosProximosPoi(List<String> paradas){

        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosProximosPoi(paradas);

    }

}
