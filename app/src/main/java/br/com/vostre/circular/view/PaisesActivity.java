package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityPaisesBinding;
import br.com.vostre.circular.view.form.FormPais;

public class PaisesActivity extends BaseActivity {

    ActivityPaisesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_paises);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Países");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void onFabClick(View v){
        FormPais formPais = new FormPais();
        formPais.show(getSupportFragmentManager(), "formPais");
    }

}
