package br.com.vostre.circular.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalhesItinerarioBinding;
import br.com.vostre.circular.databinding.ActivityRegistraViagemBinding;
import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.utils.FileUtils;
import br.com.vostre.circular.utils.LocationRequestHelper;
import br.com.vostre.circular.utils.LocationResultHelper;
import br.com.vostre.circular.utils.LocationUpdateUtils;
import br.com.vostre.circular.utils.LocationUpdatesBroadcastReceiver;
import br.com.vostre.circular.utils.LocationUpdatesIntentService;
import br.com.vostre.circular.utils.LocationUtils;
import br.com.vostre.circular.view.adapter.ParadaAdapter;
import br.com.vostre.circular.view.adapter.ParadaItinerarioAdapter;
import br.com.vostre.circular.view.form.FormHistorico;
import br.com.vostre.circular.view.form.FormItinerario;
import br.com.vostre.circular.view.utils.SortListItemHelper;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;
import br.com.vostre.circular.viewModel.RegistraViagemViewModel;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static br.com.vostre.circular.utils.LocationResultHelper.KEY_LISTA;
import static br.com.vostre.circular.utils.LocationUpdatesBroadcastReceiver.KEY_GRAVANDO;

public class RegistraViagemActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    ActivityRegistraViagemBinding binding;
    MapView map;
    IMapController mapController;

    RecyclerView listParadas;
    ParadaAdapter adapter;

    int permissionGPS;
    int permissionStorage;
    int permissionInternet;

    AppCompatActivity ctx;
    MyLocationNewOverlay mLocationOverlay;
    MapEventsOverlay overlayEvents;

    RegistraViagemViewModel viewModel;

    long ultimaAtualizacao = -1;
    List<Location> locais;
    boolean gravando = false;

    Polyline line;
    NumberFormat nf = NumberFormat.getNumberInstance();

    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registra_viagem);
        super.onCreate(savedInstanceState);

        MultiplePermissionsListener listener = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(this)
                .withTitle("Permissões Negadas")
                .withMessage("Permita os acessos à Internet, armazenamento externo e GPS para acessar esta página")
                .build();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(listener)
                .check();

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        permissionStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionInternet = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET);

        if(!checarPermissoes()){

//            finish();
            Toast.makeText(getApplicationContext(),
                    "Para acessar esta tela, por favor aceite as permissões de acesso.",
                    Toast.LENGTH_SHORT).show();

        } else{
            binding.setView(this);
            setTitle("Registrar Viagem");
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            viewModel = ViewModelProviders.of(this).get(RegistraViagemViewModel.class);

            viewModel.setItinerario(getIntent().getStringExtra("itinerario"));

            viewModel.itinerario.observe(this, itinerarioObserver);
            viewModel.paradas.observe(this, paradasObserver);
            viewModel.localAtual.observe(this, localObserver);

            binding.setViewModel(viewModel);

            buildGoogleApiClient();

            configuraMapa();

            atualizarParadasMapa(viewModel.paradas.getValue());

            //viewModel.iniciarAtualizacoesPosicao();

            ctx = this;

            MapEventsReceiver receiver = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {
                    return false;
                }

                @Override
                public boolean longPressHelper(GeoPoint p) {
                    return false;
                }
            };

            overlayEvents = new MapEventsOverlay(getBaseContext(), receiver);

            listParadas = binding.listParadas;
            listParadas.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            adapter = new ParadaAdapter(viewModel.paradas.getValue(), this);

            listParadas.setAdapter(adapter);

            nf.setMaximumFractionDigits(0);
            nf.setMinimumFractionDigits(0);

            carregaGeoJson(getIntent().getStringExtra("itinerario"));

        }
    }

    private void carregaGeoJson(String itinerario){
        File[] s = getApplicationContext().getFilesDir().listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File file, String s) {
                return file.getName().endsWith(".json");
            }
        });

        for(File b : s){
            System.out.println(b.getName());
        }
    }

    private void configuraMapa() {
        map = binding.map;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(getApplicationContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        mapController = map.getController();
        mapController.setZoom(19);

        map.setMaxZoomLevel(19d);
        map.setMinZoomLevel(9d);

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
    }

    public void onClickBtnIniciar(View v){

        if(gravando){
            gravando = false;

            binding.btnIniciar.setText("Iniciar");

            salvarPolyline(line);

            map.getOverlays().clear();
            map.invalidate();

            Toast.makeText(getApplicationContext(), "Registro Salvo!", Toast.LENGTH_SHORT).show();

            binding.textViewVelocidade.setText("0 Km/h");

            //viewModel.carregaDirections(map, line.getPoints());

            atualizarParadasMapa(viewModel.paradas.getValue());

            LocationResultHelper.marcaGravando(getApplicationContext(), gravando);

        } else{
            gravando = true;
            locais = new ArrayList<>();
            binding.btnIniciar.setText("Parar");
            line = new Polyline();
            line.getOutlinePaint().setColor(Color.BLUE);

            LocationResultHelper.clearRoute(getApplicationContext());
            LocationResultHelper.marcaGravando(getApplicationContext(), gravando);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Boolean ativo = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean(KEY_GRAVANDO, false);

        if(map != null && ativo){
            // limpando e reiniciando mapa
            recarregaPolyline();

        }

//        updateButtonsState(LocationRequestHelper.getRequesting(this));
        //Toast.makeText(getApplicationContext(), LocationResultHelper.getSavedLocationResult(this), Toast.LENGTH_SHORT).show();
    }

    private void recarregaPolyline() {
        map.getOverlays().clear();
        map.invalidate();

        atualizarParadasMapa(viewModel.paradas.getValue());

        // carregando rota
        String rota = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(KEY_LISTA, "");

        if(!rota.isEmpty()){
            rota = rota.substring(0, rota.length()-1);
            line = new Polyline();

            String[] pontos = rota.split("\\|");

            for(String p : pontos){

                String[] ponto = p.split(";");

                GeoPoint g = new GeoPoint(Float.parseFloat(ponto[0]), Float.parseFloat(ponto[1]));

                line.addPoint(g);
            }

            map.getOverlays().add(line);
            map.invalidate();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(map != null){
            map.onPause();
        }

//        PreferenceManager.getDefaultSharedPreferences(this)
//                .unregisterOnSharedPreferenceChangeListener(this);

        //stopLocationUpdates();

    }

    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

        super.onStop();
    }

    /**
     * Builds {@link GoogleApiClient}, enabling automatic lifecycle management using
     * {@link GoogleApiClient.Builder#enableAutoManage(android.support.v4.app.FragmentActivity,
     * int, GoogleApiClient.OnConnectionFailedListener)}. I.e., GoogleApiClient connects in
     * {@link AppCompatActivity#onStart}, or if onStart() has already happened, it connects
     * immediately, and disconnects automatically in {@link AppCompatActivity#onStop}.
     */
    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("API", "GoogleApiClient connected");
        requestLocationUpdates(null);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(5000);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        //mLocationRequest.setMaxWaitTime(5000);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if(map != null){
//            map.onResume();
//        }
//
//        startLocationUpdates();
//
//    }

//    @Override
//    protected void onStop() {
//        PreferenceManager.getDefaultSharedPreferences(this)
//                .unregisterOnSharedPreferenceChangeListener(this);
//        super.onStop();
//    }

    private PendingIntent getPendingIntent() {
        // Note: for apps targeting API level 25 ("Nougat") or lower, either
        // PendingIntent.getService() or PendingIntent.getBroadcast() may be used when requesting
        // location updates. For apps targeting API level O, only
        // PendingIntent.getBroadcast() should be used. This is due to the limits placed on services
        // started in the background in "O".

        // TODO(developer): uncomment to use PendingIntent.getService().
//        Intent intent = new Intent(this, LocationUpdatesIntentService.class);
//        intent.setAction(LocationUpdatesIntentService.ACTION_PROCESS_UPDATES);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        final String text = "Connection suspended";
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        final String text = "Exception while connecting to Google Play services";
        Log.w("CONN_ERR", text + ": " + connectionResult.getErrorMessage());
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        if (s.equals(LocationResultHelper.KEY_LOCATION_UPDATES_RESULT)) {

            String[] coords = LocationResultHelper.getSavedLocationResult(this).split(";");

            if(Double.parseDouble(coords[2]) < 20){
                Location l = new Location(LocationManager.GPS_PROVIDER);
                l.setLatitude(Double.parseDouble(coords[0]));
                l.setLongitude(Double.parseDouble(coords[1]));
                l.setAccuracy(Float.parseFloat(coords[2]));
                l.setSpeed(Float.parseFloat(coords[3]));
                l.setTime(Long.parseLong(coords[4]));

                viewModel.localAtual.postValue(l);
            }


//            Toast.makeText(getApplicationContext(), "LOCAL: "+LocationResultHelper.getSavedLocationResult(this), Toast.LENGTH_SHORT).show();
        } else if (s.equals(LocationRequestHelper.KEY_LOCATION_UPDATES_REQUESTED)) {
//            Toast.makeText(getApplicationContext(), "REQ: "+LocationResultHelper.getSavedLocationResult(this), Toast.LENGTH_SHORT).show();
//            updateButtonsState(LocationRequestHelper.getRequesting(this));
        }
    }

    /**
     * Handles the Request Updates button and requests start of location updates.
     */
    public void requestLocationUpdates(View view) {
        try {
            Log.i("UPD", "Starting location updates");
            LocationUpdateUtils.setRequestingLocationUpdates(this, true);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            LocationUpdateUtils.setRequestingLocationUpdates(this, false);
            e.printStackTrace();
        }
    }

//    /**
//     * Handles the Request Updates button and requests start of location updates.
//     */
//    public void requestLocationUpdates(FusedLocationProviderClient mFusedLocationClient, LocationRequest mLocationRequest, LocationCallback mLocationCallback) {
//        try {
//            Log.i("LOC", "Starting location updates");
//            LocationUpdateUtils.setRequestingLocationUpdates(this, true);
//            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
//        } catch (SecurityException e) {
//            LocationUpdateUtils.setRequestingLocationUpdates(this, false);
//            e.printStackTrace();
//        }
//    }

    /**
     * Handles the Remove Updates button, and requests removal of location updates.
     */
    public void removeLocationUpdates() {
        Log.i("LOC", "Removing location updates");
        LocationRequestHelper.setRequesting(this, false);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                getPendingIntent());
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        if(checarPermissoes()){

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setMaxWaitTime(5000);
            locationRequest.setSmallestDisplacement(50);

            if(viewModel != null){
                //requestLocationUpdates(viewModel.mFusedLocationClient, locationRequest, viewModel.mLocationCallback);
            }

            if(viewModel != null){
                viewModel.mFusedLocationClient.requestLocationUpdates(locationRequest,
                        viewModel.mLocationCallback,
                        null);
            }

        }

    }

    private void stopLocationUpdates() {

        if(viewModel != null && viewModel.mFusedLocationClient != null){
            viewModel.mFusedLocationClient.removeLocationUpdates(viewModel.mLocationCallback);
            viewModel.mFusedLocationClient.removeLocationUpdates(getPendingIntent());
        }

    }

    private boolean checarPermissoes(){

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        permissionStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionInternet = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET);

        return permissionGPS == PackageManager.PERMISSION_GRANTED
                && permissionStorage == PackageManager.PERMISSION_GRANTED
                && permissionInternet == PackageManager.PERMISSION_GRANTED;
    }

    private void atualizarParadasMapa(final List<ParadaBairro> paradas){

        if(paradas != null){

            for(final ParadaBairro p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
                m.setTitle(p.getParada().getNome());
                m.setDraggable(false);
                m.setId(p.getParada().getId());
                map.getOverlays().add(m);
            }

        }

    }

    private void adicionaLocalMapa(Location local){

        if(local != null){

            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(local));
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
            m.setDraggable(false);
            map.getOverlays().add(m);

        }

    }

    @NonNull
    private ParadaBairro getParadaFromMarker(Marker marker, List<ParadaBairro> paradas) {
        Parada p = new Parada();
        p.setId(marker.getId());

        ParadaBairro parada = new ParadaBairro();
        parada.setParada(p);

        ParadaBairro pb = paradas.get(paradas.indexOf(parada));
        pb.getParada().setLatitude(marker.getPosition().getLatitude());
        pb.getParada().setLongitude(marker.getPosition().getLongitude());
        return pb;
    }

    @BindingAdapter("text")
    public static void setText(TextView view, DateTime date) {

        if(date != null){
            String formatted = DateTimeFormat.forPattern("HH:mm:ss").print(date);
            view.setText(formatted);
        } else{
            view.setText("-");
        }


    }

    @BindingAdapter("text")
    public static void setText(TextView view, Double value) {

        if(value == null){
            view.setText("0 Km");
        } else{

            try{
                String valor = String.valueOf(value).replace(".", ",");
                view.setText(valor+" Km");
            } catch(NumberFormatException e){
                view.setText("0 Km");
            }

        }


    }

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(final List<ParadaBairro> paradas) {
            map.getOverlays().clear();
            map.getOverlays().add(mLocationOverlay);
            map.getOverlays().add(overlayEvents);
            atualizarParadasMapa(paradas);

            if(viewModel.localAtual.getValue().distanceTo(viewModel.paradas.getValue().get(0).getLocation()) > 200){
                Toast.makeText(getApplicationContext(), "Você está longe demais! "+viewModel.paradas.getValue().get(0).getLocation().getLatitude()+" | "
                        +viewModel.paradas.getValue().get(0).getLocation().getLongitude(), Toast.LENGTH_SHORT).show();
            }

            adapter.paradas = paradas;
            adapter.notifyDataSetChanged();

            //viewModel.carregaDirections(map, paradas);

//            List<Overlay> ov = map.getOverlays().;
//            System.out.println(ov.size());
        }
    };

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {
            binding.setItinerario(itinerario);
        }
    };

    Observer<Location> localObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location local) {

            if(local.getLatitude() != 0.0 && local.getLongitude() != 0.0){
                setMapCenter(map, new GeoPoint(local.getLatitude(), local.getLongitude()));
            }

            if(gravando && locais != null){

                locais.add(local);

                //adicionaLocalMapa(local);

                atualizaPolyline(local);

            }



        }
    };

    public void atualizaPolyline(Location local){

        if(local.hasAccuracy() && local.getAccuracy() < 20){

            //Toast.makeText(getApplicationContext(), "Vel.: "+local.getSpeed() * 3.6+" Km/h", Toast.LENGTH_SHORT).show();

            String velocidadeFormatada = nf.format(local.getSpeed() * 3.6);

            binding.textViewVelocidade.setText(velocidadeFormatada+" Km/h");

            line.addPoint(new GeoPoint(local.getLatitude(), local.getLongitude()));
            map.getOverlays().add(line);
            map.invalidate();
        }

    }

    private void salvarPolyline(Polyline line){

        String geoJson = "{\"type\": \"FeatureCollection\",\"features\": [{\"type\": \"Feature\", \"properties\": {},\"geometry\": {\"type\": \"LineString\", \"coordinates\": [";

        for(GeoPoint geo : line.getPoints()){
            geoJson = geoJson.concat("["+geo.getLongitude()+","+geo.getLatitude()+"],");
        }

        geoJson = geoJson.substring(0, geoJson.length()-1);

        geoJson = geoJson.concat("]}}]}");

        String nomeArquivo = viewModel.itinerario.getValue().getItinerario().getId()
                +"-"+DateTimeFormat.forPattern("yyyy-MM-dd-HH:mm:ss").print(DateTime.now())+".json";

        FileUtils.writeToFile(nomeArquivo, geoJson, getApplicationContext());

        salvarRegistroPontos(nomeArquivo.replace("json", "log"));

    }

    private void salvarRegistroPontos(String nome){

        String pontos = "";

        for(Location geo : locais){
            pontos = pontos.concat(DateTimeFormat.forPattern("yyyy-MM-dd-HH:mm:ss").print(geo.getTime())
                    +","+geo.getLongitude()+","+geo.getLatitude()+","+geo.getSpeed()*3.6+" Km/h"+"\n");
        }

        FileUtils.writeToFile(nome, pontos, getApplicationContext());

    }

    @BindingAdapter("center")
    public static void setMapCenter(MapView map, GeoPoint geoPoint){

        if(geoPoint != null){
            map.getController().setCenter(geoPoint);
        }

    }

}
