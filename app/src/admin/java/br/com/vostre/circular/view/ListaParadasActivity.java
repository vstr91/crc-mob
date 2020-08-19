package br.com.vostre.circular.view;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityListaParadasBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.adapter.ItinerarioEscolhaAdapter;
import br.com.vostre.circular.view.adapter.ParadaEscolhaAdapter;
import br.com.vostre.circular.viewModel.PushViewModel;

public class ListaParadasActivity extends BaseActivity {

    ActivityListaParadasBinding binding;
    PushViewModel viewModel;

    RecyclerView listParadas;
    List<ParadaBairro> paradas;
    ParadaEscolhaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lista_paradas);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(PushViewModel.class);
        viewModel.paradas.observe(this, paradasObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
//        setTitle("ListarItinerarios");
//        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listParadas = binding.listParadas;

        adapter = new ParadaEscolhaAdapter(paradas, this);

        listParadas.setAdapter(adapter);

    }

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            adapter.paradas = paradas;
            adapter.notifyDataSetChanged();
        }
    };

}
