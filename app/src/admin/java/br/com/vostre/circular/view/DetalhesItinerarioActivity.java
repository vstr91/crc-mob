package br.com.vostre.circular.view;

import android.Manifest;
import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.model.pojo.TrechoPartidaDestino;
import br.com.vostre.circular.utils.DrawableUtils;
import br.com.vostre.circular.utils.WidgetUtils;
import br.com.vostre.circular.view.adapter.ParadaItinerarioAdapter;
import br.com.vostre.circular.view.adapter.TrechoAdapter;
import br.com.vostre.circular.view.form.FormHistorico;
import br.com.vostre.circular.view.form.FormItinerario;
import br.com.vostre.circular.view.utils.SortListItemHelper;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;
import br.com.vostre.circular.databinding.ActivityDetalhesItinerarioBinding;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DetalhesItinerarioActivity extends BaseActivity {

    ActivityDetalhesItinerarioBinding binding;
    MapView map;
    IMapController mapController;

    RecyclerView listParadas;
    ParadaItinerarioAdapter adapter;

    int permissionGPS;
    int permissionStorage;
    int permissionInternet;

    AppCompatActivity ctx;
    MyLocationNewOverlay mLocationOverlay;
    MapEventsOverlay overlayEvents;

    DetalhesItinerarioViewModel viewModel;

    boolean mapaOculto = false;
    int tamanhoOriginalMapa = 0;

    BottomSheetDialog bsd;
    RecyclerView listTrechos;
    TrechoAdapter adapterTrechos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhes_itinerario);
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
            setTitle("Detalhes Itinerário");
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            viewModel = ViewModelProviders.of(this).get(DetalhesItinerarioViewModel.class);

            viewModel.setItinerario(getIntent().getStringExtra("itinerario"));

            viewModel.itinerario.observe(this, itinerarioObserver);
            viewModel.paradas.observe(this, paradasObserver);
            viewModel.pits.observe(this, pitsObserver);
            viewModel.paradasItinerario.observe(this, paradasItinerarioObserver);
            viewModel.historicoItinerario.observe(this, historicoItinerarioObserver);
            viewModel.trechos.observe(this, trechosObserver);

//            viewModel.localAtual.observe(this, localObserver);

            binding.setViewModel(viewModel);

            configuraMapa();

            atualizarParadasMapa(viewModel.paradas.getValue());

            viewModel.iniciarAtualizacoesPosicao();

            ctx = this;

            MapEventsReceiver receiver = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {
//                    Toast.makeText(getBaseContext(),p.getLatitude() + " - "
//                            +p.getLongitude(),Toast.LENGTH_LONG).show();
//
//                    FormParada formParada = new FormParada();
//                    formParada.setLatitude(p.getLatitude());
//                    formParada.setLongitude(p.getLongitude());
//                    formParada.setCtx(getApplication());
//                    formParada.show(getSupportFragmentManager(), "formParada");

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
            adapter = new ParadaItinerarioAdapter(viewModel.paradasItinerario.getValue(), this,
                    true);

            listParadas.setAdapter(adapter);

            ItemTouchHelper.Callback callback =
                    new SortListItemHelper(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(listParadas);
        }

        bsd = new BottomSheetDialog(ctx);
        bsd.setCanceledOnTouchOutside(true);

        bsd.setContentView(R.layout.bottom_sheet_trechos);

        listTrechos = bsd.findViewById(R.id.listTrechos);

        adapterTrechos = new TrechoAdapter(viewModel.trechos.getValue(), this);

        listTrechos.setAdapter(adapterTrechos);

        ImageButton btnFechar = bsd.findViewById(R.id.btnFechar);
        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.dismiss();
            }
        });

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

        map.setMaxZoomLevel(25d);
        map.setMinZoomLevel(9d);

    }

    public void onFabBairroClick(View v){
        List<Overlay> o = map.getOverlays();

        for(Overlay ov : o){

            if(ov instanceof RadiusMarkerClusterer){

                List<Marker> ma = ((RadiusMarkerClusterer) ov).getItems();

                for(Marker mk : ma){

                    switch(mk.getSubDescription()){
                        case "0":
                            break;
                        case "1":

                            if(mk.getAlpha() == 1f){
                                mk.setVisible(false);
                                mk.setEnabled(false);
                            } else{
                                mk.setVisible(true);
                                mk.setEnabled(true);
                            }

                            break;
                    }

                }

            }

        }

    }

    public void onFabCentroClick(View v){
        List<Overlay> o = map.getOverlays();

        for(Overlay ov : o){

            if(ov instanceof RadiusMarkerClusterer){

                List<Marker> ma = ((RadiusMarkerClusterer) ov).getItems();

                for(Marker mk : ma){

                    switch(mk.getSubDescription()){
                        case "0":

                            if(mk.getAlpha() == 1f){
                                mk.setVisible(false);
                                mk.setEnabled(false);
                            } else{
                                mk.setVisible(true);
                                mk.setEnabled(true);
                            }

                            break;
                        case "1":
                            break;
                    }

                }

            }

        }

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

    public void onClickBtnSalvar(View v){
        if(viewModel.paradasItinerario.getValue().size() > 1){
            viewModel.salvarParadas();
        } else{
            Toast.makeText(getApplicationContext(), "Ao menos duas paradas devem ser selecionadas!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickBtnEditar(View v){

//        Toast.makeText(getApplicationContext(), viewModel.itinerario.getValue().getItinerario().getId(), Toast.LENGTH_SHORT).show();

        FormItinerario formItinerario = new FormItinerario();
        formItinerario.setItinerario(viewModel.itinerario.getValue().getItinerario());
        formItinerario.setCtx(ctx.getApplication());
        formItinerario.show(getSupportFragmentManager(), "formItinerario");

    }

    public void onClickBtnDistancia(View v){

        Toast.makeText(getApplicationContext(), "Atualizando dados de distância...", Toast.LENGTH_SHORT).show();

        map.getTileProvider().clearTileCache();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                viewModel.atualizaParadasItinerario(viewModel.itinerario.getValue().getItinerario().getId());

                List<ParadaItinerarioBairro> paradas = viewModel.pits.getValue();

                viewModel.calculaDistancia(viewModel.itinerario.getValue().getItinerario(), paradas, false);

                viewModel.atualizaTrajetoItinerarios(viewModel.itinerario.getValue().getItinerario().getId());

                viewModel.atualizaDistanciaAcumulada(viewModel.itinerario.getValue().getItinerario().getId());
            }
        });
    }

    public void onClickBtnAtualizaTrechos(View v){

        Toast.makeText(getApplicationContext(), "Atualizando trechos...", Toast.LENGTH_SHORT).show();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                viewModel.atualizaTemporarias(viewModel.itinerario.getValue().getItinerario().getId());
            }
        });
    }

    public void onClickBtnHistorico(View v){

//        Toast.makeText(getApplicationContext(), viewModel.itinerario.getValue().getItinerario().getId(), Toast.LENGTH_SHORT).show();

        FormHistorico formHistorico = new FormHistorico();
        formHistorico.setItinerario(viewModel.itinerario.getValue().getItinerario());
        formHistorico.setHistorico(viewModel.historicoItinerario.getValue());
        formHistorico.setCtx(ctx.getApplication());
        formHistorico.show(getSupportFragmentManager(), "formHistorico");

    }

    public void onClickBtnSecoes(View v){
        Intent i = new Intent(ctx, SecoesItinerarioActivity.class);
        i.putExtra("itinerario", viewModel.getItinerario().getValue().getItinerario().getId());
        ctx.startActivity(i);
    }

    public void onClickBtnHorarios(View v){
        Intent i = new Intent(ctx, HorariosItinerarioActivity.class);
        i.putExtra("itinerario", viewModel.getItinerario().getValue().getItinerario().getId());
        ctx.startActivity(i);
    }

    public void onClickBtnRegistrarViagem(View v){
        Intent i = new Intent(ctx, RegistraViagemActivity.class);
        i.putExtra("itinerario", viewModel.getItinerario().getValue().getItinerario().getId());
        ctx.startActivity(i);
    }

    public void onClickBtnVerTrechos(View v){
        bsd.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(map != null && map.getOverlayManager() != null){
            map.onResume();
        }

        startLocationUpdates();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(map != null && map.getOverlayManager() != null){
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

                m.setTitle(p.getParada().getNome());
                m.setDraggable(true);
                m.setId(p.getParada().getId());
                m.setSubDescription(String.valueOf(p.getParada().getSentido()));
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

                        if(marker.getAlpha() == 1f){
                            ParadaItinerario paradaItinerario = new ParadaItinerario();
                            paradaItinerario.setParada(p.getParada().getId());
                            paradaItinerario.setDestaque(false);
                            paradaItinerario.setValorAnterior(null);
                            paradaItinerario.setValorSeguinte(null);
                            paradaItinerario.setAtivo(true);

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
                        }

                        return true;
                    }
                });
                poiMarkers.add(m);
//                map.getOverlays().add(m);
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

    @BindingAdapter("text")
    public static void setText(TextView view, DateTime date) {

        if(date != null){
            String formatted = DateTimeFormat.forPattern("HH:mm:ss").print(date);
            view.setText(formatted);
        } else{
            view.setText("-");
        }


    }

    @BindingAdapter("app:distancia")
    public static void setText(TextView view, Double value) {

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);

        if(value == null){
            view.setText("0 Km");
        } else{

            try{
                String distancia = nf.format(value/1000)+" Km";
                view.setText(distancia);
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

            atualizaTrajetoMapa(viewModel.iti.get());

//            List<Overlay> ov = map.getOverlays().;
//            System.out.println(ov.size());
        }
    };

    Observer<List<ParadaItinerarioBairro>> paradasItinerarioObserver = new Observer<List<ParadaItinerarioBairro>>() {
        @Override
        public void onChanged(final List<ParadaItinerarioBairro> paradas) {
            adapter.paradas = paradas;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<List<HistoricoItinerario>> historicoItinerarioObserver = new Observer<List<HistoricoItinerario>>() {
        @Override
        public void onChanged(List<HistoricoItinerario> historico) {
            //System.out.println(historico);
        }
    };

    Observer<List<ParadaItinerarioBairro>> pitsObserver = new Observer<List<ParadaItinerarioBairro>>() {
        @Override
        public void onChanged(final List<ParadaItinerarioBairro> paradas) {
            viewModel.paradasItinerario.postValue(paradas);

            if(map != null){

                List<GeoPoint> geoPoints = new ArrayList<>();

                for(ParadaItinerarioBairro p : paradas){
                    Location l = new Location(LocationManager.NETWORK_PROVIDER);
                    l.setLatitude(Double.parseDouble(p.getLatitude()));
                    l.setLongitude(Double.parseDouble(p.getLongitude()));

                    GeoPoint g = new GeoPoint(l);
                    geoPoints.add(g);
                }


                map.getController().zoomToSpan(BoundingBox.fromGeoPoints(geoPoints).getLatitudeSpan(),
                        BoundingBox.fromGeoPoints(geoPoints).getLongitudeSpanWithDateLine());

                viewModel.atualizaPontoMapa();

                viewModel.localAtual.observe(ctx, localObserver);
            }
        }
    };

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {
//            Toast.makeText(getApplicationContext(), viewModel.itinerario.getValue().getNomeBairroPartida()+" | AAAAAAAAAAAAA >> "
//                    +binding.textViewBairroPartida.getText(), Toast.LENGTH_SHORT).show();
            binding.textViewBairroPartida.setText(viewModel.itinerario.getValue().getNomeBairroPartida());
            viewModel.iti.set(itinerario);

            atualizaTrajetoMapa(itinerario);

            WidgetUtils.desenhaTrajetoMapa(itinerario.getItinerario(), map,
                                10, R.color.azul);

            viewModel.carregaDirections(map, itinerario.getItinerario());
        }
    };

    Observer<List<TrechoPartidaDestino>> trechosObserver = new Observer<List<TrechoPartidaDestino>>() {
        @Override
        public void onChanged(final List<TrechoPartidaDestino> trechos) {
            adapterTrechos.trechos = trechos;
            adapterTrechos.notifyDataSetChanged();

            if(trechos.size() > 0){
                binding.btnTrechos.setText("Ver Trechos ("+trechos.size()+")");
            }

        }
    };

    private void atualizaTrajetoMapa(ItinerarioPartidaDestino itinerario) {
        List<GeoPoint> pontos = PolylineEncoder.decode(itinerario.getItinerario().getTrajeto(), 10, false);

        Polyline line = new Polyline();
        line.setPoints(pontos);
        line.getOutlinePaint().setStrokeWidth(10);
        line.getOutlinePaint().setColor(getResources().getColor(R.color.azul));

        line.getOutlinePaint().setStrokeJoin(Paint.Join.ROUND);
        line.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);

        map.getOverlays().add(line);
        map.invalidate();
    }

    Observer<Location> localObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location local) {

            if(viewModel.centralizaMapa && local.getLatitude() != 0.0 && local.getLongitude() != 0.0){
                setMapCenter(map, new GeoPoint(local.getLatitude(), local.getLongitude()));
                viewModel.centralizaMapa = false;
            }

        }
    };

    @BindingAdapter("center")
    public static void setMapCenter(MapView map, GeoPoint geoPoint){

        if(geoPoint != null){
            map.getController().setCenter(geoPoint);
        }

    }

}
