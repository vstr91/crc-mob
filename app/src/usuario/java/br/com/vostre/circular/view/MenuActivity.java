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
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
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

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityMenuBinding;
import br.com.vostre.circular.viewModel.BaseViewModel;

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
    public static final long SYNC_INTERVAL_IN_MINUTES = 60*5L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;
    // Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;

    int permissionStorage;
    Location localAnterior;

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

    }

    private void configuraActivity(){
        viewModel = ViewModelProviders.of(this).get(BaseViewModel.class);
        viewModel.localAtual.observe(this, localObserver);
        viewModel.iniciarAtualizacoesPosicao();
        localAnterior = new Location(LocationManager.GPS_PROVIDER);
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
//        Intent i = new Intent(getApplicationContext(), BairrosActivity.class);
//        startActivity(i);
        Toast.makeText(getApplicationContext(), "QR Code", Toast.LENGTH_SHORT).show();
    }

    public void onClickBtnLogo(View v){
//        Intent i = new Intent(getApplicationContext(), BairrosActivity.class);
//        startActivity(i);
        Toast.makeText(getApplicationContext(), "Logo", Toast.LENGTH_SHORT).show();
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

        startLocationUpdates();

    }

    @Override
    protected void onPause() {
        super.onPause();

        stopLocationUpdates();

    }

    Observer<Location> localObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location local) {

            if(local.getLatitude() != 0.0 && local.getLongitude() != 0.0 && local.distanceTo(localAnterior) > 20){
                viewModel.buscaParadasProximas(local);
            }

        }
    };

}
