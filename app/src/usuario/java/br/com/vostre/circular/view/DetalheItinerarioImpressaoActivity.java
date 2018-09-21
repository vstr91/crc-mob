package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalheItinerarioBinding;
import br.com.vostre.circular.databinding.ActivityDetalheItinerarioImpressaoBinding;
import br.com.vostre.circular.databinding.LinhaHorariosItinerariosBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.view.adapter.SecaoItinerarioAdapter;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;

public class DetalheItinerarioImpressaoActivity extends AppCompatActivity {

    ActivityDetalheItinerarioImpressaoBinding binding;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhe_itinerario_impressao);
        binding.getRoot().setDrawingCacheEnabled(true);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        super.onCreate(savedInstanceState);

        ctx = this;

        viewModel = ViewModelProviders.of(this).get(DetalhesItinerarioViewModel.class);

        viewModel.setItinerario(getIntent().getStringExtra("itinerario"));

        viewModel.itinerario.observe(this, itinerarioObserver);

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

    Observer<List<HorarioItinerarioNome>> horariosObserver = new Observer<List<HorarioItinerarioNome>>() {
        @Override
        public void onChanged(List<HorarioItinerarioNome> horarios) {

            for(HorarioItinerarioNome h : horarios){
                LinhaHorariosItinerariosBinding b = DataBindingUtil.inflate(getLayoutInflater(),
                        R.layout.linha_horarios_itinerarios, binding.linearLayoutHorarios, false);

                b.setHorario(h);
                b.executePendingBindings();
                binding.linearLayoutHorarios.addView(b.getRoot());
            }

            Bitmap b = binding.getRoot().getDrawingCache();

        try {
            System.out.println("LOCAL::: "+getApplication().getFilesDir()+"/image.jpg");
            b.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(getApplication().getFilesDir()+"/image.jpg"));
            Toast.makeText(getApplicationContext(), "Exportado!", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

            //adapter.horarios = horarios;
            //adapter.notifyDataSetChanged();
        }
    };

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            viewModel.atualizaPontoMapa();
            atualizarParadasMapa(paradas);
        }
    };

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {

            if(itinerario != null){
                binding.setItinerario(itinerario);

                if(itinerario.getItinerario().getSigla() == null || itinerario.getItinerario().getSigla().isEmpty() || itinerario.getItinerario().getSigla().equals(null)){
                    binding.textView37.setVisibility(View.GONE);
                } else{
                    binding.textView37.setVisibility(View.VISIBLE);
                }

                viewModel.paradas.observe(ctx, paradasObserver);
            }

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
                map.getOverlays().add(m);
            }

        }

    }

}
