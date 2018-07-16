package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Usuario;
import br.com.vostre.circular.databinding.ActivityUsuariosBinding;
import br.com.vostre.circular.view.adapter.UsuarioAdapter;
import br.com.vostre.circular.view.form.FormUsuario;
import br.com.vostre.circular.viewModel.UsuariosViewModel;

public class UsuariosActivity extends BaseActivity {

    ActivityUsuariosBinding binding;
    UsuariosViewModel viewModel;

    RecyclerView listUsuarios;
    List<Usuario> usuarios;
    UsuarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_usuarios);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(UsuariosViewModel.class);
        viewModel.usuarios.observe(this, usuariosObserver);

        binding.setView(this);
        setTitle("Usuários");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listUsuarios = binding.listUsuarios;

        adapter = new UsuarioAdapter(usuarios, this);

        listUsuarios.setAdapter(adapter);

    }

    public void onFabClick(View v){
        FormUsuario formUsuario = new FormUsuario();
        formUsuario.flagInicioEdicao = false;
        formUsuario.show(getSupportFragmentManager(), "formUsuario");
    }

    Observer<List<Usuario>> usuariosObserver = new Observer<List<Usuario>>() {
        @Override
        public void onChanged(List<Usuario> usuarios) {
            adapter.usuarios = usuarios;
            adapter.notifyDataSetChanged();
        }
    };

}
