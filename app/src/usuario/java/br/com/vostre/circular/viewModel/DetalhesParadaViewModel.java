package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.utils.StringUtils;

public class DetalhesParadaViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<ParadaBairro> parada;
    public ParadaBairro umaParada;
    public MutableLiveData<Location> localAtual;

    public ParadaBairro paradaBairro;
    public PontoInteresse pontoInteresse;

    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;
    public LiveData<List<PontoInteresse>> pois;

    public Bitmap foto;

    public ParadaBairro getParadaBairro() {
        return paradaBairro;
    }

    public void setParadaBairro(ParadaBairro paradaBairro) {
        this.paradaBairro = paradaBairro;
    }

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

    public void setParada(String parada) {
        //foto = BitmapFactory.decodeFile(parada.getParada().getImagem());
        this.parada = appDatabase.paradaDAO().carregarComBairro(parada);
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada, DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime()));
        pois = appDatabase.pontoInteresseDAO().listarTodosAtivosProximos(0, 0, 0, 0);
    }

    public LiveData<ParadaBairro> getParada() {
        return parada;
    }

    public DetalhesParadaViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        parada = appDatabase.paradaDAO().carregarComBairro("");
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario("", "");
        localAtual = new MutableLiveData<>();
    }

    @BindingAdapter("srcCompat")
    public static void setImagemFoto(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        } else{
            imageView.setBackgroundResource(R.mipmap.ic_onibus_azul);
        }

    }

    public void atualizaPontoMapa(){
        midPoint(parada.getValue().getParada().getLatitude(), parada.getValue().getParada().getLongitude(),
                pontoInteresse.getLatitude(), pontoInteresse.getLongitude());
    }

    public void midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        GeoPoint origin = new GeoPoint(lat1,lon1);
        //create destination geopoints from parameters
        GeoPoint destination = new GeoPoint(lat2,lon2);
        //calculate and return center
        GeoPoint point = GeoPoint.fromCenterBetween(origin, destination);

        Location l = new Location(LocationManager.GPS_PROVIDER);

        l.setLatitude(point.getLatitude());
        l.setLongitude(point.getLongitude());

        localAtual.postValue(l);

    }

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

    public void carregarItinerarios(String parada){
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada, DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime()));
    }

    public void buscaPoisProximos(Location local){

        double latitude = local.getLatitude();
        double longitude = local.getLongitude();

//        isRunningNearPlaces = true;

        // Centro - Barra do Pirai
        //double latitude = -22.470612;
        //double longitude = -43.8263613;

        int raioEmMetros = 500;

// 6378000 Size of the Earth (in meters)
        double longitudeD = (Math.asin(raioEmMetros / (6378000 * Math.cos(Math.PI*latitude/180))))*180/Math.PI;
        double latitudeD = (Math.asin((double)raioEmMetros / (double)6378000))*180/Math.PI;

        double latitudeMax = latitude+(latitudeD);
        double latitudeMin = latitude-(latitudeD);
        double longitudeMax = longitude+(longitudeD);
        double longitudeMin = longitude-(longitudeD);

        pois = appDatabase.pontoInteresseDAO().listarTodosAtivosProximos(latitudeMin, latitudeMax, longitudeMin, longitudeMax);

        //new buscaAsyncTask(appDatabase, local, this).execute();
    }

}
