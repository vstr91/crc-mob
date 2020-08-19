package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.ViagemItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;

public class ViagensItinerarioViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<ViagemItinerario>> viagens;
    public ViagemItinerario viagem;

    public static MutableLiveData<Integer> retorno;

    public void setItinerario(String itinerario){
        viagens = appDatabase.viagemItinerarioDAO().listarTodosPorItinerario(itinerario);
    }

    public ViagensItinerarioViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        viagem = new ViagemItinerario();
        viagens = appDatabase.viagemItinerarioDAO().listarTodosPorItinerario("-1");

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void editarViagem(String id, Context context){

        ViagemItinerario viagem = new ViagemItinerario();
        viagem.setId(id);
        edit(viagem, context, true);
    }

    // editar

    public static void edit(final ViagemItinerario viagem, Context context, boolean edicaoManual) {

        if(appDatabase == null){
            appDatabase = AppDatabase.getAppDatabase(context.getApplicationContext());
        }

        new editAsyncTask(appDatabase, edicaoManual).execute(viagem);
    }

    private static class editAsyncTask extends AsyncTask<ViagemItinerario, Void, Void> {

        private AppDatabase db;
        private boolean edicaoManual;

        editAsyncTask(AppDatabase appDatabase, boolean edicaoManual) {
            db = appDatabase;
            this.edicaoManual = edicaoManual;
        }

        @Override
        protected Void doInBackground(final ViagemItinerario... params) {

            params[0] = db.viagemItinerarioDAO().carregar(params[0].getId());

            if(edicaoManual){
                params[0].setAtivo(false);
            }

            params[0].setUltimaAlteracao(new DateTime());
            params[0].setEnviado(false);


            db.viagemItinerarioDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(retorno != null){
                retorno.setValue(1);
            }

        }

    }

    // fim editar

}
