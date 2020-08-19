package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;

public class SecoesItinerarioViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<SecaoItinerario>> secoes;
    public SecaoItinerario secao;

    Itinerario itinerario;

    public LiveData<List<ParadaBairro>> paradasIniciais;
    public LiveData<List<ParadaBairro>> paradasFinais;

    public static MutableLiveData<Integer> retorno;

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

        paradasIniciais = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro(itinerario.getId());
    }

    public void setParadaInicial(ParadaBairro paradaInicial) {
        this.secao.setParadaInicial(paradaInicial.getParada().getId());
        paradasFinais = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairroSemParadaInicial(itinerario.getId(), paradaInicial.getParada().getId());
    }

    public void setParadaFinal(ParadaBairro paradaFinal) {
        this.secao.setParadaFinal(paradaFinal.getParada().getId());
    }

    public SecoesItinerarioViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        secao = new SecaoItinerario();
        secoes = appDatabase.secaoItinerarioDAO().listarTodosPorItinerario("");

        paradasIniciais = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro("");
        paradasFinais = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro("");

        retorno = new MutableLiveData<>();
    }

    public void salvarSecao(){

        secao.setItinerario(itinerario.getId());

        if(secao.valida(secao)){
            add(secao);
        } else{
            retorno.setValue(0);
        }

    }

    public void editarSecao(){

        if(secao.valida(secao)){
            secao.setItinerario(itinerario.getId());
            edit(secao);
        } else{
            retorno.setValue(0);
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
        private boolean valido = false;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final SecaoItinerario... params) {

            SecaoItinerario secao = params[0];

            ParadaItinerarioBairro paradaInicial = db.paradaItinerarioDAO().carregar(secao.getParadaInicial(), secao.getItinerario());
            ParadaItinerarioBairro paradaFinal = db.paradaItinerarioDAO().carregar(secao.getParadaFinal(), secao.getItinerario());

            if(paradaInicial.getParadaItinerario().getOrdem() < paradaFinal.getParadaItinerario().getOrdem()){
                db.secaoItinerarioDAO().inserir((params[0]));
                valido = true;
            } else{
                valido = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(valido){
                retorno.setValue(1);
            } else{
                retorno.setValue(2);
            }

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
        private boolean valido = false;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final SecaoItinerario... params) {

            SecaoItinerario secao = params[0];

            ParadaItinerarioBairro paradaInicial = db.paradaItinerarioDAO().carregar(secao.getParadaInicial(), secao.getItinerario());
            ParadaItinerarioBairro paradaFinal = db.paradaItinerarioDAO().carregar(secao.getParadaFinal(), secao.getItinerario());

            if(paradaInicial.getParadaItinerario().getOrdem() < paradaFinal.getParadaItinerario().getOrdem()){
                db.secaoItinerarioDAO().editar((params[0]));
                valido = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(valido){
                retorno.setValue(1);
            } else{
                retorno.setValue(2);
            }

        }

    }

    // fim editar

}
