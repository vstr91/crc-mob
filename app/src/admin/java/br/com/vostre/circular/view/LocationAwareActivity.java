package br.com.vostre.circular.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class LocationAwareActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onGpsChanged(boolean ativo) {
        super.onGpsChanged(ativo);

        if(ativo){
            Toast.makeText(getApplicationContext(), "GPS Ativo", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(getApplicationContext(), "GPS Inativo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRedeChanged(boolean ativo) {
        super.onGpsChanged(ativo);

        if(ativo){
            Toast.makeText(getApplicationContext(), "Rede Ativa", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(getApplicationContext(), "Rede Inativa", Toast.LENGTH_SHORT).show();
        }
    }

}