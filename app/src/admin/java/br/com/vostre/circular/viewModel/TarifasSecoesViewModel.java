package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.utils.PreferenceUtils;

public class TarifasSecoesViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public static MutableLiveData<List<ItinerarioPartidaDestino>> itinerarios;

    public LiveData<List<ItinerarioPartidaDestino>> getItinerarios() {
        return itinerarios;
    }

    public void setItinerarios(MutableLiveData<List<ItinerarioPartidaDestino>> itinerarios) {
        this.itinerarios = itinerarios;
    }

    public TarifasSecoesViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());

        itinerarios = new MutableLiveData<>();
        itinerarios.postValue(null);
    }

    public void editarPais(){

    }

    // editar

    public void edit(final List<ItinerarioPartidaDestino> itinerarios, Double tarifa) {

        for(ItinerarioPartidaDestino i : itinerarios){
            i.getItinerario().setTarifa(tarifa);
            i.getItinerario().setUltimaAlteracao(DateTime.now());
            i.getItinerario().setEnviado(false);
            i.getItinerario().setUsuarioUltimaAlteracao(PreferenceUtils.carregarUsuarioLogado(getApplication()));
        }

        new editAsyncTask(appDatabase, itinerarios).execute();

    }

    private static class editAsyncTask extends AsyncTask<Pais, Void, Void> {

        private AppDatabase db;
        private List<ItinerarioPartidaDestino> itinerarios;

        editAsyncTask(AppDatabase appDatabase, List<ItinerarioPartidaDestino> itinerarios) {
            db = appDatabase;
            this.itinerarios = itinerarios;
        }

        @Override
        protected Void doInBackground(final Pais... params) {

            for(ItinerarioPartidaDestino i : itinerarios){
                db.itinerarioDAO().editar(i.getItinerario());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            TarifasViewModel.retorno.setValue(1);
        }

    }

    // fim editar

    public void carregarItinerarios() {
        new carregarItinerariosAsyncTask(appDatabase).execute();
    }

    private static class carregarItinerariosAsyncTask extends AsyncTask<Void, Void, Void> {

        private AppDatabase db;
        private List<ItinerarioPartidaDestino> itis;
        private

        carregarItinerariosAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Void... params) {

            itis = db.itinerarioDAO().listarTodosAtivosSync();

            for(ItinerarioPartidaDestino i : itis){
                List<SecaoItinerario> secoes = db.secaoItinerarioDAO().listarTodosPorItinerarioSync(i.getItinerario().getId());
                i.setSecoes(secoes);
            }

            itinerarios.postValue(itis);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            TarifasViewModel.retorno.setValue(1);

        }

    }

}
