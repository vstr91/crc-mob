package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;

import br.com.vostre.circular.databinding.ActivityOpcoesBinding;

import br.com.vostre.circular.R;

public class OpcoesActivity extends BaseActivity {

    ActivityOpcoesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_opcoes);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Opções");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void onSwitchMapaChange(CompoundButton btn, boolean ativo){

        PreferenceUtils.salvarPreferencia(getApplicationContext(), "tela_inicial", ativo);

    }

}
