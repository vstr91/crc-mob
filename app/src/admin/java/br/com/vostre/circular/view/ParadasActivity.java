package br.com.vostre.circular.view;

import android.Manifest;
import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityParadasBinding;
import br.com.vostre.circular.listener.ParadaListener;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaBairroImport;
import br.com.vostre.circular.utils.DialogUtils;
import br.com.vostre.circular.utils.DrawableUtils;
import br.com.vostre.circular.view.adapter.ParadaAdapter;
import br.com.vostre.circular.view.form.FormParada;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.view.viewHolder.ImagemParadaViewHolder;
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

    List<ParadaBairroImport> paradasImport;
    Integer contParadaRepetida = 0;

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

            paradasImport = new ArrayList<>();

        }



    }

    public void processaParadasPorLote(final String arquivo, final List<ParadaBairroImport> paradas,
                                       List<ParadaBairro> paradasCadastradas) {
        try {

            BufferedReader r = new BufferedReader(
                    new InputStreamReader(getBaseContext().getAssets().open(arquivo)));
            StringBuilder dados = new StringBuilder();
            String[] line = new String[1];

            while ((line[0] = r.readLine()) != null) {

                String[] a = line[0].split("\t");

                String latitude = a[1].substring(0, 3).concat(".").concat(a[1].substring(3, 9));
                String longitude = a[2].substring(0, 3).concat(".").concat(a[2].substring(3, 9));

                ParadaBairroImport pbi = new ParadaBairroImport();
                Parada parada = new Parada();

                parada.setLatitude(Double.parseDouble(latitude));
                parada.setLongitude(Double.parseDouble(longitude));
                parada.setSentido(1); // sentido bairro
                parada.setImagemEnviada(true);
                parada.setAtivo(true);
                parada.setNome(a[0]+"-"+arquivo);

                pbi.setParada(parada);

                int index = paradas.indexOf(pbi);

                if(index > -1){
                    contParadaRepetida++;
                    System.out.println("PARADA JÁ EXISTE!!! "+contParadaRepetida+" - "+parada.getNome()
                            +" | "+paradas.get(index).getParada().getNome());
                } else{
                    paradas.add(pbi);
                }

            }

//            Toast.makeText(ctx, "Finalizou!", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void filtraParadasProximas(List<ParadaBairroImport> paradas, List<ParadaBairro> paradasCadastradas) {

        List<ParadaBairroImport> pbiRemover = new ArrayList<>();

        for(ParadaBairroImport pbi2 : paradas){

            GeoPoint g2 = new GeoPoint(pbi2.getParada().getLatitude(), pbi2.getParada().getLongitude());

            for(ParadaBairro p : paradasCadastradas){
                GeoPoint g1 = new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude());

                System.out.println("DISTANCIA: "+g2.distanceToAsDouble(g1)+" - "
                        +pbi2.getParada().getNome()+" | "+p.getParada().getNome());

                if(g2.distanceToAsDouble(g1) < 20 /*&& (p.getParada().getSentido() == pbi2.getParada().getSentido())*/){
                    System.out.println("PARADA PROXIMA JÁ EXISTE!!! - "+pbi2.getParada().getNome()
                            +" | "+p.getParada().getNome());

                    pbi2.setDistancia(-1f);

//                    pbiRemover.add(pbi2);

                }

            }


        }

//        paradas.removeAll(pbiRemover);

        atualizarParadasImportMapa(paradasImport);

        System.out.println("TOTAL PARADAS IMPORTADAS POS: "+paradas.size());
    }

    private void configuraMapa() {

//        OnlineTileSourceBase MAPBOXSATELLITELABELLED = new MapBoxTileSource("MapBoxSatelliteLabelled", 1, 19, 256, ".png");
//        ((MapBoxTileSource) MAPBOXSATELLITELABELLED).retrieveAccessToken(this);
//        ((MapBoxTileSource) MAPBOXSATELLITELABELLED).retrieveMapBoxMapId(this);
//        TileSourceFactory.addTileSource(MAPBOXSATELLITELABELLED);

        map = binding.map;

        map.setTileSource(TileSourceFactory.MAPNIK);
//        map.setTileSource(MAPBOXSATELLITELABELLED);
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

        map.setMaxZoomLevel(25d);
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

    public void onBtnImportarClick(View v){
        Intent i = new Intent(this, ImportarParadasActivity.class);
        startActivity(i);
    }

    public void onBtnRuasClick(View v){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                viewModel.atualizarRuaParadas();
            }
        });

        Toast.makeText(ctx, "Buscando ruas...", Toast.LENGTH_SHORT).show();
    }

    public void onBtnFotosClick(View v){
        Intent i = new Intent(this, ImagensParadasActivity.class);
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

//            try {
//                String[] arquivos = getAssets().list("");
//
//                for(String a : arquivos){
//
//                    if(a.endsWith(".txt")){
//                        processaParadasPorLote(a, paradasImport, paradas);
//                    }
//
//                }
//
//                System.out.println("TOTAL PARADAS IMPORTADAS PRE: "+paradasImport.size());
//
//                filtraParadasProximas(paradasImport, paradas);
//
//                System.out.println(arquivos.length);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

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
                            m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker_centro));
                            break;
                        case 1:
                            m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker_bairro));
                            break;
                        default:
                            m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
                            break;
                    }

                } else{

                    switch(p.getParada().getSentido()){
                        case 0:
                            m.setIcon(DrawableUtils
                                    .convertToGrayscale(getApplicationContext().getResources().getDrawable(R.drawable.marker_centro).mutate()));
                            break;
                        case 1:
                            m.setIcon(DrawableUtils
                                    .convertToGrayscale(getApplicationContext().getResources().getDrawable(R.drawable.marker_bairro).mutate()));
                            break;
                        default:
                            m.setIcon(DrawableUtils
                                    .convertToGrayscale(getApplicationContext().getResources().getDrawable(R.drawable.marker).mutate()));
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
                        viewModel.editarParada(false);
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

    private void atualizarParadasImportMapa(final List<ParadaBairroImport> paradas){

        if(paradas != null){

            this.paradasImport = paradas;

            RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(this);
            poiMarkers.setRadius(200);

            Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_cluster);
            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

            map.getOverlays().add(poiMarkers);
            poiMarkers.setIcon(clusterIcon);

            for(ParadaBairroImport p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setDragOffset(10);
                m.setDraggable(false);
                m.setTitle(p.getParada().getNome());

                if(p.getDistancia() != null && p.getDistancia() == -1f){
                    m.setIcon(DrawableUtils.convertToRed(
                            getApplicationContext().getResources().getDrawable(R.drawable.marker_ponto_conflito_bairro)));
                } else{
                    m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker_ponto_bairro));
                }

//                if(p.getParada().getAtivo()){
//
//                    switch(p.getParada().getSentido()){
//                        case 0:
//                            m.setIcon(br.com.vostre.circular.utils.DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.centro));
//                            break;
//                        case 1:
//                            m.setIcon(br.com.vostre.circular.utils.DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.bairro));
//                            break;
//                        default:
//                            m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
//                            break;
//                    }
//
//                } else{
//
//                    switch(p.getParada().getSentido()){
//                        case 0:
//                            m.setIcon(DrawableUtils.convertToGrayscale(br.com.vostre.circular.utils.DrawableUtils.
//                                    mergeDrawable(this, R.drawable.marker, R.drawable.centro).mutate()));
//                            break;
//                        case 1:
//                            m.setIcon(DrawableUtils.convertToGrayscale(br.com.vostre.circular.utils.DrawableUtils.
//                                    mergeDrawable(this, R.drawable.marker, R.drawable.bairro).mutate()));
//                            break;
//                        default:
//                            m.setIcon(DrawableUtils.convertToGrayscale(getApplicationContext().getResources().getDrawable(R.drawable.marker).mutate()));
//                            break;
//                    }
//
//                }

                m.setId(p.getParada().getId());

                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        ParadaBairroImport pb = getParadaImportFromMarker(marker, paradasImport);

                        Toast.makeText(getApplicationContext(), pb.getParada().getNome()+" - "
                                +pb.getParada().getLatitude()+","+pb.getParada().getLongitude(), Toast.LENGTH_SHORT).show();

                        // STREET VIEW

                        // Create a Uri from an intent string. Use the result to create an Intent.
                        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+pb.getParada().getLatitude()+","+pb.getParada().getLongitude());

                        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        // Make the Intent explicit by setting the Google Maps package
                        mapIntent.setPackage("com.google.android.apps.maps");

                        // Attempt to start an activity that can handle the Intent
                        startActivity(mapIntent);

                        // STREET VIEW

                        // FORM

                        viewModel.parada = new ParadaBairro();
                        formParada = new FormParada();
                        formParada.setLatitude(pb.getParada().getLatitude());
                        formParada.setLongitude(pb.getParada().getLongitude());
                        formParada.setParada(null);
                        formParada.setCtx(getApplication());
                        formParada.show(getSupportFragmentManager(), "formParadaImport");

                        // FORM

//                        InfoWindow infoWindow = new InfoWindow();
//                        infoWindow.setParada(pb);
//                        infoWindow.setCtx(ctx);
//                        infoWindow.show(getSupportFragmentManager(), "infoWindow");
                        mapController.animateTo(marker.getPosition());
                        return true;
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
    private ParadaBairroImport getParadaImportFromMarker(Marker marker, List<ParadaBairroImport> paradas) {
        Parada p = new Parada();
        p.setId(marker.getId());

        ParadaBairroImport parada = new ParadaBairroImport();
        parada.setParada(p);

        ParadaBairroImport pb = paradas.get(paradas.indexOf(parada));
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
