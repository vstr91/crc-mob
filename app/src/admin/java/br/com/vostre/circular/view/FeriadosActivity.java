package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityFeriadosBinding;
import br.com.vostre.circular.databinding.ActivityPaisesBinding;
import br.com.vostre.circular.model.Feriado;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.api.CircularAPI;
import br.com.vostre.circular.view.adapter.FeriadoAdapter;
import br.com.vostre.circular.view.adapter.PaisAdapter;
import br.com.vostre.circular.view.form.FormPais;
import br.com.vostre.circular.viewModel.FeriadosViewModel;
import br.com.vostre.circular.viewModel.PaisesViewModel;

public class FeriadosActivity extends BaseActivity {

    ActivityFeriadosBinding binding;
    FeriadosViewModel viewModel;

    RecyclerView listFeriados;
    List<Feriado> feriados;
    FeriadoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feriados);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(FeriadosViewModel.class);
        viewModel.feriados.observe(this, feriadosObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Feriados");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listFeriados = binding.listFeriados;

        adapter = new FeriadoAdapter(feriados, this);

        listFeriados.setAdapter(adapter);

    }

    public void onFabClick(View v){
        viewModel.carregaFeriados();
    }

    Observer<List<Feriado>> feriadosObserver = new Observer<List<Feriado>>() {
        @Override
        public void onChanged(List<Feriado> feriados) {
            adapter.feriados = feriados;
            adapter.notifyDataSetChanged();
        }
    };

}
