package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.utils.StringUtils;

public class TarifasViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;

    public LiveData<List<ItinerarioPartidaDestino>> getItinerarios() {
        return itinerarios;
    }

    public void setItinerarios(LiveData<List<ItinerarioPartidaDestino>> itinerarios) {
        this.itinerarios = itinerarios;
    }

    public TarifasViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivos();
    }

    public void editarPais(){

    }

    // editar

    public void edit(final List<ItinerarioPartidaDestino> itinerarios) {

//        pais.setUltimaAlteracao(new DateTime());
//        pais.setEnviado(false);
//        pais.setSlug(StringUtils.toSlug(pais.getNome()));
//
//        new editAsyncTask(appDatabase).execute(pais);
    }

    private static class editAsyncTask extends AsyncTask<Pais, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Pais... params) {
            db.paisDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            TarifasViewModel.retorno.setValue(1);
        }

    }

    // fim editar

}
