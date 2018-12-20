package br.com.vostre.circular.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.NumberFormat;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.view.adapter.ParadaItinerarioAdapter;
import br.com.vostre.circular.view.form.FormHistorico;
import br.com.vostre.circular.view.form.FormItinerario;
import br.com.vostre.circular.view.utils.SortListItemHelper;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;
import br.com.vostre.circular.databinding.ActivityDetalhesItinerarioBinding;

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

            viewModel.localAtual.observe(this, localObserver);

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
        map.setMinZoomLevel(15d);
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

            for(final ParadaBairro p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
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

                        return true;
                    }
                });
                map.getOverlays().add(m);
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
            String formatted = DateTimeFormat.forPattern("HH:mm").print(date);
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

    Observer<List<HistoricoItinerario>> historicoItinerarioObserver = new Observer<List<HistoricoItinerario>>() {
        @Override
        public void onChanged(List<HistoricoItinerario> historico) {
            System.out.println(historico);
        }
    };

    Observer<List<ParadaItinerarioBairro>> pitsObserver = new Observer<List<ParadaItinerarioBairro>>() {
        @Override
        public void onChanged(List<ParadaItinerarioBairro> paradas) {
            viewModel.paradasItinerario.postValue(paradas);
        }
    };

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {
//            Toast.makeText(getApplicationContext(), viewModel.itinerario.getValue().getNomeBairroPartida()+" | AAAAAAAAAAAAA >> "
//                    +binding.textViewBairroPartida.getText(), Toast.LENGTH_SHORT).show();
            binding.textViewBairroPartida.setText(viewModel.itinerario.getValue().getNomeBairroPartida());
            viewModel.iti.set(itinerario);
        }
    };

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
