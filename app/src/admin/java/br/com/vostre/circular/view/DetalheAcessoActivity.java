package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDashboardBinding;
import br.com.vostre.circular.databinding.ActivityDetalheAcessoBinding;

public class DetalheAcessoActivity extends BaseActivity {

    ActivityDetalheAcessoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhe_acesso);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Dashboard");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }
}
