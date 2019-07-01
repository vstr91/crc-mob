package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Problema;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ProblemaTipo;

public class ProblemasViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<ProblemaTipo>> abertos;
    public LiveData<List<ProblemaTipo>> resolvidos;
    public Problema problema;

    public static MutableLiveData<Integer> retorno;

    public Problema getProblema() {
        return problema;
    }

    public void setProblema(Problema problema) {
        this.problema = problema;
    }

    public ProblemasViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        problema = new Problema();
        //abertos = appDatabase.problemaDAO().listarTodosAbertos();
        //resolvidos = appDatabase.problemaDAO().listarTodosResolvidos();

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void editarProblema(){

        if(problema.valida(problema)){
            edit(problema);
        } else{
            retorno.setValue(0);
        }

    }

    // editar

    public void edit(final Problema problema) {

        problema.setUltimaAlteracao(new DateTime());
        problema.setEnviado(false);

        new editAsyncTask(appDatabase).execute(problema);
    }

    private static class editAsyncTask extends AsyncTask<Problema, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Problema... params) {
            db.problemaDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim editar

}
