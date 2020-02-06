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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityParadasBinding;
import br.com.vostre.circular.listener.ParadaListener;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.DialogUtils;
import br.com.vostre.circular.utils.DrawableUtils;
import br.com.vostre.circular.view.adapter.ParadaAdapter;
import br.com.vostre.circular.view.adapter.ParametroAdapter;
import br.com.vostre.circular.view.form.FormParada;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.viewModel.ParadasViewModel;

public class ParadasActivity extends BaseActivity implements ParadaListener {

    ActivityParadasBinding binding;
    MapView map;
    IMapController mapController;

    ParadasViewModel viewModel;

    int permissionGPS;
    int permissionStorage;
    int permissionInternet;

    AppCompatActivity ctx;
    MyLocationNewOverlay mLocationOverlay;
    MapEventsOverlay overlayEvents;

    FormParada formParada;

    RecyclerView listParadas;
    List<ParadaBairro> paradas;
    ParadaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_paradas);
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
            setTitle("Paradas");
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            viewModel = ViewModelProviders.of(this).get(ParadasViewModel.class);
            viewModel.paradas.observe(this, paradasObserver);

            viewModel.localAtual.observe(this, centroObserver);

            listParadas = binding.listParadas;

            adapter = new ParadaAdapter(paradas, this);
            adapter.setListener(this);

            listParadas.setAdapter(adapter);

            configuraMapa();

            viewModel.centralizaMapa = true;

            atualizarParadasMapa(viewModel.paradas.getValue());

            viewModel.iniciarAtualizacoesPosicao();

            MapEventsReceiver receiver = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {
                    viewModel.parada = new ParadaBairro();
                    formParada = new FormParada();
                    formParada.setLatitude(p.getLatitude());
                    formParada.setLongitude(p.getLongitude());
                    formParada.setParada(null);
                    formParada.setCtx(getApplication());
                    formParada.show(getSupportFragmentManager(), "formParada");

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
            viewModel.parada = new ParadaBairro();
            formParada = new FormParada();
            formParada.setLatitude(viewModel.localAtual.getValue().getLatitude());
            formParada.setLongitude(viewModel.localAtual.getValue().getLongitude());
            formParada.setParada(null);
            formParada.setCtx(getApplication());
            formParada.show(getSupportFragmentManager(), "formParada");
        }

    }

    public void onFabSugestaoClick(View v){
        Intent i = new Intent(this, ParadasSugeridasActivity.class);
        startActivity(i);
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

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            map.getOverlays().clear();
            map.getOverlays().add(mLocationOverlay);
            map.getOverlays().add(overlayEvents);
            atualizarParadasMapa(paradas);

            adapter.paradas = paradas;
            adapter.notifyDataSetChanged();

//            int cont = 0;
//            final Handler handler = new Handler();
//            int segundos = 20;
//
//            for(final ParadaBairro p : paradas){
//
//                if((p.getParada().getRua() == null || p.getParada().getRua().isEmpty()) && cont < 50){
//
//                    segundos = segundos+(cont+5);
//
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            viewModel.buscarRua(p.getParada());
//                        }
//                    }, segundos*1000);
//
//                    cont++;
//                }
//
//
//
//            }

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

    private void atualizarParadasMapa(final List<ParadaBairro> paradas){

        if(paradas != null){

            this.paradas = paradas;

            RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(this);
            poiMarkers.setRadius(200);

            Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_cluster);
            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

            map.getOverlays().add(poiMarkers);
            poiMarkers.setIcon(clusterIcon);

            for(ParadaBairro p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setDragOffset(10);
                m.setTitle(p.getParada().getNome());

                if(p.getParada().getAtivo()){

                    switch(p.getParada().getSentido()){
                        case 0:
                            m.setIcon(br.com.vostre.circular.utils.DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.circle_button_dia));
                            break;
                        case 1:
                            m.setIcon(br.com.vostre.circular.utils.DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.circle_button_dia_off));
                            break;
                        default:
                            m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
                            break;
                    }

                } else{

                    switch(p.getParada().getSentido()){
                        case 0:
                            m.setIcon(DrawableUtils.convertToGrayscale(br.com.vostre.circular.utils.DrawableUtils.
                                    mergeDrawable(this, R.drawable.marker, R.drawable.circle_button_dia).mutate()));
                            break;
                        case 1:
                            m.setIcon(DrawableUtils.convertToGrayscale(br.com.vostre.circular.utils.DrawableUtils.
                                    mergeDrawable(this, R.drawable.marker, R.drawable.circle_button_dia_off).mutate()));
                            break;
                        default:
                            m.setIcon(DrawableUtils.convertToGrayscale(getApplicationContext().getResources().getDrawable(R.drawable.marker).mutate()));
                            break;
                    }

                }



                m.setDraggable(true);
                m.setId(p.getParada().getId());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        ParadaBairro pb = getParadaFromMarker(marker, paradas);

                        InfoWindow infoWindow = new InfoWindow();
                        infoWindow.setParada(pb);
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

                        ParadaBairro pb = getParadaFromMarker(marker, paradas);
                        viewModel.buscarRua(pb.getParada());
                        viewModel.setParada(pb);
                        viewModel.editarParada();
                        Toast.makeText(getApplicationContext(), "Parada alterada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }
                });
                poiMarkers.add(m);
                //map.getOverlays().add(m);
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

    @NonNull
    private ParadaBairro getParadaPorId(String id) {
        List<Overlay> overlays = map.getOverlays();

        ParadaBairro p = new ParadaBairro();
        p.getParada().setId(id);

        p = paradas.get(paradas.indexOf(p));

        return p;

    }

    @BindingAdapter("center")
    public static void setMapCenter(MapView map, GeoPoint geoPoint){

        if(geoPoint != null){
            map.getController().setCenter(geoPoint);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        formParada = (FormParada) DialogUtils.getOpenedDialog(this);

        if (requestCode == FormParada.PICK_IMAGE) {

            if (data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    viewModel.foto = BitmapFactory.decodeStream(inputStream);
                    formParada.exibeImagem();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


        }

    }

    @Override
    public void onSelected(String id) {
        ParadaBairro parada = getParadaPorId(id);

        GeoPoint g = new GeoPoint(parada.getParada().getLatitude(), parada.getParada().getLongitude());

        mapController.animateTo(g);
    }
}
