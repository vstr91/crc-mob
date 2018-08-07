package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.BR;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalheParadaBinding;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.PontosInteresseAdapter;
import br.com.vostre.circular.view.form.FormMapa;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;
import br.com.vostre.circular.viewModel.DetalhesParadaViewModel;

public class DetalheParadaActivity extends BaseActivity {

    ActivityDetalheParadaBinding binding;
    DetalhesParadaViewModel viewModel;
    ItinerarioAdapter adapter;
    PontosInteresseAdapter adapterPois;

    RecyclerView listItinerarios;
    AppCompatActivity ctx;

    BottomSheetDialog bsd;
    RecyclerView listPois;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhe_parada);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        super.onCreate(savedInstanceState);
        setTitle("Detalhe Parada");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ctx = this;

        viewModel = ViewModelProviders.of(this).get(DetalhesParadaViewModel.class);

        binding.setViewModel(viewModel);

        viewModel.setParada(getIntent().getStringExtra("parada"));

        viewModel.parada.observe(this, paradaObserver);

        listItinerarios = binding.listItinerarios;
        adapter = new ItinerarioAdapter(viewModel.itinerarios.getValue(), this);
        listItinerarios.setAdapter(adapter);

        bsd = new BottomSheetDialog(ctx);
        bsd.setCanceledOnTouchOutside(true);

        bsd.setContentView(R.layout.bottom_sheet_pois);

        listPois = bsd.findViewById(R.id.listPois);
        ImageButton btnFechar = bsd.findViewById(R.id.btnFechar);
        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.dismiss();
            }
        });

        binding.textView15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.show();
            }
        });

        viewModel.itinerarios.observe(this, itinerariosObserver);

    }

    @BindingAdapter("app:textDinheiro")
    public static void setTextDinheiro(TextView view, Double val){

        if(val != null){
            view.setText(NumberFormat.getCurrencyInstance().format(val));
        } else{
            view.setText("-");
        }

    }

    @BindingAdapter("app:text")
    public static void setText(TextView view, Double val){

        if(val != null){
            view.setText(NumberFormat.getNumberInstance().format(val));
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
    public static void setText(TextView view, String val){

        if(val != null){
            view.setText(val);
        } else{
            view.setText("-");
        }

    }

    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
            adapter.itinerarios = itinerarios;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<ParadaBairro> paradaObserver = new Observer<ParadaBairro>() {
        @Override
        public void onChanged(ParadaBairro parada) {

            if(parada != null){
                binding.setUmaParada(parada);

                adapterPois = new PontosInteresseAdapter(viewModel.pois.getValue(), ctx, parada, bsd);
                listPois.setAdapter(adapterPois);

                Location location = new Location(LocationManager.NETWORK_PROVIDER);
                location.setLatitude(parada.getParada().getLatitude());
                location.setLongitude(parada.getParada().getLongitude());

                viewModel.buscaPoisProximos(location);
                viewModel.pois.observe(ctx, poisObserver);
                //viewModel.carregarItinerarios(parada.getParada().getId());
                //viewModel.itinerarios.observe(ctx, itinerariosObserver);
            }

        }
    };

    Observer<List<PontoInteresse>> poisObserver = new Observer<List<PontoInteresse>>() {
        @Override
        public void onChanged(List<PontoInteresse> pois) {

            if(pois.size() > 0){
                binding.textView15.setVisibility(View.VISIBLE);
            }

            adapterPois.pois = pois;
            adapterPois.notifyDataSetChanged();
        }
    };

}
