package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityBairrosBinding;

public class ParadasActivity extends AppCompatActivity {

    ActivityBairrosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_paradas);
        super.onCreate(savedInstanceState);
        //binding.setView(this);
        setTitle("Paradas");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }
}
