package br.com.vostre.circular.view;

import android.Manifest;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.pm.PackageManager;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.FilenameFilter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityComparaTrajetoBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ViagemItinerario;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.service.LocationUpdatesService;
import br.com.vostre.circular.utils.FileUtils;
import br.com.vostre.circular.utils.LocationResultHelper;
import br.com.vostre.circular.utils.LocationUpdatesBroadcastReceiver;
import br.com.vostre.circular.view.adapter.ParadaAdapter;
import br.com.vostre.circular.viewModel.RegistraViagemViewModel;

import static br.com.vostre.circular.utils.LocationResultHelper.KEY_LISTA;

public class ComparaTrajetoActivity extends BaseActivity {

    ActivityComparaTrajetoBinding binding;
    MapView map;
    IMapController mapController;

    RecyclerView listParadas;
    ParadaAdapter adapter;

    int permissionGPS;
    int permissionStorage;
    int permissionInternet;

    AppCompatActivity ctx;
    MyLocationNewOverlay mLocationOverlay;
    MapEventsOverlay overlayEvents;

    RegistraViagemViewModel viewModel;

    List<Location> locais;
    boolean gravando = false;

    Polyline line;
    NumberFormat nf = NumberFormat.getNumberInstance();

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private LocationUpdatesBroadcastReceiver myReceiver;

    DateTime horaInicial;
    DateTime horaFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compara_trajeto);
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
                Manifest.permission.ACCESS_FINE_LOCATION);
        permissionStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionInternet = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET);

        if(!checarPermissoes()){

//            finish();
            Toast.makeText(getApplicationContext(),
                    "Para acessar esta tela, por favor aceite as permissões de acesso.",
                    Toast.LENGTH_SHORT).show();

        } else{
            binding.setView(this);
            setTitle("Comparar Trajeto");
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            viewModel = ViewModelProviders.of(this).get(RegistraViagemViewModel.class);

            viewModel.setItinerario(getIntent().getStringExtra("itinerario"));

            viewModel.itinerario.observe(this, itinerarioObserver);
            viewModel.paradas.observe(this, paradasObserver);
            viewModel.localAtual.observe(this, localObserver);

            binding.setViewModel(viewModel);

            configuraMapa();

            atualizarParadasMapa(viewModel.paradas.getValue());

            //viewModel.iniciarAtualizacoesPosicao();

            ctx = this;

            MapEventsReceiver receiver = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {
                    return false;
                }

                @Override
                public boolean longPressHelper(GeoPoint p) {
                    return false;
                }
            };

            overlayEvents = new MapEventsOverlay(getBaseContext(), receiver);

        }
    }

    private void carregaGeoJson(final String itinerario){
        File[] s = getApplicationContext().getFilesDir().listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File file, String s) {
                return s.startsWith(itinerario) && s.endsWith("json");
            }
        });

        File ultima = null;

        for(File b : s){
            ultima = b;
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
        mapController.setZoom(19);

        map.setMaxZoomLevel(19d);
        map.setMinZoomLevel(9d);

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recarregaPolyline();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void recarregaPolyline() {
        map.getOverlays().clear();
        map.invalidate();

        atualizarParadasMapa(viewModel.paradas.getValue());

        // carregando rota
        String rota = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(KEY_LISTA, "");

        if(!rota.isEmpty()){
            rota = rota.substring(0, rota.length()-1);
            line = new Polyline();
            line.getOutlinePaint().setColor(Color.BLUE);

            locais = new ArrayList<>();

            String[] pontos = rota.split("\\|");

            for(String p : pontos){

                String[] ponto = p.split(";");

                GeoPoint g = new GeoPoint(Float.parseFloat(ponto[0]), Float.parseFloat(ponto[1]));

                Location l = new Location(LocationManager.GPS_PROVIDER);
                l.setLatitude(Float.parseFloat(ponto[0]));
                l.setLongitude(Float.parseFloat(ponto[1]));
                l.setAccuracy(Float.parseFloat(ponto[2]));
                l.setSpeed(Float.parseFloat(ponto[3]));
                l.setTime(Long.parseLong(ponto[4]));

                line.addPoint(g);
                locais.add(l);
            }

            map.getOverlays().add(line);
            map.invalidate();

        }
    }

    private boolean checarPermissoes(){

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        permissionStorage = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionInternet = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET);

        return permissionGPS == PackageManager.PERMISSION_GRANTED
                && permissionStorage == PackageManager.PERMISSION_GRANTED
                && permissionInternet == PackageManager.PERMISSION_GRANTED;
    }

    private void atualizarParadasMapa(final List<ParadaBairro> paradas){

        if(paradas != null){

            for(final ParadaBairro p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
                m.setTitle(p.getParada().getNome());
                m.setDraggable(false);
                m.setId(p.getParada().getId());
                map.getOverlays().add(m);
            }

        }

    }

    private void adicionaLocalMapa(Location local){

        if(local != null){

            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(local));
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
            m.setDraggable(false);
            map.getOverlays().add(m);

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

    @BindingAdapter("text")
    public static void setText(TextView view, DateTime date) {

        if(date != null){
            String formatted = DateTimeFormat.forPattern("HH:mm:ss").print(date);
            view.setText(formatted);
        } else{
            view.setText("-");
        }


    }

    @BindingAdapter("text")
    public static void setText(TextView view, Double value) {

        if(value == null){
            view.setText("0 Km");
        } else{

            try{
                String valor = String.valueOf(value).replace(".", ",");
                view.setText(valor+" Km");
            } catch(NumberFormatException e){
                view.setText("0 Km");
            }

        }


    }

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(final List<ParadaBairro> paradas) {
            map.getOverlays().clear();
            map.getOverlays().add(mLocationOverlay);
            map.getOverlays().add(overlayEvents);
            atualizarParadasMapa(paradas);

            adapter.paradas = paradas;
            adapter.notifyDataSetChanged();

//            if(viewModel.localAtual.getValue().distanceTo(viewModel.paradas.getValue().get(0).getLocation()) > 200){
//                Toast.makeText(getApplicationContext(), "Você está longe demais! "+viewModel.paradas.getValue().get(0).getLocation().getLatitude()+" | "
//                        +viewModel.paradas.getValue().get(0).getLocation().getLongitude(), Toast.LENGTH_SHORT).show();
//            }

            //viewModel.carregaDirections(map, paradas);

//            List<Overlay> ov = map.getOverlays().;
//            System.out.println(ov.size());
        }
    };

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {
            binding.setItinerario(itinerario);
        }
    };

    Observer<Location> localObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location local) {

            if(local.getLatitude() != 0.0 && local.getLongitude() != 0.0){
                setMapCenter(map, new GeoPoint(local.getLatitude(), local.getLongitude()));
            }

            if(gravando && locais != null){

                if(local.hasAccuracy() && local.getAccuracy() < 20){
                    locais.add(local);

                    //adicionaLocalMapa(local);

                    atualizaPolyline(local);
                }



            }



        }
    };

    public void atualizaPolyline(Location local){

        //if(local.hasAccuracy() && local.getAccuracy() < 20){

            //Toast.makeText(getApplicationContext(), "Vel.: "+local.getSpeed() * 3.6+" Km/h", Toast.LENGTH_SHORT).show();

            String velocidadeFormatada = nf.format(local.getSpeed() * 3.6);

            line.addPoint(new GeoPoint(local.getLatitude(), local.getLongitude()));
            map.getOverlays().add(line);
            map.invalidate();
        //}

    }

    private void salvarPolyline(Polyline line){

        if(line == null){
            recarregaPolyline();
        }

        if(line != null && line.getPoints().size() > 0){
            String geoJson = "{\"type\": \"FeatureCollection\",\"features\": [{\"type\": \"Feature\", \"properties\": {},\"geometry\": {\"type\": \"LineString\", \"coordinates\": [";

            for(GeoPoint geo : line.getPoints()){
                geoJson = geoJson.concat("["+geo.getLongitude()+","+geo.getLatitude()+"],");
            }

            geoJson = geoJson.substring(0, geoJson.length()-1);

            geoJson = geoJson.concat("]}}]}");

            String idItinerario = viewModel.itinerario.getValue().getItinerario().getId();

            String nomeArquivo = idItinerario
                    +"-"+DateTimeFormat.forPattern("yyyy-MM-dd-HH-mm-ss").print(DateTime.now())+".json";

            FileUtils.writeToFile(nomeArquivo, geoJson, getApplicationContext());

            salvarRegistroPontos(nomeArquivo.replace("json", "log"));

            // salvando registro de viagem no BD
            ViagemItinerario viagemItinerario = new ViagemItinerario();

            viagemItinerario.setItinerario(idItinerario);
            viagemItinerario.setTrajeto(nomeArquivo);
            viagemItinerario.setHoraInicial(new DateTime(LocationResultHelper.recuperaHoraInicial(getApplicationContext())));
            viagemItinerario.setHoraFinal(horaFinal);
            viagemItinerario.setAtivo(true);
            viagemItinerario.setEnviado(false);

            viewModel.salvarViagem(viagemItinerario);

        } else{
            Toast.makeText(getApplicationContext(), "Erro ao salvar viagem. Não foi possível recuperar os pontos ou não há pontos registrados.", Toast.LENGTH_SHORT).show();
        }



    }

    private void salvarRegistroPontos(String nome){

        if(locais != null){
            String pontos = "";

            for(Location geo : locais){
                pontos = pontos.concat(DateTimeFormat.forPattern("yyyy-MM-dd-HH:mm:ss").print(geo.getTime())
                        +","+geo.getLongitude()+","+geo.getLatitude()+","+geo.getSpeed()*3.6+" Km/h"+"\n");
            }

            FileUtils.writeToFile(nome, pontos, getApplicationContext());

            //Toast.makeText(getApplicationContext(), "Registro Salvo!", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(getApplicationContext(), "Nenhum ponto a ser salvo!", Toast.LENGTH_SHORT).show();
        }



    }

    @BindingAdapter("center")
    public static void setMapCenter(MapView map, GeoPoint geoPoint){

        if(geoPoint != null){
            map.getController().animateTo(geoPoint);
        }

    }

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getApplicationContext(), "Viagem cadastrada!", Toast.LENGTH_SHORT).show();
            } else if(retorno == 0){
                Toast.makeText(getApplicationContext(),
                        "Erro ao salvar viagem.",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

}
