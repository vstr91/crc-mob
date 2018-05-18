package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityCidadesBinding;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.adapter.CidadeAdapter;
import br.com.vostre.circular.view.adapter.EstadoAdapter;
import br.com.vostre.circular.view.form.FormCidade;
import br.com.vostre.circular.viewModel.CidadesViewModel;
import br.com.vostre.circular.viewModel.EstadosViewModel;

public class CidadesActivity extends BaseActivity {

    ActivityCidadesBinding binding;
    CidadesViewModel viewModel;

    RecyclerView listCidades;
    List<CidadeEstado> cidades;
    CidadeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cidades);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(CidadesViewModel.class);
        viewModel.cidades.observe(this, cidadesObserver);

        binding.setView(this);
        setTitle("Cidades");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listCidades = binding.listCidades;

        adapter = new CidadeAdapter(cidades, this);

        listCidades.setAdapter(adapter);
    }

    public void onFabClick(View v){
        FormCidade formCidade = new FormCidade();
        formCidade.setCtx(getApplication());
        formCidade.flagInicioEdicao = false;
        formCidade.show(getSupportFragmentManager(), "formCidade");
    }

    Observer<List<CidadeEstado>> cidadesObserver = new Observer<List<CidadeEstado>>() {
        @Override
        public void onChanged(List<CidadeEstado> cidades) {
            adapter.cidades = cidades;
            adapter.notifyDataSetChanged();
        }
    };

}
