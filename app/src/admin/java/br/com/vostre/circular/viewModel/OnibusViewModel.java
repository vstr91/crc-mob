package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.OnibusDAO;
import br.com.vostre.circular.model.dao.PaisDAO;
import br.com.vostre.circular.utils.StringUtils;

public class OnibusViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<Onibus>> onibus;
    public Onibus umOnibus;

    public LiveData<List<Onibus>> getOnibus() {
        return onibus;
    }

    public void setOnibus(LiveData<List<Onibus>> onibus) {
        this.onibus = onibus;
    }

    public Onibus getUmOnibus() {
        return umOnibus;
    }

    public void setUmOnibus(Onibus onibus) {
        this.umOnibus = onibus;
    }

    public OnibusViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        umOnibus = new Onibus();
        onibus = appDatabase.onibusDAO().listarTodos();
    }

    public void salvarOnibus(){

        if(umOnibus.valida(umOnibus)){
            umOnibus.setPlaca(umOnibus.getPlaca().toUpperCase());
            add(umOnibus);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarOnibus(){

        if(umOnibus.valida(umOnibus)){
            umOnibus.setPlaca(umOnibus.getPlaca().toUpperCase());
            edit(umOnibus);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    // adicionar

    public void add(final Onibus onibus) {

        onibus.setDataCadastro(new DateTime());
        onibus.setUltimaAlteracao(new DateTime());
        onibus.setEnviado(false);

        new addAsyncTask(appDatabase).execute(onibus);
    }

    private static class addAsyncTask extends AsyncTask<Onibus, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Onibus... params) {
            db.onibusDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    // editar

    public void edit(final Onibus onibus) {

        onibus.setUltimaAlteracao(new DateTime());
        onibus.setEnviado(false);

        new editAsyncTask(appDatabase).execute(onibus);
    }

    private static class editAsyncTask extends AsyncTask<Onibus, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Onibus... params) {
            db.onibusDAO().editar((params[0]));
            return null;
        }

    }

    // fim editar

}
