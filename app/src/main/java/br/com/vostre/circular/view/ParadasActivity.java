package br.com.vostre.circular.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityParadasBinding;
import br.com.vostre.circular.view.form.FormParada;

public class ParadasActivity extends BaseActivity {

    ActivityParadasBinding binding;
    MapView map;

    int permissionGPS;
    int permissionStorage;
    int permissionInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_paradas);
        super.onCreate(savedInstanceState);

        MultiplePermissionsListener listener = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(this)
                .withTitle("Permissões Negadas")
                .withMessage("Permita os acessos à Internet, armazenamento externo e GPS para acessar esta página")
                .build();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(listener)
                .check();

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissionStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionInternet = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET);

        if(permissionGPS != PackageManager.PERMISSION_GRANTED || permissionStorage != PackageManager.PERMISSION_GRANTED
                || permissionInternet != PackageManager.PERMISSION_GRANTED){

//            finish();
            Toast.makeText(getApplicationContext(),
                    "Para acessar esta tela, por favor aceite as permissões de acesso.",
                    Toast.LENGTH_SHORT).show();

        } else{
            binding.setView(this);
            setTitle("Paradas");
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            map = binding.map;
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);

            IMapController mapController = map.getController();
            mapController.setZoom(19);
            GeoPoint startPoint = new GeoPoint(-22.470804460339885, -43.82463455200195);
            mapController.setCenter(startPoint);
        }



    }

    public void onFabClick(View v){
        FormParada formParada = new FormParada();
        formParada.show(getSupportFragmentManager(), "formParada");
    }

}
