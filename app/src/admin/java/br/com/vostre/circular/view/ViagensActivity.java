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
import br.com.vostre.circular.databinding.ActivityViagensBinding;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.ViagemItinerario;
import br.com.vostre.circular.view.adapter.PaisAdapter;
import br.com.vostre.circular.view.adapter.ViagemItinerarioAdapter;
import br.com.vostre.circular.view.form.FormPais;
import br.com.vostre.circular.viewModel.PaisesViewModel;
import br.com.vostre.circular.viewModel.ViagensItinerarioViewModel;

public class ViagensActivity extends BaseActivity {

    ActivityViagensBinding binding;
    ViagensItinerarioViewModel viewModel;

    RecyclerView listViagens;
    List<ViagemItinerario> viagens;
    ViagemItinerarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_viagens);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ViagensItinerarioViewModel.class);
        viewModel.viagens.observe(this, viagensObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Viagens por Itinerário");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listViagens = binding.listViagens;

        adapter = new ViagemItinerarioAdapter(viagens, this);

        listViagens.setAdapter(adapter);

    }

    Observer<List<ViagemItinerario>> viagensObserver = new Observer<List<ViagemItinerario>>() {
        @Override
        public void onChanged(List<ViagemItinerario> viagens) {
            adapter.viagens = viagens;
            adapter.notifyDataSetChanged();
        }
    };

}
