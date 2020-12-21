package br.com.vostre.circular.view;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;

import androidx.constraintlayout.solver.state.State;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.joda.time.DateTime;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityMenuBinding;
import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.works.SyncWorker;
import br.com.vostre.circular.utils.APIUtils;
import br.com.vostre.circular.utils.DBUtils;
import br.com.vostre.circular.utils.WorksUtils;
import br.com.vostre.circular.viewModel.BaseViewModel;

public class MenuActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navView;
    ActivityMenuBinding binding;
    ActionBarDrawerToggle drawerToggle;

    int permissionStorage;

    static int PICK_FILE = 174;

    AppCompatActivity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu);
        super.onCreate(savedInstanceState);
        binding.setView(this);

        MultiplePermissionsListener listener = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(this)
                .withTitle("Permissões Negadas")
                .withMessage("Permita os acessos à Internet, armazenamento externo e GPS para acessar esta página")
                .build();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                ).withListener(listener)
                .check();

        permissionStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

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

        viewModel = ViewModelProviders.of(this).get(BaseViewModel.class);

        viewModel.parametrosInternos.observe(this, parametrosInternosObserver);

        ctx = this;

        viewModel.localAtual.observe(this, localObserver);
        viewModel.iniciarAtualizacoesPosicao();
        localAnterior = new Location(LocationManager.GPS_PROVIDER);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            startLocationUpdates();
        }

//        binding.imageView2.getViewTreeObserver().addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                YoYo.with(Techniques.SlideInDown)
//                        .duration(700)
//                        .pivot(findViewById(R.id.imageView2).getX()/2,findViewById(R.id.imageView2).getY()/2)
//                        .playOn(findViewById(R.id.imageView2));
//            }
//        });

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // inicia atualizacao periodica
        WorksUtils.iniciaWorkAtualizacao(getApplicationContext(), 60, "sync", constraints);

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

    public void onClickBtnServicos(View v){
        Intent i = new Intent(getApplicationContext(), ServicosActivity.class);
        startActivity(i);
    }

    public void onClickBtnUsuarios(View v){
        Intent i = new Intent(getApplicationContext(), UsuariosActivity.class);
        startActivity(i);
    }

    public void onClickBtnFeriados(View v){
        Intent i = new Intent(getApplicationContext(), FeriadosActivity.class);
        startActivity(i);
    }

    public void onClickBtnPush(View v){

        Intent i = new Intent(getApplicationContext(), PushActivity.class);
        startActivity(i);

//        String dados = "{" +
//                "\"app_id\": \"02ec2fb2-4df1-41c4-828e-4db1a7247276\"," +
//                "\"included_segments\": [\"All\"],"+
//                "\"data\": {\"atualizar\": \"1\", \"mostrar\": \"1\"}, "+
//                "\"contents\": {\"en\": \"Mensagem de Teste para todos os idiomas!\"}"+
//                "}";
//
//        APIUtils.enviaNotificacaoPush(getApplicationContext(), dados);
    }

    public void onClickBtnSobre(View v){
        drawer.closeDrawers();
        Intent i = new Intent(getApplicationContext(), SobreActivity.class);
        startActivity(i);
    }

    public void onClickBtnExportar(View v){
        Toast.makeText(getApplicationContext(), "Exportando Dados...", Toast.LENGTH_SHORT).show();
        DBUtils.exportDB(getApplicationContext());
    }

    public void onClickBtnDashboard(View v){
        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(i);
    }

    public void onClickBtnCamera(View v){
//        Intent i = new Intent(getApplicationContext(), CameraActivity.class);
//        startActivity(i);

        // EM USO
//        CropImage.activity(null)
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .start(this);

//        Intent intentFile = new Intent();
//        intentFile.setType("text/*");
//        intentFile.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intentFile, "Escolha o arquivo de dados"), PICK_FILE);

        Intent i = new Intent(getApplicationContext(), MapaActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    Observer<List<ParametroInterno>> parametrosInternosObserver = new Observer<List<ParametroInterno>>() {
        @Override
        public void onChanged(List<ParametroInterno> parametros) {

            if(parametros.size() > 0){
                ParametroInterno parametro = parametros.get(0);

                if(parametro.getDataUltimoAcesso().isBefore(DateTime.now().minusHours(1))){
                    requisitaAtualizacao();
                }

            } else{
                requisitaAtualizacao();
            }



        }
    };

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {

            if(paradas.size() > 0){

                Location poiLocation = new Location(LocationManager.GPS_PROVIDER);
                poiLocation.setLatitude(localAnterior.getLatitude());
                poiLocation.setLongitude(localAnterior.getLongitude());

                for(ParadaBairro i : paradas){
                    Location paradaLocation = new Location(LocationManager.GPS_PROVIDER);
                    paradaLocation.setLatitude(i.getParada().getLatitude());
                    paradaLocation.setLongitude(i.getParada().getLongitude());

                    i.setDistancia(paradaLocation.distanceTo(poiLocation));
                }

                Collections.sort(paradas, new Comparator<ParadaBairro>() {
                    @Override
                    public int compare(ParadaBairro itinerarioPartidaDestino, ParadaBairro t1) {
                        return itinerarioPartidaDestino.getDistancia() > t1.getDistancia() ? 1 : -1;
                    }
                });



                binding.textViewBairroAtual.setText(paradas.get(0).getNomeBairroComCidade());
                binding.textViewBairroAtual.setVisibility(View.VISIBLE);
            } else{
                binding.textViewBairroAtual.setVisibility(View.GONE);
            }



        }
    };
//
//    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
//        @Override
//        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
//
//            for(ItinerarioPartidaDestino i : itinerarios){
//                System.out.println("ITINERARIOS: "+i.getItinerario().getId()+" | "+i.getNomePartida()+", "+i.getNomeBairroPartida()+" - "+i.getNomeDestino()+", "+i.getNomeBairroDestino());
//            }
//
//        }
//    };

    Observer<Location> localObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location local) {

            if(local.getLatitude() != 0.0 && local.getLongitude() != 0.0 && local.distanceTo(localAnterior) > 20){
                localAnterior = local;

                if(!viewModel.isRunningNearPlaces){
                    viewModel.buscarParadasProximas(getApplicationContext(), local);
                    viewModel.paradas.observe(ctx, paradasObserver);
                }

            }

        }
    };

    private void requisitaAtualizacao(){
        WorksUtils.iniciaWorkAtualizacaoSingle(getApplicationContext(), "sync-single");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_FILE) {

            if(data != null){
                try {
                    InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());

                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                    Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                Intent i = new Intent(getApplicationContext(), CameraResultadoOnibusActivity.class);
                    i.putExtra("imagem", resultUri.toString());
                    startActivity(i);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private boolean checarPermissoes(){
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onGpsChanged(boolean ativo) {

        if(ativo){
            //binding.textViewBairroAtual.setVisibility(View.VISIBLE);
            viewModel.buscarParadasProximas(getApplicationContext(), localAnterior);
        } else{
            binding.textViewBairroAtual.setVisibility(View.GONE);
        }

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        if(checarPermissoes()){

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setMaxWaitTime(5000);

            if(viewModel != null){
                viewModel.mFusedLocationClient.requestLocationUpdates(locationRequest,
                        viewModel.mLocationCallback,
                        null);
            }

        }

    }

    private void stopLocationUpdates() {

        if(viewModel != null && viewModel.mFusedLocationClient != null){
            viewModel.mFusedLocationClient.removeLocationUpdates(viewModel.mLocationCallback);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();

    }

}
