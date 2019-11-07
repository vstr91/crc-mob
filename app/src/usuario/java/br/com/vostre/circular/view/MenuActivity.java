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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.JsonObject;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import br.com.vostre.circular.App;
import br.com.vostre.circular.BuildConfig;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityMenuBinding;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Parametro;
import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.api.CircularAPI;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.BadgeDrawerToggle;
import br.com.vostre.circular.utils.DBUtils;
import br.com.vostre.circular.utils.DestaqueUtils;
import br.com.vostre.circular.utils.JsonUtils;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.ToolbarUtils;
import br.com.vostre.circular.utils.Unique;
import br.com.vostre.circular.utils.tasks.PreferenceDownloadAsyncTask;
import br.com.vostre.circular.view.form.FormNovidades;
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
    BadgeDrawerToggle drawerToggle;

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "br.com.vostre.circular.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "br.com.vostre.circular.usuario.main";
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

    Location localAnterior;

    AppCompatActivity ctx;
    ParadaBairro paradaAtual;

    Handler handler;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseUser account;

    ProgressBar progressBar;
    SignInButton btnLogin;

    static int RC_SIGN_IN = 450;
    boolean flag = false;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu);
        super.onCreate(savedInstanceState);
        binding.setView(this);

        setViewAtual(binding.getRoot());

        carregaImagens();
        mAuth = FirebaseAuth.getInstance();

        if(!PreferenceUtils.carregarPreferenciaBoolean(getApplicationContext(), "init")){
            // caregar bd
            DBUtils.populaBancoDeDados(this);

            requisitaAtualizacao();
        }

        if(PreferenceUtils.carregarPreferencia(getApplicationContext(), getApplicationContext().getPackageName()+".id_unico").isEmpty()){

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    String identificadorUnico = Unique.geraIdentificadorUnico();
                    PreferenceUtils.salvarPreferencia(getApplicationContext(), getApplicationContext().getPackageName()+".id_unico", identificadorUnico);
                }
            });

        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, this.getClass().getCanonicalName());
        bundle.putString("id_unico", PreferenceUtils.carregarPreferencia(getApplicationContext(), getApplicationContext().getPackageName()+".id_unico"));

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        // PARA TESTES

//        PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "306f25dc-7ffa-11e8-b8e2-34238774caa8");
//        PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "");

        // PARA TESTES

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Permissão de GPS")
                .setMessage("Para aproveitar todas as funções do Circular, por favor permita o uso do GPS no diálogo a seguir. " +
                        "O GPS é necessário para mostrar paradas próximas a você e fazer com que os mapas no aplicativo funcionem corretamente.")
                .setNeutralButton("Entendi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceUtils.salvarPreferencia(ctx.getApplicationContext(), "mostrou_dialog_inicial", true);
                        dialog.dismiss();

                        Dexter.withActivity(ctx)
                                .withPermissions(
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                ).withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if(report.areAllPermissionsGranted()){
                                    Bundle bundle = new Bundle();
                                    bundle.putString("permissoes", "1");
                                    configuraActivity();
                                } else{
                                    Bundle bundle = new Bundle();
                                    bundle.putString("permissoes", "0");

//                                    geraAvisoAjuda();
//                                    geraAvisoMensagens();
//                                    geraAvisoIncidente();

                                    List<TapTarget> targets = geraAvisos();
                                    exibeTour(targets, new TapTargetSequence.Listener(){

                                        @Override
                                        public void onSequenceFinish() {
                                            //Toast.makeText(getApplicationContext(), "Tour finalizado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                                        }

                                        @Override
                                        public void onSequenceCanceled(TapTarget lastTarget) {
//                                            Toast.makeText(getApplicationContext(), "Tour cancelado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    Toast.makeText(getApplicationContext(), "Acesso ao GPS e Armazenamento são necessários para aproveitar ao máximo as funções do Circular!", Toast.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                                .check();

                    }
                }).create();

        if(!checarPermissoes() && !PreferenceUtils.carregarPreferenciaBoolean(this, "mostrou_dialog_inicial")){
            dialog.show();
        } else{
//            geraAvisoAjuda();
//            geraAvisoMensagens();
//            geraAvisoIncidente();
            List<TapTarget> targets = geraAvisos();
            exibeTour(targets, new TapTargetSequence.Listener(){

                @Override
                public void onSequenceFinish() {
                    //Toast.makeText(getApplicationContext(), "Tour finalizado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                }

                @Override
                public void onSequenceCanceled(TapTarget lastTarget) {
//                                            Toast.makeText(getApplicationContext(), "Tour cancelado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Create the dummy account
        mAccount = CreateSyncAccount(this);

        // Get the content resolver for your app
        mResolver = getContentResolver();

        if(mAccount == null){
            mAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        }

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

        drawerToggle = new BadgeDrawerToggle(this, drawer, toolbar, 0, 0){

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                drawerToggle.syncState();
            }

            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                drawerToggle.syncState();
            }

        };

        drawerToggle.setBadgeEnabled(false);

        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        progressBar = binding.progressBar;
        btnLogin = binding.btnLogin;

        ctx = this;
        binding.textView36.setVisibility(View.GONE);

        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        viewModel = ViewModelProviders.of(this).get(BaseViewModel.class);

        viewModel.parametros.observe(this, parametrosObserver);
        viewModel.parametrosInternos.observe(this, parametrosInternosObserver);

        String prefQr = PreferenceUtils.carregarPreferencia(getApplicationContext(), "param_qr");
        String prefVersao = PreferenceUtils.carregarPreferencia(getApplicationContext(), "param_versao");

        if(prefQr.equals("1")){
            binding.btnQrCode.setVisibility(View.VISIBLE);
        } else{
            binding.btnQrCode.setVisibility(View.GONE);
        }

        int versaoAtual = -1;

        try{
            versaoAtual = Integer.parseInt(prefVersao);
        } catch (NumberFormatException e){
            versaoAtual = -1;
        }

        versaoAtual = versaoAtual == -1 ? BuildConfig.VERSION_CODE : versaoAtual;

        if(prefVersao.equalsIgnoreCase(String.valueOf(BuildConfig.VERSION_CODE)) || prefVersao.isEmpty()
                || versaoAtual < BuildConfig.VERSION_CODE){
            binding.btnAviso.setVisibility(View.GONE);
        } else{
            binding.btnAviso.setText("Esta versão do Circular não é a mais atual. " +
                    "Clique aqui para atualizar e tenha acesso a correções e/ou novas funções! Versão instalada: "+BuildConfig.VERSION_CODE+", versão atual: "+prefVersao);
            binding.btnAviso.setVisibility(View.VISIBLE);
        }

        FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(account);

//        if(!PreferenceUtils.carregarPreferenciaBoolean(getApplicationContext(), "novidades_230")){
//            FormNovidades formNovidades = new FormNovidades();
//            formNovidades.setVersao(BuildConfig.VERSION_NAME);
//            formNovidades.show(this.getSupportFragmentManager(), "formNovidades");
//
//            PreferenceUtils.salvarPreferencia(getApplicationContext(), "novidades_230", true);
//        }

    }

    @Override
    public void onMensagemReceived(int mensagens) {

        if(mensagens > 0){
            drawerToggle.setBadgeEnabled(true);
        } else{
            drawerToggle.setBadgeEnabled(false);
        }

    }

    private void configuraActivity(){

        viewModel.localAtual.observe(this, localObserver);
        viewModel.iniciarAtualizacoesPosicao();
        localAnterior = new Location(LocationManager.GPS_PROVIDER);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            startLocationUpdates();
//            geraAvisoAjuda();
//            geraAvisoMensagens();
//            geraAvisoIncidente();

            List<TapTarget> targets = geraAvisos();
            exibeTour(targets, new TapTargetSequence.Listener(){

                @Override
                public void onSequenceFinish() {
                    //Toast.makeText(getApplicationContext(), "Tour finalizado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                }

                @Override
                public void onSequenceCanceled(TapTarget lastTarget) {
//                                            Toast.makeText(getApplicationContext(), "Tour cancelado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void onClickBtnItinerarios(View v){
        Intent i = new Intent(getApplicationContext(), ItinerariosActivity.class);
        startActivity(i);
    }

    public void onClickBtnParadas(View v){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent(getApplicationContext(), ParadasActivity.class);
            startActivity(i);
        } else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Permissão de GPS e Armazenamento")
                    .setMessage("Para aproveitar todas as funções do Circular, por favor permita o uso do GPS e/ou armazenamento no diálogo a seguir. " +
                            "O GPS é necessário para mostrar paradas próximas a você e fazer com que os mapas no aplicativo funcionem corretamente. " +
                            "O Armazenamento é utilizado para o download dos dados dos mapas.")
                    .setNeutralButton("Entendi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Dexter.withActivity(ctx)
                                    .withPermissions(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ).withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if(report.areAllPermissionsGranted()){
                                        Intent i = new Intent(getApplicationContext(), ParadasActivity.class);
                                        startActivity(i);
                                    } else{
                                        Toast.makeText(getApplicationContext(), "Acesso ao GPS é necessário para o " +
                                                "mapa funcionar corretamente! Acesso ao armazenamento externo é utilizado para fazer " +
                                                "cache de partes do mapa e permitir o acesso offline.", Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(getApplicationContext(), ParadasActivity.class);
                                        startActivity(i);
                                    }

                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).check();

                        }
                    }).create();

            dialog.show();
        }

    }

    public void onClickBtnMapa(View v){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent(getApplicationContext(), MapaActivity.class);
            startActivity(i);
        } else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Permissão de GPS e Armazenamento")
                    .setMessage("Para aproveitar todas as funções do Circular, por favor permita o uso do GPS e/ou armazenamento no diálogo a seguir. " +
                            "O GPS é necessário para mostrar paradas próximas a você e fazer com que os mapas no aplicativo funcionem corretamente. " +
                            "O Armazenamento é utilizado para o download dos dados dos mapas.")
                    .setNeutralButton("Entendi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Dexter.withActivity(ctx)
                                    .withPermissions(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ).withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if(report.areAllPermissionsGranted()){
                                        Intent i = new Intent(getApplicationContext(), MapaActivity.class);
                                        startActivity(i);
                                    } else{
                                        Toast.makeText(getApplicationContext(), "Acesso ao GPS é necessário para o " +
                                                "mapa funcionar corretamente! Acesso ao armazenamento externo é utilizado para fazer " +
                                                "cache de partes do mapa e permitir o acesso offline.", Toast.LENGTH_LONG).show();
                                    }

                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).check();

                        }
                    }).create();

            dialog.show();
        }

    }

    public void onClickBtnQRCode(View v){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent(getApplicationContext(), QRCodeActivity.class);
            startActivity(i);
        } else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Permissão de Câmera")
                    .setMessage("Para escanear o QR Code, por favor permita o acesso à Câmera.")
                    .setNeutralButton("Entendi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Dexter.withActivity(ctx)
                                    .withPermissions(
                                            Manifest.permission.CAMERA
                                    ).withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if(report.areAllPermissionsGranted()){
                                        Intent i = new Intent(getApplicationContext(), QRCodeActivity.class);
                                        startActivity(i);
                                    } else{
                                        Toast.makeText(getApplicationContext(), "Acesso à câmera é necessário para escanear o QR Code!", Toast.LENGTH_LONG).show();
                                    }

                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).check();

                        }
                    }).create();

            dialog.show();
        }

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

    public void onClickBtnAviso(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=br.com.vostre.circular"));
        startActivity(intent);
    }

    public void onClickBtnMensagem(View v){
        drawer.closeDrawers();
        Intent intent = new Intent(getApplicationContext(), MensagensActivity.class);
        startActivity(intent);
    }

    public void onClickBtnAtualizar(View v){
        drawer.closeDrawers();
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(new Account(ACCOUNT, ACCOUNT_TYPE), AUTHORITY, settingsBundle);

        PreferenceUtils.gravaMostraToast(getApplicationContext(),true);

        Toast.makeText(getApplicationContext(), "Iniciando sincronização", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(account);
    }

    private void updateUI(FirebaseUser account){

        if(account != null){

            String id = PreferenceUtils.carregarUsuarioLogado(getApplicationContext());

            binding.btnLogin.setVisibility(View.GONE);
            binding.textViewEmail.setText(account.getEmail());
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
        FirebaseAuth.getInstance().signOut();
        updateUI(null);

        requisitaAtualizacao();

        PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "");
        PreferenceUtils.gravaItinerariosFavoritos(new ArrayList<String>(), getApplicationContext());
        PreferenceUtils.gravaParadasFavoritas(new ArrayList<String>(), getApplicationContext());
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
            //System.out.println("Entrou");
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            return newAccount;
        } else {
            //System.out.println("Entrou erro");
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

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("login", "Google sign in failed", e);

                if(progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
            }
//            handleSignInResult(task);
            binding.btnLogin.setEnabled(true);

            if(progressBar != null){
                progressBar.setVisibility(View.VISIBLE);
            }

        }

    }

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

            //Location l0 = new Location(LocationManager.GPS_PROVIDER);
            //l0.setLatitude(latitude);
            //l0.setLongitude(longitude);

            adicionaListaALogo(paradas, localAnterior);

        }
    };

    Observer<List<ParametroInterno>> parametrosInternosObserver = new Observer<List<ParametroInterno>>() {
        @Override
        public void onChanged(List<ParametroInterno> parametros) {
            ParametroInterno parametro = parametros.get(0);

            if(parametro.getDataUltimoAcesso().isBefore(DateTime.now().minusHours(1))){
                requisitaAtualizacao();
            }

        }
    };

    Observer<List<Parametro>> parametrosObserver = new Observer<List<Parametro>>() {
        @Override
        public void onChanged(List<Parametro> parametros) {

            for(Parametro p : parametros){
                PreferenceUtils.salvarPreferencia(getApplicationContext(), "param_"+p.getNome(), p.getValor());
            }

            String prefQr = PreferenceUtils.carregarPreferencia(getApplicationContext(), "param_qr");

            if(prefQr.equals("1")){
                binding.btnQrCode.setVisibility(View.VISIBLE);
            } else{
                binding.btnQrCode.setVisibility(View.GONE);
            }

        }
    };

    private void adicionaListaALogo(final List<ParadaBairro> paradas, final Location localAtual){

        final List<ParadaBairro> myParadas = paradas;
        final Location myLocation = localAtual;

        final NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(0);

        if(paradas != null && paradas.size() > 0 && (localAtual.getLatitude() != 0 && localAtual.getLongitude() != 0) ){

            handler = new Handler();

            int cont = 1;
            int total = paradas.size();
            int delay = 0;

            binding.textView36.setVisibility(View.VISIBLE);

            for(ParadaBairro p : paradas){
               Float distancia;
                Location l = new Location(LocationManager.GPS_PROVIDER);
                l.setLatitude(p.getParada().getLatitude());
                l.setLongitude(p.getParada().getLongitude());

                distancia = l.distanceTo(myLocation);
                p.setDistancia(distancia);
            }

            Collections.sort(paradas, new Comparator<ParadaBairro>() {
                @Override
                public int compare(ParadaBairro o1, ParadaBairro o2) {
                    return (int) (o1.getDistancia() - o2.getDistancia());
                }
            });

            for(final ParadaBairro p : paradas){

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(p.getParada().getImagem() != null && !p.getParada().getImagem().equals("")){

                            final File foto = new File(getApplication().getFilesDir(), p.getParada().getImagem());

                            if(foto.exists() && foto.canRead()){
                                final Drawable drawable = Drawable.createFromPath(foto.getAbsolutePath());
                                binding.circleView.setImagem(drawable);
                                binding.circleView.invalidate();

                            } else{
                                binding.circleView.setImagem(null);
                                binding.circleView.invalidate();
                            }

                        } else{
                            binding.circleView.setImagem(null);
                            binding.circleView.invalidate();
                        }

                        String distancia = "0";

                        if(p.getDistancia() != null){
                            distancia = nf.format(p.getDistancia());
                        }

                        binding.textViewParada.setText(p.getParada().getNome());
                        binding.textViewDistancia.setText("~"+distancia+" m");

                        if(binding.textViewParada.getVisibility() == View.INVISIBLE){
                            binding.textViewParada.setVisibility(View.VISIBLE);
                        }

                        if(binding.textViewDistancia.getVisibility() == View.INVISIBLE){
                            binding.textViewDistancia.setVisibility(View.VISIBLE);
                        }

                        if(binding.textView36.getVisibility() == View.INVISIBLE){
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
            binding.textViewParada.setVisibility(View.INVISIBLE);
            binding.textViewDistancia.setVisibility(View.INVISIBLE);
            binding.textView36.setVisibility(View.INVISIBLE);
            binding.circleView.setImagem(null);
            binding.circleView.invalidate();
            paradaAtual = null;
        }



    }

    @Override
    public void onGpsChanged(boolean ativo) {

        if(!ativo){
            adicionaListaALogo(null, null);
        }

    }

    private void carregaImagens(){

        Bitmap imagem = null;
        FileOutputStream fos = null;

        try {
            String[] s = getApplicationContext().getAssets().list("brasao");

            for(String b : s){
                File file = new File(getApplicationContext().getFilesDir(), b);

                fos = new FileOutputStream(file);
                Bitmap bmp = BitmapFactory.decodeStream(getApplicationContext().getAssets().open("brasao/"+b));
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                    //Toast.makeText(ctx, "Imagem "+imagem+" recebida.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requisitaAtualizacao(){
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(new Account(ACCOUNT, ACCOUNT_TYPE), AUTHORITY, settingsBundle);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            //salva usuario preference
                            PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), user.getUid());

                            Toast.makeText(getApplicationContext(),
                                    "Login realizado com sucesso! Seja bem vindo, "+user.getDisplayName()+"!", Toast.LENGTH_SHORT).show();
                            updateUI(user);

                            PreferenceDownloadAsyncTask preferenceDownloadAsyncTask = new PreferenceDownloadAsyncTask(getApplicationContext(), PreferenceUtils.carregarUsuarioLogado(getApplicationContext()));
                            preferenceDownloadAsyncTask.execute();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Não foi possível fazer o login. " +
                                    "Por favor tente novamente.", Toast.LENGTH_SHORT).show();
                            binding.btnLogin.setEnabled(true);

                            if(progressBar != null){
                                progressBar.setVisibility(View.GONE);
                            }

                            //salva usuario preference
                            PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "");

                        }

                        binding.progressBar.setVisibility(View.GONE);

                    }
                });
    }

    @Override
    public void onToolbarItemSelected(View v) {
        List<TapTarget> targets = criaTour();
        exibeTour(targets, new TapTargetSequence.Listener(){

            @Override
            public void onSequenceFinish() {
                //Toast.makeText(getApplicationContext(), "Tour finalizado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {
                Toast.makeText(getApplicationContext(), "Tour cancelado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onToolbarInflated() {
        //geraAvisoAjuda();
        List<TapTarget> targets = geraAvisos();
        exibeTour(targets, new TapTargetSequence.Listener(){

            @Override
            public void onSequenceFinish() {
                //Toast.makeText(getApplicationContext(), "Tour finalizado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {
//                                            Toast.makeText(getApplicationContext(), "Tour cancelado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ImageButton getNavButtonView(Toolbar toolbar)
    {
        for (int i = 0; i < toolbar.getChildCount(); i++)
            if(toolbar.getChildAt(i) instanceof ImageButton)
                return (ImageButton) toolbar.getChildAt(i);

        return null;
    }

    private void geraAvisoAjuda() {
        if(!PreferenceUtils.carregarPreferenciaBoolean(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_ajuda")){

            if(menu != null){
                DestaqueUtils.geraDestaqueUnico(ctx, menu.getItem(0).getActionView().findViewById(R.id.imageButtonAjuda), "Opção de Ajuda!",
                        "Bateu aquela dúvida na hora de utilizar o aplicativo? Não se preocupe! Pressione aqui e o " +
                                "sistema mostrará as principais ações da tela que estiver aberta!", new TapTargetView.Listener(){
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                super.onTargetClick(view);
                            }
                        }, true, false);

                PreferenceUtils.salvarPreferencia(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_ajuda", true);
            }

        }
    }

    private void geraAvisoMensagens() {
        if(!PreferenceUtils.carregarPreferenciaBoolean(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_mensagens")){

            View v = getNavButtonView(toolbar);

            if(v != null){
                DestaqueUtils.geraDestaqueUnico(ctx, v, "Mudança de Layout",
                        "Os ícones de mensagem e atualização manual agora ficam dentro do menu principal! Fique atento à sinalização (ponto azul) aqui. " +
                                "Ela avisa quando há mensagens não lidas!", new TapTargetView.Listener(){
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                super.onTargetClick(view);
                            }
                        }, true, false);

                PreferenceUtils.salvarPreferencia(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_mensagens", true);
            }

        }
    }

    private void geraAvisoIncidente() {
        if(!PreferenceUtils.carregarPreferenciaBoolean(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_incidente")){

            if(menu != null){
                DestaqueUtils.geraDestaqueUnico(ctx, menu.getItem(1).getActionView().findViewById(R.id.imageButtonIncidente), "Alerta de Incidentes!",
                        "O ônibus quebrou, atrasou ou não fez o horário previsto? Ajude os outros usuários a ficarem informados! " +
                                "Basta pressionar aqui e cadastrar o incidente!", new TapTargetView.Listener(){
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                super.onTargetClick(view);
                            }
                        }, true, false);

                PreferenceUtils.salvarPreferencia(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_incidente", true);
            }

        }
    }

    @Override
    public List<TapTarget> criaTour() {
        List<TapTarget> targets = new ArrayList<>();

        targets.add(DestaqueUtils.geraTapTarget(binding.circleView, "Logo/Paradas Próximas", "Aqui aparecerão as paradas próximas à sua localização quando o GPS estiver ativado!",
                false, true, 1, 100));
        targets.add(DestaqueUtils.geraTapTarget(binding.button, "Itinerários", "Aqui você pode consultar itinerários, informando os locais de partida e destino!",
                false, true, 2));
        targets.add(DestaqueUtils.geraTapTarget(binding.button2, "Paradas", "Aqui você pode consultar pontos de parada e rodoviárias, " +
                "com dados sobre os itinerários, como próximas saídas!", false, true, 3));
        targets.add(DestaqueUtils.geraTapTarget(binding.button3, "Mapa", "Aqui você pode consultar os dados através de um mapa. " +
                "Veja os pontos de parada e de interesse próximos à sua localização atual!", false, true, 4));

//        targets.add(DestaqueUtils.geraTapTarget(binding.toolbar.findViewById(R.id.imageButtonSync), "Atualizar Dados", "Realiza a conexão com o servidor para baixar dados atualizados. " +
//                        "A atualização também ocorre automaticamente de forma periódica!",
//                false, true, 5, 30));
        targets.add(DestaqueUtils.geraTapTarget(binding.toolbar.findViewById(R.id.imageButtonFavoritos), "Favoritos", "Permite consultar os registros (itinerários e paradas) marcados como favoritos.",
                false, true, 5, 30));
//        targets.add(DestaqueUtils.geraTapTarget(binding.toolbar.findViewById(R.id.imageButtonMsg), "Mensagens",
//                "Aqui você pode consultar as mensagens enviadas pelo sistema e enviar suas sugestões!",
//                false, true, 7, 30));

        return targets;
    }

    public List<TapTarget> geraAvisos() {
        List<TapTarget> targets = new ArrayList<>();

        if(!PreferenceUtils.carregarPreferenciaBoolean(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_ajuda")){

            if(menu != null){
                targets.add(DestaqueUtils.geraTapTarget(menu.getItem(0).getActionView().findViewById(R.id.imageButtonAjuda), "Opção de Ajuda!",
                        "Bateu aquela dúvida na hora de utilizar o aplicativo? Não se preocupe! Pressione aqui e o " +
                                "sistema mostrará as principais ações da tela que estiver aberta!",
                        false, false, 1));

                PreferenceUtils.salvarPreferencia(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_ajuda", true);
            }


        }

        if(!PreferenceUtils.carregarPreferenciaBoolean(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_mensagens")){

            View v = getNavButtonView(toolbar);

            if(v != null){
                targets.add(DestaqueUtils.geraTapTarget(v, "Mudança de Layout",
                        "Os ícones de mensagem e atualização manual agora ficam dentro do menu principal! Fique atento à sinalização (ponto azul) aqui. " +
                                "Ela avisa quando há mensagens não lidas!",
                        false, false, 2));
            }

            PreferenceUtils.salvarPreferencia(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_mensagens", true);

        }

        if(!PreferenceUtils.carregarPreferenciaBoolean(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_incidente")){

            if(menu != null){
                targets.add(DestaqueUtils.geraTapTarget(menu.getItem(1).getActionView().findViewById(R.id.imageButtonIncidente), "Alerta de Incidentes!",
                        "O ônibus quebrou, atrasou ou não fez o horário previsto? Ajude os outros usuários a ficarem informados! " +
                                "Basta pressionar aqui e cadastrar o incidente!",
                        false, false, 3));

                PreferenceUtils.salvarPreferencia(getApplicationContext(), getApplicationContext().getPackageName()+".alerta_incidente", true);
            }


        }

        return targets;
    }
}
