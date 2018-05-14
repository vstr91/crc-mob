package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityPaisesBinding;
import br.com.vostre.circular.model.Pais;
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
        viewModel.carregarPaises().observe(this, paisesObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Países");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listPaises = binding.listPaises;

        adapter = new PaisAdapter(paises);

        listPaises.setAdapter(adapter);

    }

    public void onFabClick(View v){
        FormPais formPais = new FormPais();
        formPais.show(getSupportFragmentManager(), "formPais");
    }

    Observer<List<Pais>> paisesObserver = new Observer<List<Pais>>() {
        @Override
        public void onChanged(List<Pais> paises) {
            System.out.println(paises.size());
            adapter.paises = paises;
            adapter.notifyDataSetChanged();
        }
    };

}
