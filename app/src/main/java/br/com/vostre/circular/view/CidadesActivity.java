package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityCidadesBinding;
import br.com.vostre.circular.view.form.FormCidade;

public class CidadesActivity extends BaseActivity {

    ActivityCidadesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cidades);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Cidades");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void onFabClick(View v){
        FormCidade formCidade = new FormCidade();
        formCidade.show(getSupportFragmentManager(), "formCidade");
    }

}
