package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.HistoricoSecao;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.utils.PreferenceUtils;

public class TarifasItinerariosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public static MutableLiveData<List<ItinerarioPartidaDestino>> itinerarios;

    public LiveData<List<ItinerarioPartidaDestino>> getItinerarios() {
        return itinerarios;
    }

    public void setItinerarios(MutableLiveData<List<ItinerarioPartidaDestino>> itinerarios) {
        this.itinerarios = itinerarios;
    }

    public TarifasItinerariosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());

        itinerarios = new MutableLiveData<>();
        itinerarios.postValue(null);
    }

    // atualizar

    public void atualizar(final List<ItinerarioPartidaDestino> itinerarios, Context context) {

        for(ItinerarioPartidaDestino i : itinerarios){
            i.getItinerario().setUltimaAlteracao(DateTime.now());
            i.getItinerario().setEnviado(false);
            i.getItinerario().setUsuarioUltimaAlteracao(PreferenceUtils.carregarUsuarioLogado(getApplication()));
        }

        new atualizarAsyncTask(appDatabase, itinerarios, context).execute();

    }

    private static class atualizarAsyncTask extends AsyncTask<Void, Void, Void> {

        private AppDatabase db;
        private List<ItinerarioPartidaDestino> itinerarios;
        Context ctx;

        atualizarAsyncTask(AppDatabase appDatabase, List<ItinerarioPartidaDestino> itinerarios, Context ctx) {
            db = appDatabase;
            this.itinerarios = itinerarios;
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(final Void... params) {

            for(ItinerarioPartidaDestino i : itinerarios){

                HistoricoItinerario hi = new HistoricoItinerario();
                hi.setItinerario(i.getItinerario().getId());
                hi.setTarifa(i.getItinerario().getTarifa());
                hi.setAtivo(true);
                hi.setDataCadastro(new DateTime());
                hi.setEnviado(false);
                hi.setUltimaAlteracao(new DateTime());

                db.historicoItinerarioDAO().inserir(hi);

                i.getItinerario().setTarifa(i.getNovaTarifa());

                db.itinerarioDAO().editar(i.getItinerario());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            TarifasViewModel.retorno.setValue(1);
            Toast.makeText(ctx, "Itinerarios Atualizados!", Toast.LENGTH_SHORT).show();
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

            itis = db.itinerarioDAO().listarTodosAtivosSimplificadoSync();

            itinerarios.postValue(itis);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            TarifasViewModel.retorno.setValue(1);

        }

    }

}
