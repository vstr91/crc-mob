package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Parametro;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.utils.StringUtils;

public class ParametrosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<Parametro>> parametros;
    public Parametro parametro;

    public static MutableLiveData<Integer> retorno;

    public LiveData<List<Parametro>> getParametros() {
        return parametros;
    }

    public void setParametros(LiveData<List<Parametro>> parametros) {
        this.parametros = parametros;
    }

    public Parametro getParametro() {
        return parametro;
    }

    public void setParametro(Parametro parametro) {
        this.parametro = parametro;
    }

    public ParametrosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        parametro = new Parametro();
        parametros = appDatabase.parametroDAO().listarTodos();

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void salvarParametro(){

        if(parametro.valida(parametro)){
            add(parametro);
        } else{
            retorno.setValue(0);
        }

    }

    public void editarParametro(){

        if(parametro.valida(parametro)){
            edit(parametro);
        } else{
            retorno.setValue(0);
        }

    }

    // adicionar

    public void add(final Parametro parametro) {

        parametro.setDataCadastro(new DateTime());
        parametro.setUltimaAlteracao(new DateTime());
        parametro.setEnviado(false);
        parametro.setSlug(StringUtils.toSlug(parametro.getNome()));

        new addAsyncTask(appDatabase).execute(parametro);
    }

    private static class addAsyncTask extends AsyncTask<Parametro, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Parametro... params) {
            db.parametroDAO().inserir((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim adicionar

    // editar

    public void edit(final Parametro parametro) {

        parametro.setUltimaAlteracao(new DateTime());
        parametro.setEnviado(false);
        parametro.setSlug(StringUtils.toSlug(parametro.getNome()));

        new editAsyncTask(appDatabase).execute(parametro);
    }

    private static class editAsyncTask extends AsyncTask<Parametro, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Parametro... params) {
            db.parametroDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim editar

}
