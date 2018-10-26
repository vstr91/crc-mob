package br.com.vostre.circular.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityItinerariosBinding;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ParadaItinerarioAdapter;
import br.com.vostre.circular.view.form.FormItinerario;
import br.com.vostre.circular.view.form.FormParada;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.view.utils.SortListItemHelper;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;

public class ItinerariosActivity extends BaseActivity {

    ActivityItinerariosBinding binding;
    MapView map;
    IMapController mapController;

    RecyclerView listParadas;
    ParadaItinerarioAdapter adapter;

    RecyclerView listItinerarios;
    ItinerarioAdapter adapterItinerarios;

    int permissionGPS;
    int permissionStorage;
    int permissionInternet;

    AppCompatActivity ctx;
    MyLocationNewOverlay mLocationOverlay;
    MapEventsOverlay overlayEvents;

    ItinerariosViewModel viewModel;
    Empresa empresa;

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_itinerarios);
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
            setTitle("Itinerários");
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            viewModel = ViewModelProviders.of(this).get(ItinerariosViewModel.class);
            viewModel.paradas.observe(this, paradasObserver);
            viewModel.paradasItinerario.observe(this, paradasItinerarioObserver);
            viewModel.itinerarios.observe(this, itinerariosObserver);

            viewModel.localAtual.observe(this, centroObserver);

            if(getIntent().getStringExtra("empresa") != null){
                viewModel.setEmpresa(getIntent().getStringExtra("empresa"));
                viewModel.umaEmpresa.observe(this, empresaObserver);
            }

            configuraMapa();

            viewModel.centralizaMapa = true;

            atualizarParadasMapa(viewModel.paradas.getValue());

            viewModel.iniciarAtualizacoesPosicao();

            ctx = this;

            MapEventsReceiver receiver = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {

                    FormParada formParada = new FormParada();
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

            listParadas = binding.listParadas;
            listItinerarios = binding.listItinerarios;
            adapter = new ParadaItinerarioAdapter(viewModel.paradasItinerario.getValue(), this,
                    false);

            adapterItinerarios = new ItinerarioAdapter(viewModel.itinerarios.getValue(), this);

            listParadas.setAdapter(adapter);
            listItinerarios.setAdapter(adapterItinerarios);

            ItemTouchHelper.Callback callback =
                    new SortListItemHelper(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(listParadas);
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

    public void onClickSalvar(View v){

        if(viewModel.paradasItinerario.getValue().size() > 1){
            List<ParadaItinerarioBairro> pib = viewModel.paradasItinerario.getValue();
            FormItinerario formItinerario = new FormItinerario();
            formItinerario.setCtx(ctx.getApplication());
            formItinerario.show(getSupportFragmentManager(), "formItinerario");
        } else{
            Toast.makeText(getApplicationContext(), "Ao menos duas paradas devem ser selecionadas!", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClickTarifas(View v){
        Intent i = new Intent(getApplicationContext(), TarifasActivity.class);
        startActivity(i);
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
//            List<Overlay> ov = map.getOverlays().;
//            System.out.println(ov.size());
        }
    };

    Observer<List<ParadaItinerarioBairro>> paradasItinerarioObserver = new Observer<List<ParadaItinerarioBairro>>() {
        @Override
        public void onChanged(List<ParadaItinerarioBairro> paradas) {
            adapter.paradas = paradas;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
            adapterItinerarios.itinerarios = itinerarios;
            adapterItinerarios.notifyDataSetChanged();
        }
    };

    Observer<Empresa> empresaObserver = new Observer<Empresa>() {
        @Override
        public void onChanged(Empresa empresa) {
            viewModel.empresa = empresa;
        }
    };

    Observer<Location> centroObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location centro) {

            if(viewModel.centralizaMapa && centro.getLatitude() != 0.0 && centro.getLongitude() != 0.0){
                setMapCenter(map, new GeoPoint(centro.getLatitude(), centro.getLongitude()));
                viewModel.centralizaMapa = false;
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

//            Bitmap b = Bitmap.createBitmap(47, 68, Bitmap.Config.ARGB_8888);
//            Canvas c = new Canvas(b);
//
//            View v = getLayoutInflater().inflate(R.layout.marker,null,false); // inflate view here
//            v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
//            v.draw(c);

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
//                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));

                switch(p.getParada().getSentido()){
                    case 0:
                        m.setIcon(br.com.vostre.circular.utils.DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_keyboard_backspace_black_24dp));
                        break;
                    case 1:
                        m.setIcon(br.com.vostre.circular.utils.DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_keyboard_forward_black_24dp));
                        break;
                    case 2:
                        m.setIcon(br.com.vostre.circular.utils.DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_keyboard_forward_black_24dp));
                        break;
                    default:
                        m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
                        break;
                }

                m.setTitle(p.getParada().getNome());
                m.setDraggable(true);
                m.setId(p.getParada().getId());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

//                        ParadaBairro pb = getParadaFromMarker(marker, paradas);
//
//                        InfoWindow infoWindow = new InfoWindow();
//                        infoWindow.setParada(pb);
//                        infoWindow.setCtx(ctx);
//                        infoWindow.show(getSupportFragmentManager(), "infoWindow");
//                        mapController.animateTo(marker.getPosition());

                        ParadaItinerario paradaItinerario = new ParadaItinerario();
                        paradaItinerario.setParada(p.getParada().getId());
                        paradaItinerario.setDestaque(false);

                        ParadaItinerarioBairro pib = new ParadaItinerarioBairro();
                        pib.setParadaItinerario(paradaItinerario);
                        pib.setNomeParada(p.getParada().getNome());
                        pib.setNomeBairro(p.getNomeBairro());
                        pib.setNomeCidade(p.getNomeCidade());

                        if(viewModel.paradasItinerario.getValue().indexOf(pib) == -1){
                            List<ParadaItinerarioBairro> paradasItinerario = viewModel.paradasItinerario.getValue();
                            paradasItinerario.add(pib);
                            viewModel.paradasItinerario.postValue(paradasItinerario);
//                            viewModel.paradasItinerario.getValue().add(pib);
                            Toast.makeText(ctx, "Parada adicionada ao itinerário", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(ctx, "Parada já existe no itinerário", Toast.LENGTH_SHORT).show();
                        }

                        return true;
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

    @BindingAdapter("center")
    public static void setMapCenter(MapView map, GeoPoint geoPoint){

        if(geoPoint != null){
            map.getController().setCenter(geoPoint);
        }

    }

}
