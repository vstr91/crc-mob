package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import br.com.vostre.circular.BR;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalheParadaBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.viewModel.DetalhesParadaViewModel;

public class DetalheParadaActivity extends BaseActivity {

    ActivityDetalheParadaBinding binding;
    DetalhesParadaViewModel viewModel;
    ItinerarioAdapter adapter;

    RecyclerView listItinerarios;
    AppCompatActivity ctx;

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

        viewModel.itinerarios.observe(this, itinerariosObserver);

    }

    @BindingAdapter("app:text")
    public static void setText(TextView view, Double val){

        if(val != null){
            view.setText(String.valueOf(val));
        } else{
            view.setText("-");
        }

    }

    @BindingAdapter("app:text")
    public static void setText(TextView view, DateTime val){

        if(val != null){
            view.setText(DateTimeFormat.forPattern("HH:mm").print(val));
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
                //viewModel.carregarItinerarios(parada.getParada().getId());
                //viewModel.itinerarios.observe(ctx, itinerariosObserver);
            }

        }
    };

}
