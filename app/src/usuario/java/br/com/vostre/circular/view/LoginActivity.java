package br.com.vostre.circular.view;

import android.accounts.Account;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import br.com.vostre.circular.BuildConfig;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityLoginBinding;
import br.com.vostre.circular.utils.Constants;
import br.com.vostre.circular.utils.DBUtils;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.Unique;
import br.com.vostre.circular.utils.tasks.PreferenceDownloadAsyncTask;
import br.com.vostre.circular.viewModel.BaseViewModel;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;

    ProgressBar progressBar;
    SignInButton btnLogin;

    BaseViewModel viewModel;

    static int RC_SIGN_IN = 480;
    boolean flag = false;
    private FirebaseAuth mAuth;

//    // The authority for the sync adapter's content provider
//    public static final String AUTHORITY = "br.com.vostre.circular.datasync.provider";
//    // An account type, in the form of a domain name
//    public static final String ACCOUNT_TYPE = "br.com.vostre.circular.usuario";
//    // The account name
//    public static final String ACCOUNT = "dummyaccount";

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        binding.setView(this);
        viewModel = ViewModelProviders.of(this).get(BaseViewModel.class);

        mAuth = FirebaseAuth.getInstance();

        //carregaImagens();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        if(PreferenceUtils.carregarPreferencia(getApplicationContext(), getApplicationContext().getPackageName()+".id_unico").isEmpty()){

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    String identificadorUnico = Unique.geraIdentificadorUnico();
                    PreferenceUtils.salvarPreferencia(getApplicationContext(), getApplicationContext().getPackageName()+".id_unico", identificadorUnico);
                }
            });

        }



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
        ContentResolver.requestSync(new Account(Constants.ACCOUNT, Constants.ACCOUNT_TYPE), Constants.AUTHORITY, settingsBundle);

        btnLogin = binding.btnLogin;

        if(!PreferenceUtils.carregarPreferenciaBoolean(getApplicationContext(), "init")){
            // caregar bd
            DBUtils.populaBancoDeDados(this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        String lembrar = PreferenceUtils.carregarPreferencia(getApplicationContext(), "lembrar");

        if(lembrar.equals("1")){
            Intent i = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(i);
        } else{
            FirebaseUser usuario = mAuth.getCurrentUser();

            if(usuario != null){
                Intent i = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(i);
            }
        }



    }

    public void onClickBtnLogin(View v){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        btnLogin.setEnabled(false);

    }

    public void onClickBtnEntrar(View v){
        PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "");

        if(binding.checkBoxLembrar.isChecked()){
            PreferenceUtils.salvarPreferencia(getApplicationContext(), "lembrar", "1");
        } else{
            PreferenceUtils.salvarPreferencia(getApplicationContext(), "lembrar", "0");
        }

        Intent i = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(i);
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
            }
//            handleSignInResult(task);
            binding.btnLogin.setEnabled(true);

            if(progressBar != null){
                progressBar.setVisibility(View.VISIBLE);
            }

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
            btnLogin.setEnabled(true);

            if(progressBar != null){
                progressBar.setVisibility(View.GONE);
            }

            Toast.makeText(getApplicationContext(), "Erro ao efeutar login: "+e.getMessage()+". Por favor tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }

    Observer<Boolean> loginObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean logado) {

            if(viewModel.recebeuCallback){
                if(logado){

                    if(binding.checkBoxLembrar.isChecked()){
                        PreferenceUtils.salvarPreferencia(getApplicationContext(), "lembrar", "1");
                    } else{
                        PreferenceUtils.salvarPreferencia(getApplicationContext(), "lembrar", "0");
                    }

                    //PreferenceDownloadAsyncTask preferenceDownloadAsyncTask = new PreferenceDownloadAsyncTask(getApplicationContext(), PreferenceUtils.carregarUsuarioLogado(getApplicationContext()));
                    //preferenceDownloadAsyncTask.execute();

                    bundle = new Bundle();
                    mFirebaseAnalytics.logEvent("login_tela_inicial", bundle);

                    Intent i = new Intent(getApplicationContext(), MenuActivity.class);
                    startActivity(i);
                } else{
                    Toast.makeText(getApplicationContext(), "Não foi possível fazer login. Por favor tente novamente.", Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                    signOut();
                }

                if(flag){
                    btnLogin.setEnabled(true);

                    if(progressBar != null){
                        progressBar.setVisibility(View.GONE);
                    }

                }

                flag = true;
            }



        }
    };

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // nada ainda
                    }
                });
        PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "");
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("login", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(),
                                    "Login realizado com sucesso! Seja bem vindo, "+user.getDisplayName()+"!",
                                    Toast.LENGTH_SHORT).show();

                            //salva usuario preference
                            PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), user.getUid());

                            PreferenceDownloadAsyncTask preferenceDownloadAsyncTask = new PreferenceDownloadAsyncTask(getApplicationContext(), user.getUid());
                            preferenceDownloadAsyncTask.execute();

                            Intent i = new Intent(getApplicationContext(), MenuActivity.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Não foi possível fazer o login. " +
                                    "Por favor tente novamente.", Toast.LENGTH_SHORT).show();
                            binding.btnLogin.setEnabled(true);

                            //salva usuario preference
                            PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "");
                        }

                    }
                });
    }

}
