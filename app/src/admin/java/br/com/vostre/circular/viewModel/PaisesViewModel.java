package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.util.StringUtil;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.utils.StringUtils;

public class PaisesViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<Pais>> paises;
    public Pais pais;

    public static MutableLiveData<Integer> retorno;

    public LiveData<List<Pais>> getPaises() {
        return paises;
    }

    public void setPaises(LiveData<List<Pais>> paises) {
        this.paises = paises;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public PaisesViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        pais = new Pais();
        paises = appDatabase.paisDAO().listarTodos();

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void salvarPais(){

        if(pais.valida(pais)){
            pais.setSigla(pais.getSigla().toUpperCase());
            add(pais);
        } else{
            retorno.setValue(0);
        }

    }

    public void editarPais(){

        if(pais.valida(pais)){
            pais.setSigla(pais.getSigla().toUpperCase());
            edit(pais);
        } else{
            retorno.setValue(0);
        }

    }

    // adicionar

    public void add(final Pais pais) {

        pais.setDataCadastro(new DateTime());
        pais.setUltimaAlteracao(new DateTime());
        pais.setEnviado(false);
        pais.setSlug(StringUtils.toSlug(pais.getNome()));

        new addAsyncTask(appDatabase).execute(pais);
    }

    private static class addAsyncTask extends AsyncTask<Pais, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Pais... params) {
            db.paisDAO().inserir((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            PaisesViewModel.retorno.setValue(1);
        }

    }

    // fim adicionar

    // editar

    public void edit(final Pais pais) {

        pais.setUltimaAlteracao(new DateTime());
        pais.setEnviado(false);
        pais.setSlug(StringUtils.toSlug(pais.getNome()));

        new editAsyncTask(appDatabase).execute(pais);
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
            PaisesViewModel.retorno.setValue(1);
        }

    }

    // fim editar

}
