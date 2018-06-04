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
import br.com.vostre.circular.databinding.ActivityEmpresasBinding;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.view.adapter.EmpresaAdapter;
import br.com.vostre.circular.view.form.FormEmpresa;
import br.com.vostre.circular.viewModel.EmpresasViewModel;

public class EmpresasActivity extends BaseActivity {

    ActivityEmpresasBinding binding;
    EmpresasViewModel viewModel;

    RecyclerView listEmpresas;
    List<Empresa> empresas;
    EmpresaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_empresas);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(EmpresasViewModel.class);
        viewModel.empresas.observe(this, empresasObserver);

        binding.setView(this);
        setTitle("Empresas");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listEmpresas = binding.listEmpresas;

        adapter = new EmpresaAdapter(empresas, this);

        listEmpresas.setAdapter(adapter);

    }

    public void onFabClick(View v){
        FormEmpresa formEmpresa = new FormEmpresa();
        formEmpresa.flagInicioEdicao = false;
        formEmpresa.setCtx(getApplication());
        formEmpresa.show(getSupportFragmentManager(), "formEmpresa");
    }

    Observer<List<Empresa>> empresasObserver = new Observer<List<Empresa>>() {
        @Override
        public void onChanged(List<Empresa> empresas) {
            adapter.empresas = empresas;
            adapter.notifyDataSetChanged();
        }
    };

}
