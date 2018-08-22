package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalheItinerarioBinding;
import br.com.vostre.circular.databinding.ActivityDetalheParadaBinding;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.SecaoItinerarioAdapter;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;
import br.com.vostre.circular.viewModel.DetalhesParadaViewModel;

public class DetalheItinerarioActivity extends BaseActivity {

    ActivityDetalheItinerarioBinding binding;
    DetalhesItinerarioViewModel viewModel;
    HorarioItinerarioAdapter adapter;
    SecaoItinerarioAdapter adapterSecoes;

    RecyclerView listHorarios;
    RecyclerView listSecoes;
    AppCompatActivity ctx;

    MapView map;
    IMapController mapController;
    MyLocationNewOverlay mLocationOverlay;

    BottomSheetDialog bsd;
    boolean flagFavorito = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhe_itinerario);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        super.onCreate(savedInstanceState);
        setTitle("Detalhe Itinerário");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ctx = this;

        viewModel = ViewModelProviders.of(this).get(DetalhesItinerarioViewModel.class);

        binding.setViewModel(viewModel);

        viewModel.setItinerario(getIntent().getStringExtra("itinerario"));

        viewModel.itinerario.observe(this, itinerarioObserver);

        listHorarios = binding.listHorarios;
        adapter = new HorarioItinerarioAdapter(viewModel.horarios.getValue(), this);
        listHorarios.setAdapter(adapter);

        bsd = new BottomSheetDialog(ctx);
        bsd.setCanceledOnTouchOutside(true);

        bsd.setContentView(R.layout.bottom_sheet_secoes);

        listSecoes = bsd.findViewById(R.id.listSecoes);
        ImageButton btnFechar = bsd.findViewById(R.id.btnFechar);
        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.dismiss();
            }
        });

        adapterSecoes = new SecaoItinerarioAdapter(viewModel.secoes.getValue(), this);
        listSecoes.setAdapter(adapterSecoes);

        viewModel.horarios.observe(this, horariosObserver);
        viewModel.localAtual.observe(this, localObserver);

        configuraMapa();

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
        mapController.setZoom(10.5d);
//        GeoPoint startPoint = new GeoPoint(-22.470804460339885, -43.82463455200195);
//        mapController.setCenter(startPoint);

        map.setMaxZoomLevel(19d);
        map.setMinZoomLevel(8d);
    }

    public void onClickBtnFavorito(View v){
        List<String> lstItinerarios = PreferenceUtils.carregaItinerariosFavoritos(getApplicationContext());

        List<ParadaBairro> paradas = viewModel.paradas.getValue();

        if(!flagFavorito){
            SnackbarHelper.notifica(v, "Itinerário adicionado aos favoritos!", Snackbar.LENGTH_LONG);
            binding.imageButton4.setImageResource(R.drawable.ic_star_white_24dp);
            flagFavorito = true;

            lstItinerarios.add(paradas.get(0).getIdBairro()+"|"+paradas.get(paradas.size()-1).getIdBairro());

        } else{
            SnackbarHelper.notifica(v, "Itinerário removido dos favoritos!", Snackbar.LENGTH_LONG);
            binding.imageButton4.setImageResource(R.drawable.ic_star_border_white_24dp);
            flagFavorito = false;

            lstItinerarios.remove(paradas.get(0).getIdBairro()+"|"+paradas.get(paradas.size()).getIdBairro());

        }

        PreferenceUtils.gravaItinerariosFavoritos(lstItinerarios, getApplicationContext());
    }


    @BindingAdapter("app:textDinheiro")
    public static void setTextDinheiro(TextView view, Double val){

        if(val != null){
            view.setText(NumberFormat.getCurrencyInstance().format(val));
        } else{
            view.setText("-");
        }

    }

    @BindingAdapter("app:textDistancia")
    public static void setTextDistancia(TextView view, Double val){

        if(val != null){
            DecimalFormat format = new DecimalFormat();
            format.setMinimumFractionDigits(1);
            format.setMaximumFractionDigits(1);
            view.setText(format.format(val)+" Km");
        } else{
            view.setText("-");
        }

    }

    @BindingAdapter("app:textData")
    public static void setText(TextView view, DateTime val){

        if(val != null){
            view.setText(DateTimeFormat.forPattern("HH:mm").print(val));
        } else{
            view.setText("-");
        }

    }

    public void btnSecoesClick(View v){
        bsd.show();
    }

    Observer<List<HorarioItinerarioNome>> horariosObserver = new Observer<List<HorarioItinerarioNome>>() {
        @Override
        public void onChanged(List<HorarioItinerarioNome> horarios) {
            adapter.horarios = horarios;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            viewModel.atualizaPontoMapa();

            viewModel.carregaDirections(map, paradas);

            atualizarParadasMapa(paradas);

            List<String> lstItinerarios = PreferenceUtils.carregaItinerariosFavoritos(getApplicationContext());

            List<ParadaBairro> listParadas = viewModel.paradas.getValue();

            int i = lstItinerarios.indexOf(paradas.get(0).getIdBairro()+"|"+paradas.get(paradas.size()-1).getIdBairro());

            if(i >= 0){
                binding.imageButton4.setImageResource(R.drawable.ic_star_white_24dp);
                flagFavorito = true;
            } else{
                binding.imageButton4.setImageResource(R.drawable.ic_star_border_white_24dp);
                flagFavorito = false;
            }

        }
    };

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {

            if(itinerario != null){
                binding.setItinerario(itinerario);
                viewModel.paradas.observe(ctx, paradasObserver);
                viewModel.secoes.observe(ctx, secoesObserver);
                //viewModel.carregarItinerarios(parada.getParada().getId());
                //viewModel.itinerarios.observe(ctx, itinerariosObserver);
            }

        }
    };

    Observer<List<SecaoItinerario>> secoesObserver = new Observer<List<SecaoItinerario>>() {
        @Override
        public void onChanged(List<SecaoItinerario> secoes) {
            adapterSecoes.secoes = secoes;
            adapterSecoes.notifyDataSetChanged();
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

    private void atualizarParadasMapa(final List<ParadaBairro> paradas){

        if(paradas != null){

            for(final ParadaBairro p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setTitle(p.getParada().getNome());
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

}
