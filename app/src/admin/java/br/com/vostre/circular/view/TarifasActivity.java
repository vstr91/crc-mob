package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityTarifasBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.adapter.ItinerarioTarifaAdapter;
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
        List<ItinerarioPartidaDestino> itis = new ArrayList<>();

        for(ItinerarioPartidaDestino i : itinerarios){

            if(i.isSelecionado()){
                itis.add(i);
            }

        }

        if(itis.size() > 0 || !binding.editTextTarifa.getText().toString().isEmpty()){
            Double tarifa = Double.parseDouble(binding.editTextTarifa.getText().toString().replace(".", "").replace(",", "."));
            viewModel.edit(itis, tarifa);

            for(ItinerarioPartidaDestino i : itinerarios){
                i.setSelecionado(false);
            }

            binding.editTextTarifa.setText("");
            adapter.itinerarios = itinerarios;
            adapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(), "Tarifas alteradas!", Toast.LENGTH_SHORT).show();

        } else{
            Toast.makeText(getApplicationContext(), "Ao menos um itinerário deve ser selecionado e a tarifa deve ser informada.", Toast.LENGTH_SHORT).show();
        }


    }

}
