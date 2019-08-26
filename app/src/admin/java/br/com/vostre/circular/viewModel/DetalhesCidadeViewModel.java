package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;

public class DetalhesCidadeViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<BairroCidade>> bairros;
    public Bairro bairro;

    public LiveData<CidadeEstado> cidade;

    public LiveData<CidadeEstado> getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = appDatabase.cidadeDAO().carregar(cidade);
    }

    public DetalhesCidadeViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        bairro = new Bairro();
        cidade = appDatabase.cidadeDAO().carregar("");
        bairros = appDatabase.bairroDAO().listarTodosAtivosComCidadePorCidade("");
    }

    public void carregarBairros(String cidade){
        bairros = appDatabase.bairroDAO().listarTodosComCidadePorCidade(cidade);
    }

    public void salvarMensagem(){

//        if(mensagem.valida(mensagem)){
//            add(mensagem);
//        } else{
//            System.out.println("Faltou algo a ser digitado!");
//        }

    }

    public void editarMensagem(){

//        if(mensagem.valida(mensagem)){
//            edit(mensagem);
//        } else{
//            System.out.println("Faltou algo a ser digitado!");
//        }

    }

    // adicionar

    public void add(final Mensagem mensagem) {

        mensagem.setDataCadastro(new DateTime());
        mensagem.setUltimaAlteracao(new DateTime());
        mensagem.setEnviado(false);

        new addAsyncTask(appDatabase).execute(mensagem);
    }

    private static class addAsyncTask extends AsyncTask<Mensagem, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Mensagem... params) {
            db.mensagemDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    // editar

    public void edit(final Mensagem mensagem) {

        mensagem.setUltimaAlteracao(new DateTime());
        mensagem.setEnviado(false);

        new editAsyncTask(appDatabase).execute(mensagem);
    }

    private static class editAsyncTask extends AsyncTask<Mensagem, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Mensagem... params) {
            db.mensagemDAO().editar((params[0]));
            return null;
        }

    }

    // fim editar

}
