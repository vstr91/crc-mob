package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityEstadosBinding;
import br.com.vostre.circular.view.form.FormEstado;
import br.com.vostre.circular.view.form.FormPais;

public class EstadosActivity extends BaseActivity {

    ActivityEstadosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_estados);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Estados");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void onFabClick(View v){
        FormEstado formEstado = new FormEstado();
        formEstado.show(getSupportFragmentManager(), "formEstado");
    }
}
