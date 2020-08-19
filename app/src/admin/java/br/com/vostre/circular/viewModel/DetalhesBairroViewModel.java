package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;

public class DetalhesBairroViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<ParadaBairro>> paradas;
    public ParadaBairro parada;

    public LiveData<BairroCidade> bairro;

    public LiveData<BairroCidade> getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = appDatabase.bairroDAO().carregar(bairro);
    }

    public DetalhesBairroViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        parada = new ParadaBairro();
        bairro = appDatabase.bairroDAO().carregar("");
        paradas = appDatabase.paradaDAO().listarTodosComBairroPorBairro("");
    }

    public void carregarParadas(String bairro){
        paradas = appDatabase.paradaDAO().listarTodosComBairroPorBairro(bairro);
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
