package br.com.vostre.circular.view;

import android.databinding.DataBindingUtil;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import br.com.vostre.circular.R;

public class MenuActivity extends BaseActivity {

    DrawerLayout drawer;
//    ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_menu);
       // binding = DataBindingUtil.setContentView(this, R.layout.layout_main);
        super.onCreate(savedInstanceState);
    }

    public void onClickBtnPaises(View v){
        Toast.makeText(getApplicationContext(), "Clicou botão Paises", Toast.LENGTH_SHORT).show();
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

}
