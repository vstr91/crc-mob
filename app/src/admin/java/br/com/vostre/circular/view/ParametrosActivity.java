package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityParametrosBinding;
import br.com.vostre.circular.model.Parametro;
import br.com.vostre.circular.view.adapter.ParametroAdapter;
import br.com.vostre.circular.view.form.FormParametro;
import br.com.vostre.circular.viewModel.ParametrosViewModel;

public class ParametrosActivity extends BaseActivity {

    ActivityParametrosBinding binding;
    ParametrosViewModel viewModel;

    RecyclerView listParametros;
    List<Parametro> parametros;
    ParametroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_parametros);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ParametrosViewModel.class);
        viewModel.parametros.observe(this, parametrosObserver);

        binding.setView(this);
        setTitle("Parâmetros");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listParametros = binding.listParametros;

        adapter = new ParametroAdapter(parametros, this);

        listParametros.setAdapter(adapter);

    }

    public void onFabClick(View v){
        FormParametro formParametro = new FormParametro();
        formParametro.flagInicioEdicao = false;
        formParametro.show(getSupportFragmentManager(), "formParametro");
    }

    Observer<List<Parametro>> parametrosObserver = new Observer<List<Parametro>>() {
        @Override
        public void onChanged(List<Parametro> parametros) {
            adapter.parametros = parametros;
            adapter.notifyDataSetChanged();
        }
    };

}
