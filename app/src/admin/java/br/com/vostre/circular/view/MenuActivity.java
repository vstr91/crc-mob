package br.com.vostre.circular.view;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityMenuBinding;
import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.DBUtils;
import br.com.vostre.circular.utils.DestaqueUtils;
import br.com.vostre.circular.viewModel.BaseViewModel;

public class MenuActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navView;
    ActivityMenuBinding binding;
    ActionBarDrawerToggle drawerToggle;

    // Constants
    // The authority for the sync adapter's content provider
    public static String AUTHORITY = "br.com.vostre.circular.admin.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "br.com.vostre.circular.admin";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;

    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60*5L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;
    // Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;

    int permissionStorage;

    static int PICK_FILE = 174;

    AppCompatActivity ctx;

    Location localAnterior;

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

        // Create the dummy account
        mAccount = CreateSyncAccount(this);

        // Get the content resolver for your app
        mResolver = getContentResolver();

        mAccount = new Account(ACCOUNT, ACCOUNT_TYPE);

        ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);

        /*
         * Turn on periodic syncing
         */
        ContentResolver.addPeriodicSync(
                new Account(ACCOUNT, ACCOUNT_TYPE),
                AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL);

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

//        DestaqueUtils.geraDestaqueUnico(this, binding.button, "Outro", "Teste modular", new TapTargetView.Listener(){
//            @Override
//            public void onTargetClick(TapTargetView view) {
//                super.onTargetClick(view);
//                onClickBtnPaises(view);
//            }
//        });

//        List<TapTarget> targets = new ArrayList<>();
//
//        targets.add(DestaqueUtils.geraTapTarget(binding.button2, "Novo teste!", "Pressione esta opção para ter acesso a outros dados!", false));
//        targets.add(DestaqueUtils.geraTapTarget(binding.button3, "Botao 3", "Botao tres texto", false));
//        targets.add(DestaqueUtils.geraTapTarget(binding.button4, "Botao 4", "Botao quatro texto", false));
//
//        DestaqueUtils.geraSequenciaDestaques(this, targets, new TapTargetSequence.Listener() {
//            @Override
//            public void onSequenceFinish() {
//                Toast.makeText(getApplicationContext(), "Terminou!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
//                Toast.makeText(getApplicationContext(), "Um passo", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onSequenceCanceled(TapTarget lastTarget) {
//                Toast.makeText(getApplicationContext(), "Cancelou...", Toast.LENGTH_SHORT).show();
//            }
//        });

        viewModel.localAtual.observe(this, localObserver);
        viewModel.iniciarAtualizacoesPosicao();
        localAnterior = new Location(LocationManager.GPS_PROVIDER);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            startLocationUpdates();
        }

        binding.imageView2.getViewTreeObserver().addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                YoYo.with(Techniques.SlideInDown)
                        .duration(700)
                        .pivot(findViewById(R.id.imageView2).getX()/2,findViewById(R.id.imageView2).getY()/2)
                        .playOn(findViewById(R.id.imageView2));
            }
        });



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

        CropImage.activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

//        Intent intentFile = new Intent();
//        intentFile.setType("text/*");
//        intentFile.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intentFile, "Escolha o arquivo de dados"), PICK_FILE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            System.out.println("AQUI!!!!");
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            System.out.println("EXISTE!!!!");
        }

        return null;

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
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(new Account(ACCOUNT, ACCOUNT_TYPE), AUTHORITY, settingsBundle);
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

                Intent i = new Intent(getApplicationContext(), CameraResultadoActivity.class);
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
