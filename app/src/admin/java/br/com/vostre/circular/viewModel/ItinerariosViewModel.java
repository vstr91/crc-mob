package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;

public class ItinerariosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;
    public Itinerario itinerario;

    public MutableLiveData<List<ParadaItinerarioBairro>> paradasItinerario;
    public ParadaItinerarioBairro parada;

    public LiveData<List<ParadaBairro>> paradas;
    public MutableLiveData<Location> localAtual;
    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    public Empresa empresa;
    public LiveData<List<Empresa>> empresas;

    public LiveData<Empresa> umaEmpresa;

    public LiveData<List<ItinerarioPartidaDestino>> getItinerarios() {
        return itinerarios;
    }

    public static MutableLiveData<Integer> retorno;

    public void setItinerarios(LiveData<List<ItinerarioPartidaDestino>> itinerarios) {
        this.itinerarios = itinerarios;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public LiveData<List<ParadaItinerarioBairro>> getParadasItinerario() {
        return paradasItinerario;
    }

    public void setParadasItinerario(MutableLiveData<List<ParadaItinerarioBairro>> paradasItinerario) {
        this.paradasItinerario = paradasItinerario;
    }

    public ParadaItinerarioBairro getParada() {
        return parada;
    }

    public void setParada(ParadaItinerarioBairro parada) {
        this.parada = parada;
    }

    public LiveData<List<ParadaBairro>> getParadas() {
        return paradas;
    }

    public void setParadas(LiveData<List<ParadaBairro>> paradas) {
        this.paradas = paradas;
    }

    public void setEmpresa(String empresa) {
        this.umaEmpresa = appDatabase.empresaDAO().carregar(empresa);
    }

    public ItinerariosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerario = new Itinerario();
        itinerarios = appDatabase.itinerarioDAO().listarTodos();
        paradas = appDatabase.paradaDAO().listarTodosAtivosComBairro();
        empresas = appDatabase.empresaDAO().listarTodosAtivos();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());

        localAtual = new MutableLiveData<>();
        localAtual.setValue(new Location(LocationManager.GPS_PROVIDER));

        paradasItinerario = new MutableLiveData<>();
        paradasItinerario.setValue(new ArrayList<ParadaItinerarioBairro>());

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);

    }

    public void salvarItinerario(){

        itinerario.setEmpresa(empresa.getId());

        if(itinerario.valida(itinerario)){
            add(itinerario);
            addParadas(this.paradasItinerario.getValue());

        } else{
            retorno.setValue(0);
        }

    }

    public void editarItinerario(){

        itinerario.setEmpresa(empresa.getId());

        if(itinerario.valida(itinerario)){
            edit(itinerario);
        } else{
            retorno.setValue(0);
        }

    }

    public void salvarParada(){

    }

    public void editarParada(){
        List<ParadaItinerarioBairro> pibs = this.paradasItinerario.getValue();
//        ParadaItinerarioBairro pib = pibs.get(pibs.indexOf(parada));
//        pib = parada;
        this.paradasItinerario.postValue(pibs);

    }

    // adicionar paradas

    public void addParadas(List<ParadaItinerarioBairro> pibs) {

        int cont = 1;

        for(ParadaItinerarioBairro pib : pibs){
            pib.getParadaItinerario().setItinerario(itinerario.getId());
            pib.getParadaItinerario().setOrdem(cont);

            pib.getParadaItinerario().setDataCadastro(new DateTime());
            pib.getParadaItinerario().setUltimaAlteracao(new DateTime());
            pib.getParadaItinerario().setEnviado(false);

            new addParadaAsyncTask(appDatabase).execute(pib.getParadaItinerario());

            cont++;
        }



    }

    private static class addParadaAsyncTask extends AsyncTask<ParadaItinerario, Void, Void> {

        private AppDatabase db;

        addParadaAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ParadaItinerario... params) {
            db.paradaItinerarioDAO().inserir(params[0]);
            return null;
        }

    }

    // fim adicionar paradas

    // adicionar

    public void add(final Itinerario itinerario) {

        itinerario.setDataCadastro(new DateTime());
        itinerario.setUltimaAlteracao(new DateTime());
        itinerario.setEnviado(false);

        new addAsyncTask(appDatabase).execute(itinerario);
    }

    private static class addAsyncTask extends AsyncTask<Itinerario, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Itinerario... params) {
            db.itinerarioDAO().inserir((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ItinerariosViewModel.retorno.setValue(1);
        }
    }

    // fim adicionar

    // editar

    public void edit(final Itinerario itinerario) {

        itinerario.setUltimaAlteracao(new DateTime());
        itinerario.setEnviado(false);

        new editAsyncTask(appDatabase).execute(itinerario);
    }

    private static class editAsyncTask extends AsyncTask<Itinerario, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Itinerario... params) {
            db.itinerarioDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ItinerariosViewModel.retorno.setValue(1);
        }

    }

    // fim editar

    public void iniciarAtualizacoesPosicao(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    if(location.getAccuracy() <= 20){
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
