package br.com.vostre.circular.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
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
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityParadasBinding;
import br.com.vostre.circular.databinding.ActivityParadasSugeridasBinding;
import br.com.vostre.circular.listener.ParadaSugestaoListener;
import br.com.vostre.circular.listener.PontoInteresseSugestaoListener;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.PontoInteresseSugestao;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;
import br.com.vostre.circular.utils.DialogUtils;
import br.com.vostre.circular.utils.DrawableUtils;
import br.com.vostre.circular.view.adapter.ParadaSugestaoAdapter;
import br.com.vostre.circular.view.adapter.PontoInteresseSugestaoAdapter;
import br.com.vostre.circular.view.form.FormParada;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.view.utils.InfoWindowSugestao;
import br.com.vostre.circular.view.utils.InfoWindowSugestaoPoi;
import br.com.vostre.circular.viewModel.ParadasSugeridasViewModel;
import br.com.vostre.circular.viewModel.ParadasViewModel;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ParadasSugeridasActivity extends BaseActivity implements ParadaSugestaoListener, PontoInteresseSugestaoListener {

    ActivityParadasSugeridasBinding binding;
    MapView map;
    IMapController mapController;

    ParadasSugeridasViewModel viewModel;

    int permissionGPS;
    int permissionStorage;
    int permissionInternet;

    AppCompatActivity ctx;
    MyLocationNewOverlay mLocationOverlay;
    MapEventsOverlay overlayEvents;

    ParadaSugestaoAdapter adapterSugestao;
    ParadaSugestaoAdapter adapterAceitas;
    ParadaSugestaoAdapter adapterRejeitadas;

    PontoInteresseSugestaoAdapter adapterSugestaoPois;
    PontoInteresseSugestaoAdapter adapterAceitasPois;
    PontoInteresseSugestaoAdapter adapterRejeitadasPois;

    boolean mapaOculto = false;
    int tamanhoOriginalMapa = 0;

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_paradas_sugeridas);
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
            setTitle("Paradas Sugeridas");
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            viewModel = ViewModelProviders.of(this).get(ParadasSugeridasViewModel.class);
            viewModel.paradas.observe(this, paradasObserver);
            viewModel.sugeridas.observe(this, paradasSugeridasObserver);
            viewModel.aceitas.observe(this, paradasAceitasObserver);
            viewModel.rejeitadas.observe(this, paradasRejeitadasObserver);

            viewModel.sugeridasPoi.observe(this, poisSugeridosObserver);
            viewModel.aceitasPoi.observe(this, poisAceitosObserver);
            viewModel.rejeitadasPoi.observe(this, poisRejeitadosObserver);

            viewModel.localAtual.observe(this, centroObserver);

            adapterSugestao = new ParadaSugestaoAdapter(viewModel.sugeridas.getValue(), this);
            adapterSugestao.setListener(this);

            binding.listSugestoes.setAdapter(adapterSugestao);

            ViewCompat.setNestedScrollingEnabled(binding.listSugestoes, false);

            adapterAceitas = new ParadaSugestaoAdapter(viewModel.aceitas.getValue(), this);
            adapterAceitas.setListener(this);

            binding.listAceitas.setAdapter(adapterAceitas);

            ViewCompat.setNestedScrollingEnabled(binding.listAceitas, false);

            adapterRejeitadas = new ParadaSugestaoAdapter(viewModel.rejeitadas.getValue(), this);
            adapterRejeitadas.setListener(this);

            binding.listRejeitadas.setAdapter(adapterRejeitadas);

            ViewCompat.setNestedScrollingEnabled(binding.listRejeitadas, false);

            // POIS

            adapterSugestaoPois = new PontoInteresseSugestaoAdapter(viewModel.sugeridasPoi.getValue(), this);
            adapterSugestaoPois.setListener(this);

            binding.listSugestoesPois.setAdapter(adapterSugestaoPois);

            ViewCompat.setNestedScrollingEnabled(binding.listSugestoesPois, false);

            adapterAceitasPois = new PontoInteresseSugestaoAdapter(viewModel.aceitasPoi.getValue(), this);
            adapterAceitasPois.setListener(this);

            binding.listAceitasPois.setAdapter(adapterAceitasPois);

            ViewCompat.setNestedScrollingEnabled(binding.listAceitasPois, false);

            adapterRejeitadasPois = new PontoInteresseSugestaoAdapter(viewModel.rejeitadasPoi.getValue(), this);
            adapterRejeitadasPois.setListener(this);

            binding.listRejeitadasPois.setAdapter(adapterRejeitadasPois);

            ViewCompat.setNestedScrollingEnabled(binding.listRejeitadasPois, false);

            configuraMapa();

            viewModel.centralizaMapa = true;

            atualizarParadasMapa(viewModel.paradas.getValue());

            viewModel.iniciarAtualizacoesPosicao();

            MapEventsReceiver receiver = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {
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

            tabHost = binding.tabs;
            tabHost.setup();

            TabHost.TabSpec spec = tabHost.newTabSpec("Pendentes");
            spec.setContent(R.id.tab1);
            spec.setIndicator("Pendentes");
            tabHost.addTab(spec);

            TabHost.TabSpec spec2 = tabHost.newTabSpec("Aceitas");
            spec2.setContent(R.id.tab2);
            spec2.setIndicator("Aceitas");
            tabHost.addTab(spec2);

            TabHost.TabSpec spec3 = tabHost.newTabSpec("Rejeitadas");
            spec3.setContent(R.id.tab3);
            spec3.setIndicator("Rejeitadas");
            tabHost.addTab(spec3);

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

        ocultarMapa(map);
    }

    public void ocultarMapa(View v){

        if(mapaOculto){
            binding.map.setVisibility(View.VISIBLE);
            binding.cardView.getLayoutParams().height = tamanhoOriginalMapa;
            mapaOculto = false;
        } else{
            tamanhoOriginalMapa = binding.cardView.getLayoutParams().height;
            binding.map.setVisibility(View.GONE);
            binding.cardView.getLayoutParams().height = WRAP_CONTENT;
            mapaOculto = true;
        }

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

    Observer<List<ParadaSugestaoBairro>> paradasSugeridasObserver = new Observer<List<ParadaSugestaoBairro>>() {
        @Override
        public void onChanged(List<ParadaSugestaoBairro> paradas) {
            adapterSugestao.paradas = paradas;
            adapterSugestao.notifyDataSetChanged();

            if(paradas.size() > 0){
                binding.textViewSugestoesParadasVazia.setVisibility(View.GONE);
            } else{
                binding.textViewSugestoesParadasVazia.setVisibility(View.VISIBLE);
            }

            map.getOverlays().clear();
            map.getOverlays().add(mLocationOverlay);
            map.getOverlays().add(overlayEvents);
            atualizarSugestoesMapa(paradas);
        }
    };

    Observer<List<ParadaSugestaoBairro>> paradasAceitasObserver = new Observer<List<ParadaSugestaoBairro>>() {
        @Override
        public void onChanged(List<ParadaSugestaoBairro> paradas) {
            adapterAceitas.paradas = paradas;
            adapterAceitas.notifyDataSetChanged();

            map.getOverlays().clear();
            map.getOverlays().add(mLocationOverlay);
            map.getOverlays().add(overlayEvents);
            atualizarSugestoesMapa(paradas);
        }
    };

    Observer<List<ParadaSugestaoBairro>> paradasRejeitadasObserver = new Observer<List<ParadaSugestaoBairro>>() {
        @Override
        public void onChanged(List<ParadaSugestaoBairro> paradas) {
            adapterRejeitadas.paradas = paradas;
            adapterRejeitadas.notifyDataSetChanged();

            map.getOverlays().clear();
            map.getOverlays().add(mLocationOverlay);
            map.getOverlays().add(overlayEvents);
            atualizarSugestoesMapa(paradas);
        }
    };

    //pois

    Observer<List<PontoInteresseSugestaoBairro>> poisSugeridosObserver = new Observer<List<PontoInteresseSugestaoBairro>>() {
        @Override
        public void onChanged(List<PontoInteresseSugestaoBairro> paradas) {
            adapterSugestaoPois.paradas = paradas;
            adapterSugestaoPois.notifyDataSetChanged();

            if(paradas.size() > 0){
                binding.textViewSugestoesPoisVazia.setVisibility(View.GONE);
            } else{
                binding.textViewSugestoesPoisVazia.setVisibility(View.VISIBLE);
            }

            map.getOverlays().clear();
            map.getOverlays().add(mLocationOverlay);
            map.getOverlays().add(overlayEvents);
            atualizarSugestoesPoiMapa(paradas);
        }
    };

    Observer<List<PontoInteresseSugestaoBairro>> poisAceitosObserver = new Observer<List<PontoInteresseSugestaoBairro>>() {
        @Override
        public void onChanged(List<PontoInteresseSugestaoBairro> paradas) {
            adapterAceitasPois.paradas = paradas;
            adapterAceitasPois.notifyDataSetChanged();

            map.getOverlays().clear();
            map.getOverlays().add(mLocationOverlay);
            map.getOverlays().add(overlayEvents);
            atualizarSugestoesPoiMapa(paradas);
        }
    };

    Observer<List<PontoInteresseSugestaoBairro>> poisRejeitadosObserver = new Observer<List<PontoInteresseSugestaoBairro>>() {
        @Override
        public void onChanged(List<PontoInteresseSugestaoBairro> paradas) {
            adapterRejeitadasPois.paradas = paradas;
            adapterRejeitadasPois.notifyDataSetChanged();

            map.getOverlays().clear();
            map.getOverlays().add(mLocationOverlay);
            map.getOverlays().add(overlayEvents);
            atualizarSugestoesPoiMapa(paradas);
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
                m.setTitle(p.getParada().getNome());

                switch(p.getParada().getSentido()){
                    case 0:
                        m.setIcon(DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_keyboard_backspace_black_24dp));
                        break;
                    case 1:
                        m.setIcon(DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_keyboard_forward_black_24dp));
                        break;
                    case 2:
                        m.setIcon(DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_keyboard_forward_black_24dp));
                        break;
                    default:
                        m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
                        break;
                }

                m.setDraggable(false);
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
                poiMarkers.add(m);
                //map.getOverlays().add(m);
            }

        }

    }

    private void atualizarSugestoesMapa(final List<ParadaSugestaoBairro> paradas){

        if(paradas != null){

            RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(this);
            poiMarkers.setRadius(200);

            Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_cluster);
            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

            map.getOverlays().add(poiMarkers);
            poiMarkers.setIcon(clusterIcon);

            for(ParadaSugestaoBairro p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setTitle(p.getParada().getNome());

                switch(p.getParada().getSentido()){
                    case 0:
                        m.setIcon(DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_keyboard_backspace_black_24dp));
                        break;
                    case 1:
                        m.setIcon(DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_keyboard_forward_black_24dp));
                        break;
                    case 2:
                        m.setIcon(DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_keyboard_forward_black_24dp));
                        break;
                    default:
                        m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.sugestao));
                        break;
                }

                m.setDraggable(false);
                m.setId(p.getParada().getId());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        ParadaSugestaoBairro pb = getSugestaoFromMarker(marker, paradas);

                        InfoWindowSugestao infoWindow = new InfoWindowSugestao();
                        infoWindow.setParada(pb);
                        infoWindow.setCtx(ctx);
                        infoWindow.show(getSupportFragmentManager(), "infoWindow");
                        mapController.animateTo(marker.getPosition());
                        return true;
                    }
                });
                poiMarkers.add(m);
                //map.getOverlays().add(m);
            }

        }

    }

    private void atualizarSugestoesPoiMapa(final List<PontoInteresseSugestaoBairro> paradas){

        if(paradas != null){

            RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(this);
            poiMarkers.setRadius(200);

            Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_cluster);
            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

            map.getOverlays().add(poiMarkers);
            poiMarkers.setIcon(clusterIcon);

            for(PontoInteresseSugestaoBairro p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getPontoInteresse().getLatitude(), p.getPontoInteresse().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setTitle(p.getPontoInteresse().getNome());

                m.setDraggable(false);
                m.setId(p.getPontoInteresse().getId());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        PontoInteresseSugestaoBairro pb = getSugestaoPoiFromMarker(marker, paradas);

                        InfoWindowSugestaoPoi infoWindow = new InfoWindowSugestaoPoi();
                        infoWindow.setParada(pb);
                        infoWindow.setCtx(ctx);
                        infoWindow.show(getSupportFragmentManager(), "infoWindowPoi");
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
    private ParadaSugestaoBairro getSugestaoFromMarker(Marker marker, List<ParadaSugestaoBairro> paradas) {
        ParadaSugestao p = new ParadaSugestao();
        p.setId(marker.getId());

        ParadaSugestaoBairro parada = new ParadaSugestaoBairro();
        parada.setParada(p);

        ParadaSugestaoBairro pb = paradas.get(paradas.indexOf(parada));
        pb.getParada().setLatitude(marker.getPosition().getLatitude());
        pb.getParada().setLongitude(marker.getPosition().getLongitude());
        return pb;
    }

    @NonNull
    private PontoInteresseSugestaoBairro getSugestaoPoiFromMarker(Marker marker, List<PontoInteresseSugestaoBairro> paradas) {
        PontoInteresseSugestao p = new PontoInteresseSugestao();
        p.setId(marker.getId());

        PontoInteresseSugestaoBairro parada = new PontoInteresseSugestaoBairro();
        parada.setPontoInteresse(p);

        PontoInteresseSugestaoBairro pb = paradas.get(paradas.indexOf(parada));
        pb.getPontoInteresse().setLatitude(marker.getPosition().getLatitude());
        pb.getPontoInteresse().setLongitude(marker.getPosition().getLongitude());
        return pb;
    }

    @BindingAdapter("center")
    public static void setMapCenter(MapView map, GeoPoint geoPoint){

        if(geoPoint != null){
            map.getController().setCenter(geoPoint);
        }

    }

    @Override
    public void onSelected(String id, int acao) {

        ParadaSugestaoBairro pa = new ParadaSugestaoBairro();
        pa.getParada().setId(id);

        final ParadaSugestaoBairro p = viewModel.sugeridas.getValue().get(viewModel.sugeridas.getValue().indexOf(pa));

        switch(acao){
            case 0:
                // rejeitou
                new AlertDialog.Builder(this)
                        .setTitle("Confirma a rejeição?")
                        .setMessage("Deseja realmente rejeitar a sugestão para a parada "+p.getParada().getNome()+"?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final View v = ctx.getLayoutInflater().inflate(R.layout.form_obs_sugestao, null);

                                new AlertDialog.Builder(ctx)
                                        .setTitle("Adicionar observação?")
                                        .setMessage("Digite-a no campo abaixo")
                                        .setView(v)
                                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                EditText editTextObservacao = v.findViewById(R.id.editTextObservacao);
                                                String text = editTextObservacao.getText().toString();

                                                if(!text.isEmpty()){

                                                    if(p.getParada().getObservacao() != null){
                                                        p.getParada().setObservacao(p.getParada().getObservacao().concat(System.getProperty("line.separator")).concat(text));
                                                    } else{
                                                        p.getParada().setObservacao(text);
                                                    }


                                                }

                                                viewModel.rejeitaSugestao(p);

                                            }
                                        })
                                        .setNegativeButton("Não, apenas rejeitar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                viewModel.rejeitaSugestao(p);
                                            }
                                        })
                                        .show();

                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
                break;
            case 1:
                //aceitou
                new AlertDialog.Builder(this)
                        .setTitle("Confirma o aceite?")
                        .setMessage("Deseja realmente aceitar a sugestão para a parada "+p.getParada().getNome()+"?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                viewModel.aceitaSugestao(p);
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
                break;
            case 2:
                // ver no mapa

                GeoPoint point = new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude());

                map.getController().animateTo(point);

                break;
        }

    }

    @Override
    public void onSelectedPoi(String id, int acao) {

        PontoInteresseSugestaoBairro pa = new PontoInteresseSugestaoBairro();
        pa.getPontoInteresse().setId(id);

        final PontoInteresseSugestaoBairro p = viewModel.sugeridasPoi.getValue().get(viewModel.sugeridasPoi.getValue().indexOf(pa));

        switch(acao){
            case 0:
                // rejeitou
                new AlertDialog.Builder(this)
                        .setTitle("Confirma a rejeição?")
                        .setMessage("Deseja realmente rejeitar a sugestão para o Ponto de Interesse "+p.getPontoInteresse().getNome()+"?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final View v = ctx.getLayoutInflater().inflate(R.layout.form_obs_sugestao, null);

                                new AlertDialog.Builder(ctx)
                                        .setTitle("Adicionar observação?")
                                        .setMessage("Digite-a no campo abaixo")
                                        .setView(v)
                                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                EditText editTextObservacao = v.findViewById(R.id.editTextObservacao);
                                                String text = editTextObservacao.getText().toString();

                                                if(!text.isEmpty()){

                                                    if(p.getPontoInteresse().getObservacao() != null){
                                                        p.getPontoInteresse().setObservacao(p.getPontoInteresse().getObservacao().concat(System.getProperty("line.separator")).concat(text));
                                                    } else{
                                                        p.getPontoInteresse().setObservacao(text);
                                                    }


                                                }

                                                viewModel.rejeitaSugestaoPoi(p);

                                            }
                                        })
                                        .setNegativeButton("Não, apenas rejeitar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                viewModel.rejeitaSugestaoPoi(p);
                                            }
                                        })
                                        .show();

                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
                break;
            case 1:
                //aceitou
                new AlertDialog.Builder(this)
                        .setTitle("Confirma o aceite?")
                        .setMessage("Deseja realmente aceitar a sugestão para o Ponto de Interesse "+p.getPontoInteresse().getNome()+"?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                viewModel.aceitaSugestaoPoi(p);
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
                break;
            case 2:
                // ver no mapa

                GeoPoint point = new GeoPoint(p.getPontoInteresse().getLatitude(), p.getPontoInteresse().getLongitude());

                map.getController().animateTo(point);

                break;
        }

    }

}
