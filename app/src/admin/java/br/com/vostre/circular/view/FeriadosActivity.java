package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityFeriadosBinding;
import br.com.vostre.circular.model.Feriado;
import br.com.vostre.circular.view.adapter.FeriadoAdapter;
import br.com.vostre.circular.viewModel.FeriadosViewModel;

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
