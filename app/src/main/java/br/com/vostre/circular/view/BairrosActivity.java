package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityBairrosBinding;
import br.com.vostre.circular.databinding.ActivityEstadosBinding;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.view.form.FormEstado;

public class BairrosActivity extends BaseActivity {

    ActivityBairrosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bairros);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Bairros");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void onFabClick(View v){
        FormBairro formBairro = new FormBairro();
        formBairro.show(getSupportFragmentManager(), "formBairro");
    }

}
