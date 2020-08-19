package br.com.vostre.circular.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityAcompanharViagemBinding;
import br.com.vostre.circular.databinding.ActivityFavoritosBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.utils.SessionUtils;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.form.FormParada;
import br.com.vostre.circular.viewModel.AcompanharViagemViewModel;
import br.com.vostre.circular.viewModel.BaseViewModel;
import br.com.vostre.circular.viewModel.MapaViewModel;

public class AcompanharViagemActivity extends BaseActivity {

    ActivityAcompanharViagemBinding binding;
    private FirebaseAuth mAuth;
    AcompanharViagemViewModel viewModel;

    AppCompatActivity ctx;

    MapView map;
    IMapController mapController;
    MyLocationNewOverlay mLocationOverlay;

    int permissionGPS;
    int permissionStorage;
    int permissionInternet;

    NumberFormat nf = NumberFormat.getNumberInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_acompanhar_viagem);
        binding.setView(this);
        super.onCreate(savedInstanceState);
        setTitle("Acompanhar Viagem");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        ctx = this;
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if(report.areAllPermissionsGranted()){
                    configuraActivity();
                } else{
                    Toast.makeText(getApplicationContext(), "Acesso ao GPS é necessário para o " +
                            "mapa funcionar corretamente! Acesso ao armazenamento externo é utilizado para fazer " +
                            "cache de partes do mapa e permitir o acesso offline.", Toast.LENGTH_LONG).show();
                    finish();
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        })
                .check();

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissionInternet = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET);

        if(!checarPermissoes()){
            configuraActivity();
        }

        ocultaModalLoading();

    }

    private void configuraActivity(){
        viewModel = ViewModelProviders.of(this).get(AcompanharViagemViewModel.class);

        String itinerario = getIntent().getStringExtra("itinerario");

        viewModel.setItinerario(itinerario);
        viewModel.paradas.observe(this, paradasObserver);
//        viewModel.pois.observe(this, poisObserver);
        viewModel.localAtual.observe(this, localObserver);
        viewModel.iniciarAtualizacoesPosicao();

        if(!gpsAtivo){
            binding.textViewGps.setVisibility(View.VISIBLE);
        } else{
            binding.textViewGps.setVisibility(View.GONE);
        }

        configuraMapa();

        binding.textViewGps.getViewTreeObserver().addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                YoYo.with(Techniques.Swing)
                        .duration(700)
                        .repeat(1)
                        .playOn(findViewById(R.id.textViewGps));

//                    View v = findViewById(R.id.textView66);
//
//                    YoYo.with(Techniques.Flash)
//                            .delay(500)
//                            .duration(500)
//                            .playOn(findViewById(R.id.textView66));
            }
        });

        binding.textViewVelocidade.setText("0 Km/h");

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

    private void configuraMapa() {
        map = binding.map;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);

        map.setFlingEnabled(false);

        mLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(getApplicationContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        mapController = map.getController();
        mapController.setZoom(19d);
//        GeoPoint startPoint = new GeoPoint(-22.470804460339885, -43.82463455200195);
//        mapController.setCenter(startPoint);

        map.setMaxZoomLevel(19d);
        map.setMinZoomLevel(8d);

        startLocationUpdates();
    }

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            atualizarParadasMapa(paradas);
            ocultaModalLoading();
        }
    };

    Observer<Location> localObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location local) {

            if(viewModel.centralizaMapa && local.getLatitude() != 0.0 && local.getLongitude() != 0.0){
                setMapCenter(map, new GeoPoint(local.getLatitude(), local.getLongitude()));
                binding.textViewGps.setVisibility(View.GONE);

                String velocidadeFormatada = nf.format(local.getSpeed() * 3.6);

                binding.textViewVelocidade.setText(velocidadeFormatada+" Km/h");

                //viewModel.centralizaMapa = false;
            }

        }
    };

    @BindingAdapter("center")
    public static void setMapCenter(MapView map, GeoPoint geoPoint){

        if(geoPoint != null){
            map.getController().setCenter(geoPoint);
        }

    }

    private void atualizarParadasMapa(final List<ParadaBairro> paradas){

        if(paradas != null){

            RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(this);
            poiMarkers.setRadius(200);

            Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_cluster);
            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

            map.getOverlays().add(poiMarkers);
            poiMarkers.setIcon(clusterIcon);

            for(final ParadaBairro p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setTitle(p.getParada().getNome());
                m.setDraggable(false);
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker_ponto));

                m.setId(p.getParada().getId());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        return false;
                    }
                });
//                map.getOverlays().add(m);
                poiMarkers.add(m);
            }

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

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        if(checarPermissoes()){

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setMaxWaitTime(5000);

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
        }

    }

    private boolean checarPermissoes(){

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissionStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return permissionGPS == PackageManager.PERMISSION_GRANTED && permissionStorage == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(map != null){
            map.onResume();
        }

        startLocationUpdates();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(map != null){
            map.onPause();
        }

        stopLocationUpdates();

    }

    @Override
    public void onGpsChanged(boolean ativo) {

        if(!ativo){
            binding.textViewGps.setText(R.string.text_gps);
            binding.textViewGps.setVisibility(View.VISIBLE);
            binding.map.setEnabled(false);
        } else{
//            binding.textViewGps.setVisibility(View.GONE);
            binding.textViewGps.setText(R.string.text_procurando_gps);
            binding.map.setEnabled(true);
        }

    }

    private void geraModalLoading() {
        binding.fundo.setVisibility(View.VISIBLE);
        binding.textViewCarregando.setVisibility(View.VISIBLE);
        binding.progressBar.setIndeterminate(true);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void ocultaModalLoading(){
        binding.fundo.setVisibility(View.GONE);
        binding.textViewCarregando.setVisibility(View.GONE);
        binding.progressBar.setIndeterminate(true);
        binding.progressBar.setVisibility(View.GONE);
    }

}