package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityPaisesBinding;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.adapter.PaisAdapter;
import br.com.vostre.circular.view.form.FormPais;
import br.com.vostre.circular.viewModel.PaisesViewModel;

public class PaisesActivity extends BaseActivity {

    ActivityPaisesBinding binding;
    PaisesViewModel viewModel;

    RecyclerView listPaises;
    List<Pais> paises;
    PaisAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_paises);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(PaisesViewModel.class);
        viewModel.paises.observe(this, paisesObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Países");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listPaises = binding.listPaises;

        adapter = new PaisAdapter(paises, this);

        listPaises.setAdapter(adapter);

    }

    public void onFabClick(View v){
        FormPais formPais = new FormPais();
        formPais.flagInicioEdicao = false;
        formPais.show(getSupportFragmentManager(), "formPais");
//        viewModel.retorno.observe(this, retornoObserver);
    }

    Observer<List<Pais>> paisesObserver = new Observer<List<Pais>>() {
        @Override
        public void onChanged(List<Pais> paises) {
            adapter.paises = paises;
            adapter.notifyDataSetChanged();
        }
    };

}
