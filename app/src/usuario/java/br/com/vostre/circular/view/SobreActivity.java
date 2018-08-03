package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivitySobreBinding;

public class SobreActivity extends BaseActivity {

    ActivitySobreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sobre);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Sobre");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }
}
