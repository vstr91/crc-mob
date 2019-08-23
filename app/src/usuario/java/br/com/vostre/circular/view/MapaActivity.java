package br.com.vostre.circular.view;

import android.Manifest;
import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.IntentService;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.StorageUtils;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalheItinerarioBinding;
import br.com.vostre.circular.databinding.ActivityMapaBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;
import br.com.vostre.circular.utils.DestaqueUtils;
import br.com.vostre.circular.utils.DialogUtils;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.SessionUtils;
import br.com.vostre.circular.utils.SignInActivity;
import br.com.vostre.circular.utils.tasks.PreferenceDownloadAsyncTask;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioCompactoAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioFavoritoAdapter;
import br.com.vostre.circular.view.adapter.ParadaAdapter;
import br.com.vostre.circular.view.adapter.SecaoItinerarioAdapter;
import br.com.vostre.circular.view.form.FormParada;
import br.com.vostre.circular.view.form.FormPoi;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.view.utils.InfoWindowParada;
import br.com.vostre.circular.viewModel.BaseViewModel;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;
import br.com.vostre.circular.viewModel.InfoWindowPOIViewModel;
import br.com.vostre.circular.viewModel.MapaViewModel;

public class MapaActivity extends BaseActivity {

    ActivityMapaBinding binding;
    MapaViewModel viewModel;
    BaseViewModel baseViewModel;
    InfoWindowPOIViewModel viewModelPoi;
    ParadaAdapter adapter;
    ItinerarioAdapter adapterItinerarios;

    AppCompatActivity ctx;

    MapView map;
    IMapController mapController;
    MyLocationNewOverlay mLocationOverlay;

    int permissionGPS;
    int permissionStorage;
    int permissionInternet;

    BottomSheetDialog bsd;
    BottomSheetDialog bsdPoi;

    FormParada formParada;
    FormPoi formPoi;

    GoogleSignInClient mGoogleSignInClient;

    static int RC_SIGN_IN = 481;
    boolean flag = false;

    Bundle bundle;
    boolean logado = false;

    private FirebaseAuth mAuth;

    boolean submenuAberto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mapa);
        binding.setView(this);
        super.onCreate(savedInstanceState);
        setTitle("Mapa");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        fechaSubmenu();
        ocultaModalLoading();

        // PARA TESTES

//        PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "306f25dc-7ffa-11e8-b8e2-34238774caa8");
//        PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "");

        // PARA TESTES

        ctx = this;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
                            Toast.makeText(getApplicationContext(), "Acesso ao GPS é necessário para o " +
                                    "mapa funcionar corretamente! Acesso ao armazenamento externo é utilizado para fazer " +
                                    "cache de partes do mapa e permitir o acesso offline.", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissionInternet = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET);

        if(!checarPermissoes()){
            configuraActivity();
        }


    }

    public void onClickBtnLogin(View v){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        binding.btnLogin.setEnabled(false);

        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("clicou_login_mapa", bundle);

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

        } else if(requestCode == FormParada.PICK_IMAGE) {

                formParada = (FormParada) DialogUtils.getOpenedDialog(this);

                if (data != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        viewModel.foto = BitmapFactory.decodeStream(inputStream);
                        formParada.exibeImagem();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            } else if(requestCode == FormPoi.PICK_IMAGE) {

            formPoi = (FormPoi) DialogUtils.getOpenedDialog(this);

            if (data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    viewModel.fotoPoi = BitmapFactory.decodeStream(inputStream);
                    formPoi.exibeImagem();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

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
                //binding.btnLogin.setEnabled(true);
            }

            flag = true;

        }
    };

    private void configuraActivity(){
        viewModel = ViewModelProviders.of(this).get(MapaViewModel.class);
        baseViewModel = ViewModelProviders.of(this).get(BaseViewModel.class);

        binding.setViewModel(viewModel);

        viewModel.paradas.observe(this, paradasObserver);
        viewModel.paradasSugeridas.observe(this, paradasSugeridasObserver);
        viewModel.pois.observe(this, poisObserver);
        viewModel.poisSugeridos.observe(this, poisSugeridosObserver);
        viewModel.localAtual.observe(this, localObserver);
        viewModel.iniciarAtualizacoesPosicao();

        bsd = new BottomSheetDialog(ctx);
        bsd.setCanceledOnTouchOutside(true);

        bsd.setContentView(R.layout.infowindow_parada);

        bsdPoi = new BottomSheetDialog(ctx);
        bsdPoi.setCanceledOnTouchOutside(true);

        bsdPoi.setContentView(R.layout.infowindow_poi);

        checaLogin();

        if(!gpsAtivo){
            binding.textViewGps.setVisibility(View.VISIBLE);
            binding.fabParada.setEnabled(false);
            binding.fabMeuLocal.setEnabled(false);
            binding.fabParada.setVisibility(View.GONE);
        } else{
            binding.textViewGps.setVisibility(View.GONE);
            binding.fabParada.setEnabled(true);
            binding.fabMeuLocal.setEnabled(true);
        }

        configuraMapa();

        binding.textViewGps.getViewTreeObserver().addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                YoYo.with(Techniques.Swing)
                        .duration(700)
                        .repeat(1)
                        .playOn(findViewById(R.id.textViewGps));

//                    View v = findViewById(R.id.textView66);
//
//                    YoYo.with(Techniques.Flash)
//                            .delay(500)
//                            .duration(500)
//                            .playOn(findViewById(R.id.textView66));
            }
        });
    }

    private void checaLogin() {
        if(SessionUtils.estaLogado(getApplicationContext())){
            binding.fabParada.setEnabled(true);
            binding.btnLogin.setVisibility(View.GONE);
            binding.fabSugestao.setEnabled(true);
            binding.fabSugestao.setVisibility(View.VISIBLE);
            binding.fabParada.setVisibility(View.VISIBLE);
        } else{
            binding.fabParada.setEnabled(false);
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.fabSugestao.setEnabled(false);
            binding.fabSugestao.setVisibility(View.GONE);
            binding.fabParada.setVisibility(View.GONE);
        }
    }

    private void configuraMapa() {
        map = binding.map;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(getApplicationContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        mapController = map.getController();
        mapController.setZoom(19d);
//        GeoPoint startPoint = new GeoPoint(-22.470804460339885, -43.82463455200195);
//        mapController.setCenter(startPoint);

        map.setMaxZoomLevel(19d);
        map.setMinZoomLevel(8d);
        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(submenuAberto){
                    fechaSubmenu();
                }

                return false;

            }
        });

        startLocationUpdates();
    }

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            atualizarParadasMapa(paradas);
        }
    };

    Observer<List<ParadaSugestaoBairro>> paradasSugeridasObserver = new Observer<List<ParadaSugestaoBairro>>() {
        @Override
        public void onChanged(List<ParadaSugestaoBairro> paradas) {
            atualizarSugestoesMapa(paradas);
        }
    };

    Observer<List<PontoInteresse>> poisObserver = new Observer<List<PontoInteresse>>() {
        @Override
        public void onChanged(List<PontoInteresse> pois) {
            atualizarPoisMapa(pois);
        }
    };

    Observer<List<PontoInteresseSugestaoBairro>> poisSugeridosObserver = new Observer<List<PontoInteresseSugestaoBairro>>() {
        @Override
        public void onChanged(List<PontoInteresseSugestaoBairro> pois) {
            atualizarSugestoesPoiMapa(pois);
        }
    };

    Observer<Location> localObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location local) {

            if(viewModel.centralizaMapa && local.getLatitude() != 0.0 && local.getLongitude() != 0.0){
                setMapCenter(map, new GeoPoint(local.getLatitude(), local.getLongitude()));
                binding.textViewGps.setVisibility(View.GONE);
                //viewModel.centralizaMapa = false;
            }

        }
    };

    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
            adapterItinerarios.itinerarios = itinerarios;
            adapterItinerarios.notifyDataSetChanged();

            ocultaModalLoading();

            int contLegenda = 0;

            for(ItinerarioPartidaDestino i : itinerarios){

                if(i.getTempoAcumulado() != null &&
                        (i.getTempoAcumulado().getHourOfDay() > 0 || i.getTempoAcumulado().getMinuteOfHour() > 0)){
                    contLegenda++;
                }

            }

            if(contLegenda == 0){
                bsd.findViewById(R.id.textViewLegenda).setVisibility(View.GONE);
            } else{
                bsd.findViewById(R.id.textViewLegenda).setVisibility(View.VISIBLE);
            }

            if(!bsd.isShowing()){
                bsd.show();
            }

        }
    };

    @BindingAdapter("center")
    public static void setMapCenter(MapView map, GeoPoint geoPoint){

        if(geoPoint != null){
            map.getController().setCenter(geoPoint);
        }

    }

    private void atualizarParadasMapa(final List<ParadaBairro> paradas){

        if(paradas != null){

            RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(this);
            poiMarkers.setRadius(200);

            Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_cluster);
            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

            map.getOverlays().add(poiMarkers);
            poiMarkers.setIcon(clusterIcon);

            for(final ParadaBairro p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setTitle(p.getParada().getNome());
                m.setDraggable(false);
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));

//                switch(p.getParada().getSentido()){
//                    case 0:
//                        m.setIcon(br.com.vostre.circular.utils.DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_keyboard_backspace_black_24dp));
//                        break;
//                    case 1:
//                        m.setIcon(br.com.vostre.circular.utils.DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_keyboard_forward_black_24dp));
//                        break;
//                    case 2:
//                        m.setIcon(br.com.vostre.circular.utils.DrawableUtils.mergeDrawable(this, R.drawable.marker, R.drawable.ic_swap_horiz_black_24dp));
//                        break;
//                    default:
//                        m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
//                        break;
//                }

                m.setId(p.getParada().getId());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        if(gpsAtivo){
                            geraModalLoading();

                            final ParadaBairro pb = getParadaFromMarker(marker, paradas);

                            viewModel.setParada(pb);
                            viewModel.itinerarios.observe(ctx, itinerariosObserver);

                            RecyclerView listItinerarios = bsd.findViewById(R.id.listItinerarios);

                            adapterItinerarios = new ItinerarioAdapter(viewModel.itinerarios.getValue(), ctx);
                            listItinerarios.setAdapter(adapterItinerarios);

                            // bottom menu

                            TextView textViewReferencia = bsd.findViewById(R.id.textViewReferencia);
                            TextView textViewBairro = bsd.findViewById(R.id.textViewBairro);
                            TextView textViewRua = bsd.findViewById(R.id.textViewRua);

                            textViewReferencia.setText(pb.getParada().getNome());

                            if(pb.getParada().getRua() != null && !pb.getParada().getRua().equals("")){
                                textViewRua.setText(pb.getParada().getRua());
                                textViewRua.setVisibility(View.VISIBLE);
                            } else{
                                textViewRua.setVisibility(View.GONE);
                            }

                            textViewBairro.setText(pb.getNomeBairroComCidade());

                            bsd.findViewById(R.id.textView32).setVisibility(View.VISIBLE);
                            bsd.findViewById(R.id.textView33).setVisibility(View.VISIBLE);
                            bsd.findViewById(R.id.textViewLegenda).setVisibility(View.VISIBLE);

                            Button btnDetalhes = bsd.findViewById(R.id.btnDetalhes);
                            btnDetalhes.setVisibility(View.VISIBLE);

                            btnDetalhes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(ctx, DetalheParadaActivity.class);
                                    i.putExtra("parada", pb.getParada().getId());
                                    ctx.startActivity(i);
                                }
                            });

                            Button btnFechar = bsd.findViewById(R.id.btnFechar);
                            btnFechar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bsd.dismiss();
                                }
                            });

                            Button btnEdicao = bsd.findViewById(R.id.btnEdicao);
                            btnEdicao.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    viewModel.paradaNova = new ParadaSugestaoBairro();
                                    formParada = new FormParada();
                                    formParada.setParadaRelativa(pb);
                                    formParada.setCtx(getApplication());
                                    formParada.show(getSupportFragmentManager(), "formParada");
                                }
                            });

                            ImageView img = bsd.findViewById(R.id.imageView3);

                            File f = null;

                            if(pb.getParada().getImagem() != null){
                                f = new File(ctx.getApplicationContext().getFilesDir(),  pb.getParada().getImagem());
                            }

                            if(pb.getParada().getImagem() != null && f != null && f.exists() && f.canRead()){
                                img.setImageDrawable(Drawable.createFromPath(getApplicationContext().getFilesDir()
                                        +"/"+pb.getParada().getImagem()));
                            } else{
                                img.setImageDrawable(getResources().getDrawable(R.drawable.imagem_nao_disponivel_16_9));
                            }

                            if(!SessionUtils.estaLogado(getApplicationContext())){
                                bsd.findViewById(R.id.btnEdicao).setVisibility(View.GONE);
                            } else{
                                bsd.findViewById(R.id.btnEdicao).setVisibility(View.VISIBLE);
                            }

                            // fim bottom menu

                            mapController.animateTo(marker.getPosition());
                        }

                        return true;
                    }
                });
//                map.getOverlays().add(m);
                poiMarkers.add(m);
            }

        }

    }

    private void atualizarPoisMapa(final List<PontoInteresse> pois){

        if(pois != null){

            RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(this);
            poiMarkers.setRadius(200);

            Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_cluster);
            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

            map.getOverlays().add(poiMarkers);
            poiMarkers.setIcon(clusterIcon);

            for(final PontoInteresse p : pois){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setTitle(p.getNome());
                m.setDraggable(false);
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.poi));
                m.setId(p.getId());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        if(gpsAtivo){
                            final PontoInteresse poi = getPoiFromMarker(marker, pois);

                            // bottom menu

                            TextView textViewReferencia = bsdPoi.findViewById(R.id.textViewReferencia);
                            TextView textViewDescricao = bsdPoi.findViewById(R.id.textViewDescricao);

                            textViewReferencia.setText(poi.getNome());

                            if(poi.getDescricao() != null && !poi.getDescricao().isEmpty()){
                                textViewDescricao.setText(poi.getDescricao());
                                textViewDescricao.setVisibility(View.VISIBLE);
                            } else{
                                textViewDescricao.setVisibility(View.GONE);
                            }

                            //itinerarios poi
//                            viewModelPoi = ViewModelProviders.of(ctx).get(InfoWindowPOIViewModel.class);
//                            viewModelPoi.setPontoInteresse(poi);
//
//                            Location l = new Location(LocationManager.NETWORK_PROVIDER);
//                            l.setLatitude(poi.getLatitude());
//                            l.setLongitude(poi.getLongitude());
//
//                            viewModelPoi.buscarParadasProximas(ctx, l);
//                            viewModelPoi.paradas.observe(ctx, paradasPoiObserver);

                            //fim tinerarios poi

                            ImageView img = bsdPoi.findViewById(R.id.imageView3);

                            File f = null;

                            if(poi.getImagem() != null){
                                f = new File(ctx.getApplicationContext().getFilesDir(),  poi.getImagem());
                            }

                            if(poi.getImagem() != null && f != null && f.exists() && f.canRead()){
                                img.setImageDrawable(Drawable.createFromPath(getApplicationContext().getFilesDir()
                                        +"/"+poi.getImagem()));
                            } else{
                                img.setImageDrawable(getResources().getDrawable(R.drawable.imagem_nao_disponivel_16_9));
                            }

                            Button btnFechar = bsdPoi.findViewById(R.id.btnFechar);
                            btnFechar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bsdPoi.dismiss();
                                }
                            });

                            bsdPoi.findViewById(R.id.btnDetalhes).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(ctx, DetalhePontoInteresseActivity.class);
                                    i.putExtra("poi", poi.getId());
                                    ctx.startActivity(i);
                                }
                            });

                            // fim bottom menu

                            mapController.animateTo(marker.getPosition());
                            bsdPoi.show();
                        }

                        return true;
                    }
                });
//                map.getOverlays().add(m);
                poiMarkers.add(m);
            }

        }

    }

    private void atualizarSugestoesMapa(final List<ParadaSugestaoBairro> paradasSugeridas){

        if(paradasSugeridas != null){

            RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(this);
            poiMarkers.setRadius(200);

            Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_cluster);
            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

            map.getOverlays().add(poiMarkers);
            poiMarkers.setIcon(clusterIcon);

            for(final ParadaSugestaoBairro p : paradasSugeridas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setTitle(p.getParada().getNome());
                m.setDraggable(true);
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.sugestao));
                m.setId(p.getParada().getId());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        if(gpsAtivo){

                            geraModalLoading();

                            final ParadaSugestaoBairro p = getSugestaoFromMarker(marker, paradasSugeridas);

                            viewModel.setParadaNova(p);

                            // bottom menu

                            RecyclerView listItinerarios = bsd.findViewById(R.id.listItinerarios);

                            adapterItinerarios = new ItinerarioAdapter(new ArrayList<ItinerarioPartidaDestino>(), ctx);
                            listItinerarios.setAdapter(adapterItinerarios);

                            TextView textViewReferencia = bsd.findViewById(R.id.textViewReferencia);
                            TextView textViewBairro = bsd.findViewById(R.id.textViewBairro);

                            bsd.findViewById(R.id.textView32).setVisibility(View.GONE);
                            bsd.findViewById(R.id.textView33).setVisibility(View.GONE);
                            bsd.findViewById(R.id.textViewLegenda).setVisibility(View.GONE);

                            textViewReferencia.setText(p.getParada().getNome());
                            textViewBairro.setText(p.getNomeBairroComCidade());

                            ImageView img = bsd.findViewById(R.id.imageView3);

                            File f = null;

                            if(p.getParada().getImagem() != null){
                                f = new File(ctx.getApplicationContext().getFilesDir(),  p.getParada().getImagem());
                            }

                            if(p.getParada().getImagem() != null && f != null && f.exists() && f.canRead()){
                                img.setImageDrawable(Drawable.createFromPath(getApplicationContext().getFilesDir()
                                        +"/"+p.getParada().getImagem()));
                            } else{
                                img.setImageDrawable(getResources().getDrawable(R.drawable.imagem_nao_disponivel_16_9));
                            }

                            Button btnDetalhes = bsd.findViewById(R.id.btnDetalhes);
                            btnDetalhes.setVisibility(View.GONE);

                            Button btnEdicao = bsd.findViewById(R.id.btnEdicao);
                            btnEdicao.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    formParada = new FormParada();
                                    formParada.setParada(p.getParada());
                                    formParada.setCtx(getApplication());
                                    formParada.show(getSupportFragmentManager(), "formParada");
                                }
                            });

                            btnEdicao.setVisibility(View.GONE);

                            Button btnFechar = bsd.findViewById(R.id.btnFechar);
                            btnFechar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bsd.dismiss();
                                    ocultaModalLoading();
                                }
                            });

                            // fim bottom menu

                            mapController.animateTo(marker.getPosition());
                            bsd.show();
                        }

                        return true;
                    }
                });
                m.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        ParadaSugestaoBairro p = getSugestaoFromMarker(marker, paradasSugeridas);
                        viewModel.setParadaNova(p);
                        viewModel.editarParada();
                        Toast.makeText(getApplicationContext(), "Sugestão alterada", Toast.LENGTH_SHORT).show();

                        bundle = new Bundle();
                        bundle.putString("parada", p.getParada().getNome()+", "+p.getNomeBairroComCidade());
                        mFirebaseAnalytics.logEvent("sugestao_alterada", bundle);
                    }

                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }
                });
//                map.getOverlays().add(m);
                poiMarkers.add(m);
            }

        }

    }

    private void atualizarSugestoesPoiMapa(final List<PontoInteresseSugestaoBairro> paradasSugeridas){

        if(paradasSugeridas != null){

            RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(this);
            poiMarkers.setRadius(200);

            Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_cluster);
            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

            map.getOverlays().add(poiMarkers);
            poiMarkers.setIcon(clusterIcon);

            for(final PontoInteresseSugestaoBairro p : paradasSugeridas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getPontoInteresse().getLatitude(), p.getPontoInteresse().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setTitle(p.getPontoInteresse().getNome());
                m.setDraggable(true);
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.sugestao));
                m.setId(p.getPontoInteresse().getId());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        if(gpsAtivo){
                            final PontoInteresseSugestaoBairro p = getSugestaoPoiFromMarker(marker, paradasSugeridas);

                            viewModel.setPoiNovo(p);

                            // bottom menu

                            TextView textViewReferencia = bsdPoi.findViewById(R.id.textViewReferencia);
                            TextView textViewDescricao = bsdPoi.findViewById(R.id.textViewDescricao);

//                            bsdPoi.findViewById(R.id.textView32).setVisibility(View.GONE);
//                            bsdPoi.findViewById(R.id.textView33).setVisibility(View.GONE);
//                            bsdPoi.findViewById(R.id.textViewLegenda).setVisibility(View.GONE);

                            textViewReferencia.setText(p.getPontoInteresse().getNome());
                            textViewDescricao.setText(p.getPontoInteresse().getDescricao());

                            ImageView img = bsdPoi.findViewById(R.id.imageView3);

                            File f = null;

                            if(p.getPontoInteresse().getImagem() != null){
                                f = new File(ctx.getApplicationContext().getFilesDir(),  p.getPontoInteresse().getImagem());
                            }

                            if(p.getPontoInteresse().getImagem() != null && f != null && f.exists() && f.canRead()){
                                img.setImageDrawable(Drawable.createFromPath(getApplicationContext().getFilesDir()
                                        +"/"+p.getPontoInteresse().getImagem()));
                            } else{
                                img.setImageDrawable(getResources().getDrawable(R.drawable.imagem_nao_disponivel_16_9));
                            }

                            Button btnDetalhes = bsdPoi.findViewById(R.id.btnDetalhes);
//                            btnDetalhes.setVisibility(View.GONE);

//                            Button btnEdicao = bsdPoi.findViewById(R.id.btnEdicao);
//                            btnEdicao.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    formPoi = new FormPoi();
//                                    formPoi.setParada(p.getPontoInteresse());
//                                    formPoi.setCtx(getApplication());
//                                    formPoi.show(getSupportFragmentManager(), "formPoi");
//                                }
//                            });
//
//                            btnEdicao.setVisibility(View.GONE);

                            Button btnFechar = bsdPoi.findViewById(R.id.btnFechar);
                            btnFechar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bsdPoi.dismiss();
                                    ocultaModalLoading();
                                }
                            });

                            // fim bottom menu

                            mapController.animateTo(marker.getPosition());
                            bsdPoi.show();
                        }

                        return true;
                    }
                });
                m.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        PontoInteresseSugestaoBairro p = getSugestaoPoiFromMarker(marker, paradasSugeridas);
                        viewModel.setPoiNovo(p);
                        viewModel.editarPontoInteresse();
                        Toast.makeText(getApplicationContext(), "Sugestão alterada", Toast.LENGTH_SHORT).show();

                        bundle = new Bundle();
                        bundle.putString("ponto_interesse", p.getPontoInteresse().getNome()+", "+p.getNomeBairroComCidade());
                        mFirebaseAnalytics.logEvent("sugestao_poi_alterada", bundle);
                    }

                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }
                });
//                map.getOverlays().add(m);
                poiMarkers.add(m);
            }

        }

    }

    @NonNull
    private ParadaBairro getParadaFromMarker(Marker marker, List<ParadaBairro> paradas) {
        Parada p = new Parada();
        p.setId(marker.getId());

        ParadaBairro parada = new ParadaBairro();
        parada.setParada(p);

        ParadaBairro pb = paradas.get(paradas.indexOf(parada));
        pb.getParada().setLatitude(marker.getPosition().getLatitude());
        pb.getParada().setLongitude(marker.getPosition().getLongitude());
        return pb;
    }

    @NonNull
    private PontoInteresse getPoiFromMarker(Marker marker, List<PontoInteresse> pois) {
        PontoInteresse p = new PontoInteresse();
        p.setId(marker.getId());

        p = pois.get(pois.indexOf(p));
        p.setLatitude(marker.getPosition().getLatitude());
        p.setLongitude(marker.getPosition().getLongitude());
        return p;
    }

    @NonNull
    private ParadaSugestaoBairro getSugestaoFromMarker(Marker marker, List<ParadaSugestaoBairro> sugestoes) {
        ParadaSugestaoBairro p = new ParadaSugestaoBairro();
        p.getParada().setId(marker.getId());

        p = sugestoes.get(sugestoes.indexOf(p));
        p.getParada().setLatitude(marker.getPosition().getLatitude());
        p.getParada().setLongitude(marker.getPosition().getLongitude());
        return p;
    }

    @NonNull
    private PontoInteresseSugestaoBairro getSugestaoPoiFromMarker(Marker marker, List<PontoInteresseSugestaoBairro> sugestoes) {
        PontoInteresseSugestaoBairro p = new PontoInteresseSugestaoBairro();
        p.getPontoInteresse().setId(marker.getId());

        p = sugestoes.get(sugestoes.indexOf(p));
        p.getPontoInteresse().setLatitude(marker.getPosition().getLatitude());
        p.getPontoInteresse().setLongitude(marker.getPosition().getLongitude());
        return p;
    }

    public void onFabAddClick(View v){

        if(submenuAberto){
            fechaSubmenu();
        } else{
            abreSubmenu();
        }

    }

    public void onFabParadaClick(View v){

        if(viewModel.localAtual != null && viewModel.localAtual.getValue().getLatitude() != 0 && viewModel.localAtual.getValue().getLongitude() != 0){
            formParada = new FormParada();
            formParada.setLatitude(viewModel.localAtual.getValue().getLatitude());
            formParada.setLongitude(viewModel.localAtual.getValue().getLongitude());
            //formParada.setParada(new ParadaSugestao());
            formParada.setCtx(getApplication());
            formParada.show(getSupportFragmentManager(), "formParada");

            fechaSubmenu();

            bundle = new Bundle();
            mFirebaseAnalytics.logEvent("clicou_fab_parada_mapa", bundle);
        } else{
            Toast.makeText(getApplicationContext(), "Coordenadas inválidas. Favor verificar o funcionamento do GPS.", Toast.LENGTH_SHORT).show();
        }

    }

    public void onFabPoiClick(View v){

        if(viewModel.localAtual != null && viewModel.localAtual.getValue().getLatitude() != 0 && viewModel.localAtual.getValue().getLongitude() != 0){
            formPoi = new FormPoi();
            formPoi.setLatitude(viewModel.localAtual.getValue().getLatitude());
            formPoi.setLongitude(viewModel.localAtual.getValue().getLongitude());
            //formParada.setParada(new ParadaSugestao());
            formPoi.setCtx(getApplication());
            formPoi.show(getSupportFragmentManager(), "formPoi");

            fechaSubmenu();

            bundle = new Bundle();
            mFirebaseAnalytics.logEvent("clicou_fab_poi_mapa", bundle);
        } else{
            Toast.makeText(getApplicationContext(), "Coordenadas inválidas. Favor verificar o funcionamento do GPS.", Toast.LENGTH_SHORT).show();
        }

    }

    public void onFabSugestaoClick(View v){
        Intent i = new Intent(this, ParadasSugeridasActivity.class);
        startActivity(i);

        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("clicou_fab_sugestoes_mapa", bundle);
    }

    public void onFabLocationClick(View v){
        mapController.animateTo(new GeoPoint(viewModel.localAtual.getValue().getLatitude(),
                viewModel.localAtual.getValue().getLongitude()));

        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("clicou_fab_local_mapa", bundle);
    }

    public boolean onFabLocationLongClick(View v){
        viewModel.centralizaMapa = !viewModel.centralizaMapa;

        if(viewModel.centralizaMapa){
            mapController.animateTo(new GeoPoint(viewModel.localAtual.getValue().getLatitude(),
                    viewModel.localAtual.getValue().getLongitude()));
            ViewCompat.setBackgroundTintList(v, ColorStateList.valueOf(Color.GREEN));
        } else{
            ViewCompat.setBackgroundTintList(v, ColorStateList.valueOf(Color.DKGRAY));
        }

        v.invalidate();

        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("press_fab_local_mapa", bundle);

        return true;

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

    private boolean checarPermissoes(){

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissionStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return permissionGPS == PackageManager.PERMISSION_GRANTED && permissionStorage == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(map != null){
            map.onResume();
        }

        startLocationUpdates();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(map != null){
            map.onPause();
        }

        stopLocationUpdates();

    }

    @Override
    public void onGpsChanged(boolean ativo) {

        if(!ativo){
            binding.textViewGps.setText(R.string.text_gps);
            binding.textViewGps.setVisibility(View.VISIBLE);
            binding.fabParada.setEnabled(false);
            binding.fabParada.setVisibility(View.GONE);
            binding.fabMeuLocal.setEnabled(false);
            binding.map.setEnabled(false);
        } else{
//            binding.textViewGps.setVisibility(View.GONE);
            binding.textViewGps.setText(R.string.text_procurando_gps);

            if(SessionUtils.estaLogado(getApplicationContext())){
                binding.fabParada.setEnabled(true);
                binding.fabParada.setVisibility(View.VISIBLE);
            } else{
                binding.fabParada.setEnabled(false);
                binding.fabParada.setVisibility(View.GONE);
            }

            binding.fabMeuLocal.setEnabled(true);

            binding.map.setEnabled(true);
        }

    }

    private void updateUI(FirebaseUser account){

        if(account != null){
            binding.btnLogin.setVisibility(View.GONE);

            if(gpsAtivo){
                binding.fabParada.setEnabled(true);
                binding.fabMeuLocal.setEnabled(true);
                configuraActivity();
            } else{
                binding.fabParada.setEnabled(false);
                binding.fabMeuLocal.setEnabled(false);
            }

            Toast.makeText(getApplicationContext(), "Login realizado com sucesso! " +
                    "Seja bem vindo, "+account.getDisplayName()+"!", Toast.LENGTH_LONG).show();

            bundle = new Bundle();
            mFirebaseAnalytics.logEvent("login_mapa", bundle);
            logado = true;

        } else{
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.fabParada.setEnabled(false);
            logado = false;
        }

    }

    private void signOut() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
        PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "");

        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("logoff_mapa", bundle);
        logado = false;
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

                            PreferenceDownloadAsyncTask preferenceDownloadAsyncTask = new PreferenceDownloadAsyncTask(getApplicationContext(),
                                    PreferenceUtils.carregarUsuarioLogado(getApplicationContext()));
                            preferenceDownloadAsyncTask.execute();
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

    private void abreSubmenu(){

        binding.linearLayoutParada.setEnabled(true);
        binding.linearLayoutPoi.setEnabled(true);

        binding.fabParadaSug.setEnabled(true);
        binding.fabPoiSug.setEnabled(true);

        binding.linearLayoutParada.animate().alpha(1);
        binding.linearLayoutPoi.animate().alpha(1);

        binding.linearLayoutParada.animate().translationY(-55);
        binding.linearLayoutPoi.animate().translationY(-105);
        submenuAberto = true;
    }

    private void fechaSubmenu(){
        binding.linearLayoutParada.animate().translationY(0);
        binding.linearLayoutPoi.animate().translationY(0);

        ocultaSubmenu();

        submenuAberto = false;
    }

    private void ocultaSubmenu(){
        binding.linearLayoutParada.animate().alpha(0);
        binding.linearLayoutPoi.animate().alpha(0);

        binding.linearLayoutParada.setEnabled(false);
        binding.linearLayoutPoi.setEnabled(false);

        binding.fabParadaSug.setEnabled(false);
        binding.fabPoiSug.setEnabled(false);

        submenuAberto = false;
    }

    private void geraModalLoading() {
        binding.fundo.setVisibility(View.VISIBLE);
        binding.textViewCarregando.setVisibility(View.VISIBLE);
        binding.progressBar.setIndeterminate(true);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void ocultaModalLoading(){
        binding.fundo.setVisibility(View.GONE);
        binding.textViewCarregando.setVisibility(View.GONE);
        binding.progressBar.setIndeterminate(true);
        binding.progressBar.setVisibility(View.GONE);
    }

    TapTargetView.Listener l2 = new TapTargetView.Listener(){
        @Override
        public void onTargetClick(TapTargetView view) {
            super.onTargetClick(view);
            DestaqueUtils.geraDestaqueUnico(ctx, binding.fabParada, "Faça sua sugestão de parada ou " +
                            "ponto de interesse",
                    "Primeiro, pressione aqui para mostrar as opções!",
                    new TapTargetView.Listener(){
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            onFabAddClick(view);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    DestaqueUtils.geraDestaqueUnico(ctx, binding.fabParadaSug,
                                            "Escolha a opção desejada", "Depois, escolha a opção desejada. " +
                                                    "Você pode sugerir tanto paradas quanto pontos de interesse!",
                                            l3, false, true);
                                }
                            }, 300);

                        }
                    }, false, true);
        }
    };

    TapTargetView.Listener l3 = new TapTargetView.Listener(){
        @Override
        public void onTargetClick(TapTargetView view) {
            super.onTargetClick(view);
            binding.fabParadaSug.performClick();
        }
    };

    @Override
    public void onToolbarItemSelected(View v) {
        criaTour();
    }

    @Override
    public List<TapTarget> criaTour() {

        if(SessionUtils.estaLogado(getApplicationContext())){

            if(gpsAtivo){

                DestaqueUtils.geraDestaqueUnico(this, binding.fabMeuLocal, "Botão \"Meu local\"",
                        "Pressione para centralizar o mapa na sua posição atual. Mantenha pressionado " +
                                "para ligar ou desligar a centralização automática!",
                        l2, false, true);
            } else{
                Toast.makeText(getApplicationContext(),
                        "Por favor, ative o GPS para que o mapa funcione corretamente.", Toast.LENGTH_SHORT).show();
            }

        } else{
            DestaqueUtils.geraDestaqueUnico(this, binding.btnLogin, "Faça login para mais opções",
                    "Faça login e descubra novas funções no mapa, como sugerir novas paradas e pontos de interesse!",
                    new TapTargetView.Listener(){
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            onClickBtnLogin(view);

                        }
                    }, false, true);
        }



        List<TapTarget> targets = new ArrayList<>();

        return targets;
    }

//    Observer<List<ParadaBairro>> paradasPoiObserver = new Observer<List<ParadaBairro>>() {
//        @Override
//        public void onChanged(List<ParadaBairro> paradas) {
//
//            List<String> listParadas = new ArrayList<>();
//
//            for(ParadaBairro p : paradas){
//
//                listParadas.add(p.getParada().getId());
//
//                System.out.println("PARADAS: "+p.getParada().getId()+" | "+p.getParada().getNome()+" - "+p.getNomeBairroComCidade());
//            }
//
//            viewModelPoi.listarTodosAtivosProximosPoi(listParadas);
//            viewModelPoi.itinerarios.observe(ctx, itinerariosPoiObserver);
//
//        }
//    };
//
//    Observer<List<ItinerarioPartidaDestino>> itinerariosPoiObserver = new Observer<List<ItinerarioPartidaDestino>>() {
//        @Override
//        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
//
//            ItinerarioCompactoAdapter adapter = new ItinerarioCompactoAdapter(itinerarios, ctx);
//            RecyclerView listItinerarios = bsdPoi.findViewById(R.id.listItinerarios);
//            listItinerarios.setAdapter(adapter);
//
//            listItinerarios.setLayoutManager(new GridLayoutManager(ctx, 1));
//
//            for(ItinerarioPartidaDestino i : itinerarios){
//                System.out.println("ITINERARIOS: "+i.getItinerario().getId()+" | "+i.getNomePartida()+", "+i.getNomeBairroPartida()+" - "+i.getNomeDestino()+", "+i.getNomeBairroDestino());
//            }
//
//            bsdPoi.show();
//
//        }
//    };

}
