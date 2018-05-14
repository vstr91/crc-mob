package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityEmpresasBinding;
import br.com.vostre.circular.databinding.ActivityPaisesBinding;
import br.com.vostre.circular.view.form.FormEmpresa;
import br.com.vostre.circular.view.form.FormPais;

public class EmpresasActivity extends BaseActivity {

    ActivityEmpresasBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_empresas);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Empresas");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void onFabClick(View v){
        FormEmpresa formEmpresa = new FormEmpresa();
        formEmpresa.show(getSupportFragmentManager(), "formEmpresa");
    }
}
