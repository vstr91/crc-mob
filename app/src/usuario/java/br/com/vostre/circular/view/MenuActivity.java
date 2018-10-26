package br.com.vostre.circular.view;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.sql.SQLOutput;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import br.com.vostre.circular.App;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityMenuBinding;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.api.CircularAPI;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.ToolbarUtils;
import br.com.vostre.circular.viewModel.BaseViewModel;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterDirectedGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MenuActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navView;
    ActivityMenuBinding binding;
    ActionBarDrawerToggle drawerToggle;

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "br.com.vostre.circular.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "br.com.vostre.circular.usuario";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;

    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60*1L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;
    // Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;

    int permissionStorage;
    Location localAnterior;

    AppCompatActivity ctx;
    ParadaBairro paradaAtual;

    Handler handler;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;

    ProgressBar progressBar;
    SignInButton btnLogin;

    static int RC_SIGN_IN = 450;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu);
        super.onCreate(savedInstanceState);
        binding.setView(this);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if(report.areAllPermissionsGranted()){
                    configuraActivity();
                } else{
                    Toast.makeText(getApplicationContext(), "Acesso ao GPS e armazenamento interno é necessário para aproveitar ao máximo o Circular!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        })
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

//        SignInButton btnLogin = drawer.findViewById(R.id.btnLogin);
//
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(ctx, "Login!", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void configuraActivity(){

//        if(PreferenceUtils.carregarPreferenciaBoolean(getApplicationContext(), "tela_inicial")){
//            onClickBtnMapa(null);
//        }

        progressBar = binding.progressBar;
        btnLogin = binding.btnLogin;

        viewModel = ViewModelProviders.of(this).get(BaseViewModel.class);
        viewModel.localAtual.observe(this, localObserver);
        viewModel.iniciarAtualizacoesPosicao();
        localAnterior = new Location(LocationManager.GPS_PROVIDER);
        ctx = this;
        binding.textView36.setVisibility(View.GONE);

        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        startLocationUpdates();

    }

    public void onClickBtnItinerarios(View v){
        Intent i = new Intent(getApplicationContext(), ItinerariosActivity.class);
        startActivity(i);
    }

    public void onClickBtnParadas(View v){
        Intent i = new Intent(getApplicationContext(), ParadasActivity.class);
        startActivity(i);
    }

    public void onClickBtnMapa(View v){
        Intent i = new Intent(getApplicationContext(), MapaActivity.class);
        startActivity(i);
    }

    public void onClickBtnQRCode(View v){
        Intent i = new Intent(getApplicationContext(), QRCodeActivity.class);
        startActivity(i);
    }

    public void onClickBtnLogo(View v){

        if(paradaAtual != null){
            Intent i = new Intent(ctx, DetalheParadaActivity.class);
            i.putExtra("parada", paradaAtual.getParada().getId());
            startActivity(i);
        }

    }

    public void onClickBtnLogin(View v){

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        btnLogin.setEnabled(false);

    }

    public void onClickBtnSair(View v){
        signOut();
    }

    public void onClickBtnOpcoes(View v){
        drawer.closeDrawers();
        Intent i = new Intent(getApplicationContext(), OpcoesActivity.class);
        startActivity(i);
    }

    public void onClickBtnSobre(View v){
        drawer.closeDrawers();
        Intent i = new Intent(getApplicationContext(), SobreActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account){

        if(account != null){

            String id = PreferenceUtils.carregarUsuarioLogado(getApplicationContext());

            binding.btnLogin.setVisibility(View.GONE);
            binding.textViewEmail.setText(account.getEmail()+" | "+id);
            binding.textViewEmail.setVisibility(View.VISIBLE);
            binding.textView34.setVisibility(View.VISIBLE);
            binding.btnSair.setVisibility(View.VISIBLE);
        } else{
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.textViewEmail.setText("");
            binding.textViewEmail.setVisibility(View.GONE);
            binding.textView34.setVisibility(View.GONE);
            binding.btnSair.setVisibility(View.GONE);
        }

    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
        PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "");
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
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }

        return null;

    }

    private boolean checarPermissoes(){
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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
        configuraActivity();

    }

    @Override
    protected void onPause() {
        super.onPause();

        stopLocationUpdates();

        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            viewModel.validaUsuario(idToken, account.getId());
            viewModel.usuarioValidado.observe(this, loginObserver);

            this.account = account;

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SIGN", "signInResult:failed code=" + e.getStatusCode()+" | "+e.getMessage());
            updateUI(null);
            btnLogin.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Erro ao efeutar login: "+e.getMessage()+". Por favor tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }

    Observer<Boolean> loginObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean logado) {

            if(logado){
                updateUI(account);
            } else{
                updateUI(null);
                signOut();
            }

            if(flag){
                btnLogin.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }

            flag = true;

        }
    };

    Observer<Location> localObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location local) {

            if(local.getLatitude() != 0.0 && local.getLongitude() != 0.0 && local.distanceTo(localAnterior) > 20){
                localAnterior = local;

                if(!viewModel.isRunningNearPlaces){
                    viewModel.buscaParadasProximas(local);
                    viewModel.paradas.observe(ctx, paradasObserver);
                }

            }

        }
    };

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {

            viewModel.isRunningNearPlaces = false;

            //double latitude = -22.470612;
            //double longitude = -43.8263613;

            Location l0 = new Location(LocationManager.GPS_PROVIDER);
            //l0.setLatitude(latitude);
            //l0.setLongitude(longitude);

            adicionaListaALogo(paradas, l0);

        }
    };

    private void adicionaListaALogo(final List<ParadaBairro> paradas, final Location localAtual){

        final List<ParadaBairro> myParadas = paradas;
        final Location myLocation = localAtual;

        final NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(0);

        if(paradas != null && paradas.size() > 0){

            handler = new Handler();

            int cont = 1;
            int total = paradas.size();
            int delay = 0;

            for(final ParadaBairro p : paradas){

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Location l = new Location(LocationManager.GPS_PROVIDER);
                        l.setLatitude(p.getParada().getLatitude());
                        l.setLongitude(p.getParada().getLongitude());

                        if(p.getParada().getImagem() != null && !p.getParada().getImagem().equals("")){

                            final File foto = new File(getApplication().getFilesDir(), p.getParada().getImagem());

                            if(foto.exists() && foto.canRead()){
                                final Drawable drawable = Drawable.createFromPath(foto.getAbsolutePath());
                                binding.circleView.setImagem(drawable);

                            } else{
                                binding.circleView.setImagem(null);
                                binding.circleView.invalidate();
                            }

                        }

                        String distancia = nf.format(l.distanceTo(localAtual));

                        binding.textViewParada.setText(p.getParada().getNome());
                        binding.textViewDistancia.setText("~"+distancia+" m");

                        if(binding.textViewParada.getVisibility() == View.GONE){
                            binding.textViewParada.setVisibility(View.VISIBLE);
                        }

                        if(binding.textViewDistancia.getVisibility() == View.GONE){
                            binding.textViewDistancia.setVisibility(View.VISIBLE);
                        }

                        if(binding.textView36.getVisibility() == View.GONE){
                            binding.textView36.setVisibility(View.VISIBLE);
                        }

                        paradaAtual = p;

//                System.out.println("PARADA PROXIMA:: "+p.getParada().getNome()+" - "+p.getParada().getLatitude()+" | "
//                        +p.getParada().getLongitude()+" || Distancia: "+l.distanceTo(localAtual));
                    }
                }, delay * 1000);

                delay = delay + 5;
                cont++;

                if(cont == total){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adicionaListaALogo(myParadas, myLocation);
                        }
                    }, delay * 1000 + 5000);
                }

            }
        } else{
            binding.textViewParada.setVisibility(View.GONE);
            binding.textViewDistancia.setVisibility(View.GONE);
            binding.textView36.setVisibility(View.GONE);
            binding.circleView.setImagem(null);
            binding.circleView.invalidate();
        }



    }

}
