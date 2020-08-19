package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.HistoricoSecao;
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

    // atualizar

    public void atualizar(final List<SecaoItinerario> secoes, Context context) {

        for(SecaoItinerario s : secoes){
            s.setUltimaAlteracao(DateTime.now());
            s.setEnviado(false);
            s.setUsuarioUltimaAlteracao(PreferenceUtils.carregarUsuarioLogado(getApplication()));
        }

        new atualizarAsyncTask(appDatabase, secoes, context).execute();

    }

    private static class atualizarAsyncTask extends AsyncTask<Void, Void, Void> {

        private AppDatabase db;
        private List<SecaoItinerario> secoes;
        Context ctx;

        atualizarAsyncTask(AppDatabase appDatabase, List<SecaoItinerario> secoes, Context ctx) {
            db = appDatabase;
            this.secoes = secoes;
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(final Void... params) {

            for(SecaoItinerario s : secoes){

                HistoricoSecao hs = new HistoricoSecao();
                hs.setSecao(s.getId());
                hs.setTarifa(s.getTarifa());
                hs.setAtivo(true);
                hs.setDataCadastro(new DateTime());
                hs.setEnviado(false);
                hs.setUltimaAlteracao(new DateTime());

                db.historicoSecaoDAO().inserir(hs);

                s.setTarifa(s.getNovaTarifa());

                db.secaoItinerarioDAO().editar(s);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            TarifasViewModel.retorno.setValue(1);
            Toast.makeText(ctx, "Tarifas Atualizadas!", Toast.LENGTH_SHORT).show();
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
            List<ItinerarioPartidaDestino> comSecoes = new ArrayList<>();

            for(ItinerarioPartidaDestino i : itis){
                List<SecaoItinerario> secoes = db.secaoItinerarioDAO().listarTodosPorItinerarioSync(i.getItinerario().getId());

                if(secoes != null && secoes.size() > 0){
                    i.setSecoes(secoes);
                    comSecoes.add(i);
                }

            }

            itinerarios.postValue(comSecoes);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            TarifasViewModel.retorno.setValue(1);

        }

    }

}
