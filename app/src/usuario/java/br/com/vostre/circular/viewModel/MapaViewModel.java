package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.util.List;

import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;

public class MapaViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<ParadaBairro>> paradas;
    public LiveData<List<PontoInteresse>> pois;
    public MutableLiveData<Location> localAtual;
    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    ParadaBairro parada;
    public Bitmap foto;
    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;

    public ParadaBairro getParada() {
        return parada;
    }

    public void setParada(ParadaBairro parada) {
        this.parada = parada;
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada.getParada().getId(),
                DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime()));

        if(parada.getParada().getImagem() != null){
            File foto = new File(getApplication().getFilesDir(), parada.getParada().getImagem());

            if(foto.exists() && foto.canRead()){
                this.foto = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.foto = null;
        }

    }

    public void setItinerario(String itinerario) {
        this.paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro(itinerario);
    }

    public LiveData<List<ParadaBairro>> getParadas() {
        return paradas;
    }

    public void setParadas(LiveData<List<ParadaBairro>> paradas) {
        this.paradas = paradas;
    }

    public MapaViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        paradas = appDatabase.paradaDAO().listarTodosAtivosComBairro();
        pois = appDatabase.pontoInteresseDAO().listarTodosAtivos();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());

        localAtual = new MutableLiveData<>();
        localAtual.postValue(new Location(LocationManager.GPS_PROVIDER));
    }

    public void iniciarAtualizacoesPosicao(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    if(location.getAccuracy() <= 20){
                        localAtual.postValue(location);

                        if(localAtual.getValue() != null){
                            localAtual.getValue().setLatitude(localAtual.getValue().getLatitude());
                            localAtual.getValue().setLongitude(localAtual.getValue().getLongitude());
                        }

                    }

                }
            }
        };
    }

}
