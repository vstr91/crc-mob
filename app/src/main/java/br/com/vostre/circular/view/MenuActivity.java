package br.com.vostre.circular.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityMenuBinding;

public class MenuActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navView;
    ActivityMenuBinding binding;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu);
        super.onCreate(savedInstanceState);
        binding.setView(this);

        drawer = binding.container;
        navView = binding.nav;

        navView.setNavigationItemSelectedListener(this);

        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0){

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                drawerToggle.syncState();
            }

            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                drawerToggle.syncState();
            }

        };

        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }

    public void onClickBtnPaises(View v){
        Intent i = new Intent(getApplicationContext(), PaisesActivity.class);
        startActivity(i);
    }

    public void onClickBtnEstados(View v){
        Toast.makeText(getApplicationContext(), "Clicou botão Estados", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnCidades(View v){
        Toast.makeText(getApplicationContext(), "Clicou botão Cidades", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnBairros(View v){
        Toast.makeText(getApplicationContext(), "Clicou botão Bairros", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnParadas(View v){
        Toast.makeText(getApplicationContext(), "Clicou botão Paradas", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnItinerarios(View v){
        Toast.makeText(getApplicationContext(), "Clicou botão Itinerarios", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnPontosInteresse(View v){
        Toast.makeText(getApplicationContext(), "Clicou botão Pontos de Interesse", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnEmpresas(View v){
        Toast.makeText(getApplicationContext(), "Clicou botão Empresas", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnParametros(View v){
        Toast.makeText(getApplicationContext(), "Clicou botão Parametros", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnUsuarios(View v){
        Toast.makeText(getApplicationContext(), "Clicou botão Usuarios", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
