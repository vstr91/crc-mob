package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import br.com.vostre.circular.R;
import br.com.vostre.circular.view.form.FormUsuario;
import br.com.vostre.circular.databinding.ActivityUsuariosBinding;

public class UsuariosActivity extends BaseActivity {

    ActivityUsuariosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_usuarios);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Usuários");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void onFabClick(View v){
        FormUsuario formUsuario = new FormUsuario();
        formUsuario.show(getSupportFragmentManager(), "formUsuario");
    }
}
