package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityEmpresasBinding;
import br.com.vostre.circular.databinding.ActivityParametrosBinding;
import br.com.vostre.circular.view.form.FormEmpresa;
import br.com.vostre.circular.view.form.FormParametro;

public class ParametrosActivity extends BaseActivity {

    ActivityParametrosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_parametros);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Parâmetros");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void onFabClick(View v){
        FormParametro formParametro = new FormParametro();
        formParametro.show(getSupportFragmentManager(), "formParametro");
    }
}
