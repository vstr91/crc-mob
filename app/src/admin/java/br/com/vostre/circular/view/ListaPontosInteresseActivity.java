package br.com.vostre.circular.view;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityListaPontosInteresseBinding;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseBairro;
import br.com.vostre.circular.view.adapter.ParadaEscolhaAdapter;
import br.com.vostre.circular.view.adapter.PontoInteresseEscolhaAdapter;
import br.com.vostre.circular.viewModel.PushViewModel;

public class ListaPontosInteresseActivity extends BaseActivity {

    ActivityListaPontosInteresseBinding binding;
    PushViewModel viewModel;

    RecyclerView listPontosInteresse;
    List<PontoInteresseBairro> pontosInteresse;
    PontoInteresseEscolhaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lista_pontos_interesse);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(PushViewModel.class);
        viewModel.pois.observe(this, poisObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
//        setTitle("ListarItinerarios");
//        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listPontosInteresse = binding.listPontosInteresse;

        adapter = new PontoInteresseEscolhaAdapter(pontosInteresse, this);

        listPontosInteresse.setAdapter(adapter);

    }

    Observer<List<PontoInteresseBairro>> poisObserver = new Observer<List<PontoInteresseBairro>>() {
        @Override
        public void onChanged(List<PontoInteresseBairro> pois) {
            adapter.pois = pois;
            adapter.notifyDataSetChanged();
        }
    };

}
