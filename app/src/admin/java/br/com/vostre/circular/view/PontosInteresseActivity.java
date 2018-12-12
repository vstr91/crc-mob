package br.com.vostre.circular.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.DrawableContainer;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityPontosInteresseBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.DialogUtils;
import br.com.vostre.circular.view.form.FormPontoInteresse;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.view.utils.InfoWindowPOI;
import br.com.vostre.circular.viewModel.PontosInteresseViewModel;

public class PontosInteresseActivity extends BaseActivity {

    ActivityPontosInteresseBinding binding;
    MapView map;
    IMapController mapController;

    PontosInteresseViewModel viewModel;

    int permissionGPS;
    int permissionStorage;
    int permissionInternet;

    AppCompatActivity ctx;
    MyLocationNewOverlay mLocationOverlay;
    MapEventsOverlay overlayEvents;

    FormPontoInteresse formPontoInteresse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_pontos_interesse);
        super.onCreate(savedInstanceState);

        MultiplePermissionsListener listener = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(this)
                .withTitle("Permissões Negadas")
                .withMessage("Permita os acessos à Internet, armazenamento externo e GPS para acessar esta página")
                .build();

        ctx = this;

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(listener)
                .check();

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
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
            setTitle("Pontos de Interesse");
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            viewModel = ViewModelProviders.of(this).get(PontosInteresseViewModel.class);
            viewModel.pontosInteresse.observe(this, pontosInteresseObserver);

            viewModel.paradas.observe(this, paradasObserver);

            viewModel.localAtual.observe(this, centroObserver);

            configuraMapa();

            viewModel.centralizaMapa = true;

            atualizarPontosInteresseMapa(viewModel.pontosInteresse.getValue());

            viewModel.iniciarAtualizacoesPosicao();

            MapEventsReceiver receiver = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {
//                    Toast.makeText(getBaseContext(),p.getLatitude() + " - "
//                            +p.getLongitude(),Toast.LENGTH_LONG).show();

                    formPontoInteresse = new FormPontoInteresse();
                    formPontoInteresse.setLatitude(p.getLatitude());
                    formPontoInteresse.setLongitude(p.getLongitude());
                    formPontoInteresse.setCtx(getApplication());
                    formPontoInteresse.setPontoInteresse(new PontoInteresse());
                    formPontoInteresse.show(getSupportFragmentManager(), "formPontoInteresse");

//                    Marker m = new Marker(map);
//                    m.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
//                    m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//                    map.getOverlays().add(m);

                    return false;
                }

                @Override
                public boolean longPressHelper(GeoPoint p) {
                    return false;
                }
            };

            overlayEvents = new MapEventsOverlay(getBaseContext(), receiver);

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
//        GeoPoint startPoint = new GeoPoint(-22.470804460339885, -43.82463455200195);
//        mapController.setCenter(startPoint);

        map.setMaxZoomLevel(19d);
        map.setMinZoomLevel(5d);
    }

    public void onFabClick(View v){

        if(viewModel.localAtual != null){
            formPontoInteresse = new FormPontoInteresse();
            formPontoInteresse.setLatitude(viewModel.localAtual.getValue().getLatitude());
            formPontoInteresse.setLongitude(viewModel.localAtual.getValue().getLongitude());
            formPontoInteresse.setCtx(getApplication());
            formPontoInteresse.show(getSupportFragmentManager(), "formPontoInteresse");
        }

    }

    public void onFabLocationClick(View v){
        mapController.animateTo(new GeoPoint(viewModel.localAtual.getValue().getLatitude(),
                viewModel.localAtual.getValue().getLongitude()));
    }

    public boolean onFabLocationLongClick(View v){
        viewModel.centralizaMapa = !viewModel.centralizaMapa;

        if(viewModel.centralizaMapa){
            mapController.animateTo(new GeoPoint(viewModel.localAtual.getValue().getLatitude(),
                    viewModel.localAtual.getValue().getLongitude()));
            v.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        } else{
            v.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY));
        }

        v.invalidate();

        return true;

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

    Observer<List<PontoInteresse>> pontosInteresseObserver = new Observer<List<PontoInteresse>>() {
        @Override
        public void onChanged(List<PontoInteresse> pontosInteresse) {
            map.getOverlays().clear();
            map.getOverlays().add(mLocationOverlay);
            map.getOverlays().add(overlayEvents);
            atualizarPontosInteresseMapa(pontosInteresse);
            atualizarParadasMapa(viewModel.paradas.getValue());
//            List<Overlay> ov = map.getOverlays().;
//            System.out.println(ov.size());
        }
    };

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            atualizarParadasMapa(paradas);
//            List<Overlay> ov = map.getOverlays().;
//            System.out.println(ov.size());
        }
    };

    Observer<Location> centroObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location centro) {

            if(viewModel.centralizaMapa && centro.getLatitude() != 0.0 && centro.getLongitude() != 0.0){
                setMapCenter(map, new GeoPoint(centro.getLatitude(), centro.getLongitude()));
                //viewModel.centralizaMapa = false;
            }

        }
    };

    private boolean checarPermissoes(){

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissionStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionInternet = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET);

        return permissionGPS == PackageManager.PERMISSION_GRANTED
                && permissionStorage == PackageManager.PERMISSION_GRANTED
                && permissionInternet == PackageManager.PERMISSION_GRANTED;
    }

    private void atualizarPontosInteresseMapa(final List<PontoInteresse> pontosInteresse){

        if(pontosInteresse != null){

            for(PontoInteresse p : pontosInteresse){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setTitle(p.getNome());
                m.setDraggable(true);
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.poi));
                m.setId(p.getId());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        PontoInteresse p = getPontoInteresseFromMarker(marker, pontosInteresse);

                        InfoWindowPOI infoWindow = new InfoWindowPOI();
                        infoWindow.setPontoInteresse(p);
                        infoWindow.setCtx(ctx);
                        infoWindow.show(getSupportFragmentManager(), "infoWindow");
                        mapController.animateTo(marker.getPosition());
                        return true;
                    }
                });
                m.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {

                        PontoInteresse p = getPontoInteresseFromMarker(marker, pontosInteresse);
                        viewModel.setPontoInteresse(p);
                        viewModel.editarPontoInteresse();
                        Toast.makeText(getApplicationContext(), "Ponto de Interesse alterado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }
                });
                map.getOverlays().add(m);
            }

        }

    }

    private void atualizarParadasMapa(final List<ParadaBairro> paradas){

        if(paradas != null){

            for(ParadaBairro p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                m.setTitle(p.getParada().getNome());
                m.setDraggable(false);
                m.setId(p.getParada().getId());
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        ParadaBairro p = getParadaFromMarker(marker, paradas);

                        InfoWindow infoWindow = new InfoWindow();
                        infoWindow.setParada(p);
                        infoWindow.setCtx(ctx);
                        infoWindow.exibeBotaoEditar = false;
                        infoWindow.show(getSupportFragmentManager(), "infoWindow");
                        mapController.animateTo(marker.getPosition());
                        return true;
                    }
                });
                map.getOverlays().add(m);
            }

        }

    }

    @NonNull
    private PontoInteresse getPontoInteresseFromMarker(Marker marker, List<PontoInteresse> pontosInteresse) {
        PontoInteresse p = new PontoInteresse();
        p.setId(marker.getId());


        p = pontosInteresse.get(pontosInteresse.indexOf(p));
        p.setLatitude(marker.getPosition().getLatitude());
        p.setLongitude(marker.getPosition().getLongitude());
        return p;
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

    @BindingAdapter("center")
    public static void setMapCenter(MapView map, GeoPoint geoPoint){

        if(geoPoint != null){
            map.getController().setCenter(geoPoint);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        formPontoInteresse = (FormPontoInteresse) DialogUtils.getOpenedDialog(this);

        if (requestCode == FormPontoInteresse.PICK_IMAGE) {

            if (data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    viewModel.foto = BitmapFactory.decodeStream(inputStream);
                    formPontoInteresse.exibeImagem();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


        }

    }

}
