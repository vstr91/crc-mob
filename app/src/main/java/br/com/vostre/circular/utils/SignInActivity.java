package br.com.vostre.circular.utils;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.Task;

import br.com.vostre.circular.R;
import br.com.vostre.circular.view.BaseActivity;
import br.com.vostre.circular.view.MenuActivity;
import br.com.vostre.circular.viewModel.BaseViewModel;

public class SignInActivity extends BaseActivity {

    GoogleSignInClient mGoogleSignInClient;

    BaseViewModel viewModel;

    static int RC_SIGN_IN = 487;
    private int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(BaseViewModel.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
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
            Toast.makeText(getApplicationContext(), "Erro ao efeutar login: "+e.getMessage()+". Por favor tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
            Intent result = new Intent();
            result.putExtra("logado", false);
            result.putExtra("mensagemErro", e.getMessage());
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }

    Observer<Boolean> loginObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean logado) {
            Intent result = new Intent();
            result.putExtra("account", account);
            result.putExtra("logado", logado);
            result.putExtra("mensagemErro", "Erro ao validar usuário. Por favor tente novamente mais tarde.");
            setResult(Activity.RESULT_OK, result);

            if(cont > 0){
                finish();
            }

            cont++;

        }
    };

}
