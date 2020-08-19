package br.com.vostre.circular.view;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityListaItinerariosBinding;
import br.com.vostre.circular.databinding.ActivityPaisesBinding;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioEscolhaAdapter;
import br.com.vostre.circular.view.adapter.PaisAdapter;
import br.com.vostre.circular.view.form.FormPais;
import br.com.vostre.circular.viewModel.PaisesViewModel;
import br.com.vostre.circular.viewModel.PushViewModel;

public class ListaItinerariosActivity extends BaseActivity {

    ActivityListaItinerariosBinding binding;
    PushViewModel viewModel;

    RecyclerView listItinerarios;
    List<ItinerarioPartidaDestino> itinerarios;
    ItinerarioEscolhaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lista_itinerarios);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(PushViewModel.class);
        viewModel.itinerarios.observe(this, itinerariosObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
//        setTitle("ListarItinerarios");
//        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listItinerarios = binding.listItinerarios;

        adapter = new ItinerarioEscolhaAdapter(itinerarios, this);

        listItinerarios.setAdapter(adapter);

    }

    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
            adapter.itinerarios = itinerarios;
            adapter.notifyDataSetChanged();
        }
    };

}
