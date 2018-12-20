package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.database.Observable;
import android.databinding.ObservableField;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;

public class DetalhesItinerarioViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<ItinerarioPartidaDestino> itinerario;

    public MutableLiveData<List<ParadaItinerarioBairro>> paradasItinerario;
    public LiveData<List<ParadaItinerarioBairro>> pits;
    public ParadaItinerarioBairro parada;

    public LiveData<List<ParadaBairro>> paradas;
    public MutableLiveData<Location> localAtual;
    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    public Empresa empresa;
    public LiveData<List<Empresa>> empresas;

    public static MutableLiveData<Integer> retorno;

    public ObservableField<ItinerarioPartidaDestino> iti;

    public LiveData<List<HistoricoItinerario>> historicoItinerario;

    public ObservableField<ItinerarioPartidaDestino> getIti() {
        return iti;
    }

    public void setIti(ObservableField<ItinerarioPartidaDestino> iti) {
        this.iti = iti;
    }

    public LiveData<ItinerarioPartidaDestino> getItinerario() {
        return itinerario;
    }

    public void setItinerario(String itinerario) {
        this.itinerario = appDatabase.itinerarioDAO().carregar(itinerario);
        this.pits = appDatabase.paradaItinerarioDAO().listarTodosAtivosPorItinerarioComBairro(itinerario);
        this.historicoItinerario = appDatabase.historicoItinerarioDAO().carregarPorItinerario(itinerario);
//        paradasItinerario.setValue(appDatabase.paradaItinerarioDAO()
//                .listarTodosAtivosPorItinerarioComBairro(itinerario).getValue());
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

    public DetalhesItinerarioViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerario = appDatabase.itinerarioDAO().carregar("");
        paradas = appDatabase.paradaDAO().listarTodosAtivosComBairro();
        empresas = appDatabase.empresaDAO().listarTodosAtivos();

        iti = new ObservableField<>(new ItinerarioPartidaDestino());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());

        localAtual = new MutableLiveData<>();
        localAtual.setValue(new Location(LocationManager.GPS_PROVIDER));

        paradasItinerario = new MutableLiveData<>();
        paradasItinerario.setValue(new ArrayList<ParadaItinerarioBairro>());

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void salvarItinerario(){

        Itinerario umItinerario = itinerario.getValue().getItinerario();
        umItinerario.setEmpresa(empresa.getId());

        if(umItinerario.valida(umItinerario)){
            add(umItinerario);
            addParadas(this.paradasItinerario.getValue());

        } else{
            retorno.setValue(0);
        }

    }

    public void editarItinerario(){

        Itinerario umItinerario = itinerario.getValue().getItinerario();
        umItinerario.setEmpresa(empresa.getId());

        if(umItinerario.valida(umItinerario)){
            edit(umItinerario);
        } else{
            retorno.setValue(0);
        }

    }

    public void salvarParadas(){
        addParadas(this.paradasItinerario.getValue());
    }

    public void editarParada(){
        List<ParadaItinerarioBairro> pibs = this.paradasItinerario.getValue();
//        ParadaItinerarioBairro pib = pibs.get(pibs.indexOf(parada));
//        pib = parada;
        this.paradasItinerario.postValue(pibs);
        this.parada = null;

    }

    // adicionar paradas

    public void addParadas(List<ParadaItinerarioBairro> pibs) {

        int cont = 1;

        new invalidaParadasAsyncTask(appDatabase).execute(itinerario.getValue().getItinerario());

        for(ParadaItinerarioBairro pib : pibs){
            pib.getParadaItinerario().setItinerario(itinerario.getValue().getItinerario().getId());
            pib.getParadaItinerario().setOrdem(cont);
            pib.getParadaItinerario().setAtivo(true);

            pib.getParadaItinerario().setDataCadastro(new DateTime());
            pib.getParadaItinerario().setUltimaAlteracao(new DateTime());
            pib.getParadaItinerario().setEnviado(false);

            new addParadaAsyncTask(appDatabase).execute(pib.getParadaItinerario());

            cont++;
        }

        paradasItinerario.postValue(appDatabase.paradaItinerarioDAO().listarTodosAtivosPorItinerarioComBairro(itinerario.getValue().getItinerario().getId()).getValue());

    }

    private static class invalidaParadasAsyncTask extends AsyncTask<Itinerario, Void, Void> {

        private AppDatabase db;

        invalidaParadasAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Itinerario... params) {

            db.paradaItinerarioDAO().invalidaTodosPorItinerario(params[0].getId());

            return null;
        }

    }

    private static class addParadaAsyncTask extends AsyncTask<ParadaItinerario, Void, Void> {

        private AppDatabase db;

        addParadaAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ParadaItinerario... params) {

            ParadaItinerario paradaItinerario = db.paradaItinerarioDAO().checaDuplicidade(params[0].getParada(), params[0].getItinerario());

            if(paradaItinerario != null){
                ParadaItinerario pi = params[0];

                paradaItinerario.setAtivo(true);
                paradaItinerario.setOrdem(pi.getOrdem());
                paradaItinerario.setDestaque(pi.getDestaque());
                paradaItinerario.setValorAnterior(pi.getValorAnterior());
                paradaItinerario.setValorSeguinte(pi.getValorSeguinte());

                db.paradaItinerarioDAO().editar(paradaItinerario);
            } else{
                db.paradaItinerarioDAO().inserir(params[0]);
            }


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
            DetalhesItinerarioViewModel.retorno.setValue(1);
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
