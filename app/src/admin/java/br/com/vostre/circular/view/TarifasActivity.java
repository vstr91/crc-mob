package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityPaisesBinding;
import br.com.vostre.circular.databinding.ActivityTarifasBinding;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioTarifaAdapter;
import br.com.vostre.circular.view.adapter.PaisAdapter;
import br.com.vostre.circular.view.form.FormPais;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;
import br.com.vostre.circular.viewModel.PaisesViewModel;
import br.com.vostre.circular.viewModel.TarifasViewModel;

public class TarifasActivity extends BaseActivity {

    ActivityTarifasBinding binding;
    TarifasViewModel viewModel;

    RecyclerView listItinerarios;
    List<ItinerarioPartidaDestino> itinerarios;
    ItinerarioTarifaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tarifas);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(TarifasViewModel.class);
        viewModel.itinerarios.observe(this, itinerariosObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Tarifas");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listItinerarios = binding.listItinerarios;

        adapter = new ItinerarioTarifaAdapter(itinerarios, this);

        listItinerarios.setAdapter(adapter);

    }

    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
            adapter.itinerarios = itinerarios;
            adapter.notifyDataSetChanged();
        }
    };

    public void onBtnSalvarClick(View v){

        itinerarios = viewModel.itinerarios.getValue();

        for(ItinerarioPartidaDestino i : itinerarios){

            if(i.isSelecionado()){
                System.out.println("ITI > "+i.getNomeCidadePartida()+" x "+i.getNomeCidadeDestino());
                i.setSelecionado(false);
            }

        }

        binding.editTextTarifa.setText("");


    }

}
