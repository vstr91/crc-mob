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
import br.com.vostre.circular.databinding.ActivityBairrosBinding;
import br.com.vostre.circular.databinding.ActivityEstadosBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.view.adapter.BairroAdapter;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.view.form.FormEstado;
import br.com.vostre.circular.viewModel.BairrosViewModel;

public class BairrosActivity extends BaseActivity {

    ActivityBairrosBinding binding;
    BairrosViewModel viewModel;

    RecyclerView listBairros;
    List<BairroCidade> bairros;
    BairroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bairros);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(BairrosViewModel.class);
        viewModel.bairros.observe(this, bairrosObserver);

        binding.setView(this);
        setTitle("Bairros");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listBairros = binding.listBairros;

        adapter = new BairroAdapter(bairros, this);

        listBairros.setAdapter(adapter);
    }

    public void onFabClick(View v){
        FormBairro formBairro = new FormBairro();
        formBairro.setCtx(getApplication());
        formBairro.flagInicioEdicao = false;
        formBairro.show(getSupportFragmentManager(), "formBairro");
    }

    Observer<List<BairroCidade>> bairrosObserver = new Observer<List<BairroCidade>>() {
        @Override
        public void onChanged(List<BairroCidade> bairros) {
            adapter.bairros = bairros;
            adapter.notifyDataSetChanged();
        }
    };

}
