package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.PaisDAO;
import br.com.vostre.circular.model.dao.SecaoItinerarioDAO;
import br.com.vostre.circular.utils.StringUtils;

public class SecoesItinerarioViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<SecaoItinerario>> secoes;
    public SecaoItinerario secao;

    Itinerario itinerario;

    public LiveData<List<SecaoItinerario>> getSecoes() {
        return secoes;
    }

    public void setSecoes(LiveData<List<SecaoItinerario>> secoes) {
        this.secoes = secoes;
    }

    public SecaoItinerario getSecao() {
        return secao;
    }

    public void setSecao(SecaoItinerario secao) {
        this.secao = secao;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
        secoes = appDatabase.secaoItinerarioDAO().listarTodosPorItinerario(itinerario.getId());
    }

    public SecoesItinerarioViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        secao = new SecaoItinerario();
        secoes = appDatabase.secaoItinerarioDAO().listarTodosPorItinerario("");
    }

    public void salvarSecao(){

        secao.setItinerario(itinerario.getId());

        if(secao.valida(secao)){
            add(secao);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarSecao(){

        if(secao.valida(secao)){
            secao.setItinerario(itinerario.getId());
            edit(secao);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    // adicionar

    public void add(final SecaoItinerario secao) {

        secao.setDataCadastro(new DateTime());
        secao.setUltimaAlteracao(new DateTime());
        secao.setEnviado(false);

        new addAsyncTask(appDatabase).execute(secao);
    }

    private static class addAsyncTask extends AsyncTask<SecaoItinerario, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final SecaoItinerario... params) {
            db.secaoItinerarioDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    // editar

    public void edit(final SecaoItinerario secao) {

        secao.setUltimaAlteracao(new DateTime());
        secao.setEnviado(false);

        new editAsyncTask(appDatabase).execute(secao);
    }

    private static class editAsyncTask extends AsyncTask<SecaoItinerario, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final SecaoItinerario... params) {
            db.secaoItinerarioDAO().editar((params[0]));
            return null;
        }

    }

    // fim editar

}
