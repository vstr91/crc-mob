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

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;
import java.util.TimeZone;

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

        JodaTimeAndroid.init(this);

    }

    public void onClickBtnPaises(View v){
        Intent i = new Intent(getApplicationContext(), PaisesActivity.class);
        startActivity(i);
    }

    public void onClickBtnEstados(View v){
        Intent i = new Intent(getApplicationContext(), EstadosActivity.class);
        startActivity(i);
    }

    public void onClickBtnCidades(View v){
        Intent i = new Intent(getApplicationContext(), CidadesActivity.class);
        startActivity(i);
    }

    public void onClickBtnBairros(View v){
        Intent i = new Intent(getApplicationContext(), BairrosActivity.class);
        startActivity(i);
    }

    public void onClickBtnParadas(View v){
        Intent i = new Intent(getApplicationContext(), ParadasActivity.class);
        startActivity(i);
    }

    public void onClickBtnItinerarios(View v){
        Intent i = new Intent(getApplicationContext(), ItinerariosActivity.class);
        startActivity(i);
    }

    public void onClickBtnPontosInteresse(View v){
        Intent i = new Intent(getApplicationContext(), PontosInteresseActivity.class);
        startActivity(i);
    }

    public void onClickBtnEmpresas(View v){
        Intent i = new Intent(getApplicationContext(), EmpresasActivity.class);
        startActivity(i);
    }

    public void onClickBtnParametros(View v){
        Intent i = new Intent(getApplicationContext(), ParametrosActivity.class);
        startActivity(i);
    }

    public void onClickBtnUsuarios(View v){
        Intent i = new Intent(getApplicationContext(), UsuariosActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
