package br.com.vostre.circular.view;

import android.Manifest;
import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
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
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.StorageUtils;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
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
import br.com.vostre.circular.utils.DialogUtils;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.SessionUtils;
import br.com.vostre.circular.utils.SignInActivity;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ParadaAdapter;
import br.com.vostre.circular.view.adapter.SecaoItinerarioAdapter;
import br.com.vostre.circular.view.form.FormParada;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.view.utils.InfoWindowParada;
import br.com.vostre.circular.viewModel.BaseViewModel;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;
import br.com.vostre.circular.viewModel.MapaViewModel;

public class MapaActivity extends BaseActivity {

    ActivityMapaBinding binding;
    MapaViewModel viewModel;
    BaseViewModel baseViewModel;
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

    GoogleSignInClient mGoogleSignInClient;

    static int RC_SIGN_IN = 481;
    boolean flag = false;

    Bundle bundle;
    boolean logado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mapa);
        binding.setView(this);
        super.onCreate(savedInstanceState);
        setTitle("Mapa");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        // PARA TESTES

//        PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "306f25dc-7ffa-11e8-b8e2-34238774caa8");
//        PreferenceUtils.salvarUsuarioLogado(getApplicationContext(), "");

        // PARA TESTES

        ctx = this;

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
        Intent i = new Intent(getApplicationContext(), SignInActivity.class);
        startActivityForResult(i, RC_SIGN_IN);
        //binding.btnLogin.setEnabled(false);

        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("clicou_login_mapa", bundle);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RC_SIGN_IN){

            boolean logado = data.getExtras().getBoolean("logado");

            if(!logado){

                String e = data.getExtras().getString("mensagemErro");

                signOut();
                //binding.btnLogin.setEnabled(true);
            } else{
                account = (GoogleSignInAccount) data.getExtras().get("account");
                updateUI(account);
            }

        }

        formParada = (FormParada) DialogUtils.getOpenedDialog(this);

        if (requestCode == FormParada.PICK_IMAGE) {

            if (data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    viewModel.foto = BitmapFactory.decodeStream(inputStream);
                    formParada.exibeImagem();

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
        viewModel.localAtual.observe(this, localObserver);
        viewModel.iniciarAtualizacoesPosicao();

        bsd = new BottomSheetDialog(ctx);
        bsd.setCanceledOnTouchOutside(true);

        bsd.setContentView(R.layout.infowindow_parada);

        bsdPoi = new BottomSheetDialog(ctx);
        bsdPoi.setCanceledOnTouchOutside(true);

        bsdPoi.setContentView(R.layout.infowindow_poi);

        if(!gpsAtivo){
            binding.textViewGps.setVisibility(View.VISIBLE);
            binding.fabParada.setEnabled(false);
            binding.fabMeuLocal.setEnabled(false);
        } else{
            binding.textViewGps.setVisibility(View.GONE);
            binding.fabParada.setEnabled(true);
            binding.fabMeuLocal.setEnabled(true);
        }

        checaLogin();

        configuraMapa();
    }

    private void checaLogin() {
        if(SessionUtils.estaLogado(getApplicationContext())){
            binding.fabParada.setEnabled(true);
            //binding.btnLogin.setVisibility(View.GONE);
            binding.fabSugestao.setEnabled(true);
            binding.fabSugestao.setVisibility(View.VISIBLE);
        } else{
            binding.fabParada.setEnabled(false);
            //binding.btnLogin.setVisibility(View.VISIBLE);
            binding.fabSugestao.setEnabled(false);
            binding.fabSugestao.setVisibility(View.GONE);
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

                if(gpsAtivo){
                    return false;
                } else{
                    return true;
                }

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
            atualizarSugestoessMapa(paradas);
        }
    };

    Observer<List<PontoInteresse>> poisObserver = new Observer<List<PontoInteresse>>() {
        @Override
        public void onChanged(List<PontoInteresse> pois) {
            atualizarPoisMapa(pois);
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
                            final ParadaBairro pb = getParadaFromMarker(marker, paradas);

                            viewModel.setParada(pb);
                            viewModel.itinerarios.observe(ctx, itinerariosObserver);

                            RecyclerView listItinerarios = bsd.findViewById(R.id.listItinerarios);

                            adapterItinerarios = new ItinerarioAdapter(viewModel.itinerarios.getValue(), ctx);
                            listItinerarios.setAdapter(adapterItinerarios);

                            // bottom menu

                            TextView textViewReferencia = bsd.findViewById(R.id.textViewReferencia);
                            TextView textViewBairro = bsd.findViewById(R.id.textViewBairro);

                            textViewReferencia.setText(pb.getParada().getNome());
                            textViewBairro.setText(pb.getNomeBairroComCidade());

                            bsd.findViewById(R.id.textView32).setVisibility(View.VISIBLE);
                            bsd.findViewById(R.id.textViewId).setVisibility(View.VISIBLE);

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

                            textViewReferencia.setText(poi.getNome());

                            Button btnFechar = bsdPoi.findViewById(R.id.btnFechar);
                            btnFechar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bsdPoi.dismiss();
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

    private void atualizarSugestoessMapa(final List<ParadaSugestaoBairro> paradasSugeridas){

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
                            final ParadaSugestaoBairro p = getSugestaoFromMarker(marker, paradasSugeridas);

                            viewModel.setParadaNova(p);

                            // bottom menu

                            RecyclerView listItinerarios = bsd.findViewById(R.id.listItinerarios);

                            adapterItinerarios = new ItinerarioAdapter(new ArrayList<ItinerarioPartidaDestino>(), ctx);
                            listItinerarios.setAdapter(adapterItinerarios);

                            TextView textViewReferencia = bsd.findViewById(R.id.textViewReferencia);
                            TextView textViewBairro = bsd.findViewById(R.id.textViewBairro);

                            bsd.findViewById(R.id.textView32).setVisibility(View.GONE);
                            bsd.findViewById(R.id.textViewId).setVisibility(View.GONE);

                            textViewReferencia.setText(p.getParada().getNome());
                            textViewBairro.setText(p.getNomeBairroComCidade());

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

                            Button btnFechar = bsd.findViewById(R.id.btnFechar);
                            btnFechar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bsd.dismiss();
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

    public void onFabParadaClick(View v){

        if(viewModel.localAtual != null){
            formParada = new FormParada();
            formParada.setLatitude(viewModel.localAtual.getValue().getLatitude());
            formParada.setLongitude(viewModel.localAtual.getValue().getLongitude());
            //formParada.setParada(new ParadaSugestao());
            formParada.setCtx(getApplication());
            formParada.show(getSupportFragmentManager(), "formParada");

            bundle = new Bundle();
            mFirebaseAnalytics.logEvent("clicou_fab_parada_mapa", bundle);
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
            v.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        } else{
            v.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY));
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
            binding.fabMeuLocal.setEnabled(false);
            binding.map.setEnabled(false);
        } else{
//            binding.textViewGps.setVisibility(View.GONE);
            binding.textViewGps.setText(R.string.text_procurando_gps);

            if(SessionUtils.estaLogado(getApplicationContext())){
                binding.fabParada.setEnabled(true);
            }

            binding.fabMeuLocal.setEnabled(true);

            binding.map.setEnabled(true);
        }

    }

    private void updateUI(GoogleSignInAccount account){

        if(account != null){
            //binding.btnLogin.setVisibility(View.GONE);

            if(gpsAtivo){
                binding.fabParada.setEnabled(true);
                binding.fabMeuLocal.setEnabled(true);
                configuraActivity();
            }

            Toast.makeText(getApplicationContext(), "Login realizado com sucesso! " +
                    "Seja bem vindo, "+account.getGivenName()+"!", Toast.LENGTH_LONG).show();

            bundle = new Bundle();
            mFirebaseAnalytics.logEvent("login_mapa", bundle);
            logado = true;

        } else{
            //binding.btnLogin.setVisibility(View.VISIBLE);
            binding.fabParada.setEnabled(false);
            logado = false;
        }

    }

    private void signOut() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE)
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

}
