package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.util.StringUtil;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.PaisDAO;
import br.com.vostre.circular.utils.StringUtils;

public class PaisesViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    PaisDAO paisDAO;

    public LiveData<List<Pais>> paises;
    public Pais pais;

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
    }

    public void salvarPais(){

        if(pais.valida(pais)){
            add(pais);
            System.out.println(pais.getNome()+" | "+pais.getSigla()+" | "+pais.getAtivo());
        } else{
            System.out.println("Faltou algo a ser digitado!");
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

    }

    // fim adicionar

}
