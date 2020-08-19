package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;

public class DetalhesOnibusViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<Onibus> onibus;

    public MutableLiveData<Location> localAtual;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    public LiveData<ItinerarioPartidaDestino> itinerario;

    public static MutableLiveData<Integer> retorno;

    public LiveData<Onibus> getOnibus() {
        return onibus;
    }

    public void setOnibus(String onibus) {
        this.onibus = appDatabase.onibusDAO().carregar(onibus);
    }

    public DetalhesOnibusViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        onibus = appDatabase.onibusDAO().carregar("");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());

        localAtual = new MutableLiveData<>();
        localAtual.setValue(new Location(LocationManager.GPS_PROVIDER));

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void iniciarAtualizacoesPosicao(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    if(location.getAccuracy() <= 10){
                        localAtual.setValue(location);

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
