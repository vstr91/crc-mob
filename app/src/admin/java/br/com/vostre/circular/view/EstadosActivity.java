package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityEstadosBinding;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.adapter.EstadoAdapter;
import br.com.vostre.circular.view.form.FormEstado;
import br.com.vostre.circular.viewModel.EstadosViewModel;

public class EstadosActivity extends BaseActivity {

    ActivityEstadosBinding binding;
    EstadosViewModel viewModel;

    RecyclerView listEstados;
    List<Estado> estados;
    EstadoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_estados);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(EstadosViewModel.class);
        viewModel.estados.observe(this, estadosObserver);

        binding.setView(this);
        setTitle("Estados");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listEstados = binding.listEstados;

        adapter = new EstadoAdapter(estados, this);

        listEstados.setAdapter(adapter);
    }

    public void onFabClick(View v){
        FormEstado formEstado = new FormEstado();
        formEstado.setCtx(getApplication());
        formEstado.flagInicioEdicao = false;
        formEstado.show(getSupportFragmentManager(), "formEstado");
    }

    Observer<List<Estado>> estadosObserver = new Observer<List<Estado>>() {
        @Override
        public void onChanged(List<Estado> estados) {
            adapter.estados = estados;
            adapter.notifyDataSetChanged();
        }
    };

}
