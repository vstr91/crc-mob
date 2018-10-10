package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.MensagemDAO;
import br.com.vostre.circular.model.dao.PaisDAO;
import br.com.vostre.circular.utils.StringUtils;

public class MensagensViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<Mensagem>> mensagens;
    public LiveData<List<Mensagem>> mensagensRecebidas;
    public Mensagem mensagem;

    public LiveData<List<Mensagem>> getMensagensRecebidas() {
        return mensagensRecebidas;
    }

    public void setMensagensRecebidas(LiveData<List<Mensagem>> mensagensRecebidas) {
        this.mensagensRecebidas = mensagensRecebidas;
    }

    public LiveData<List<Mensagem>> getMensagens() {
        return mensagens;
    }

    public void setMensagens(LiveData<List<Mensagem>> mensagens) {
        this.mensagens = mensagens;
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    public MensagensViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        mensagem = new Mensagem();
        mensagens = appDatabase.mensagemDAO().listarTodos();
        mensagensRecebidas = appDatabase.mensagemDAO().listarTodosRecebidos();
    }

    public void salvarMensagem(){

        mensagem.setServidor(false);

        if(mensagem.valida(mensagem)){
            add(mensagem);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarMensagem(){

        mensagem.setServidor(false);

        if(mensagem.valida(mensagem)){
            edit(mensagem);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

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

            if(params[0].getUsuarioCadastro() == null){
                String id = db.parametroInternoDAO().carregar().getIdentificadorUnico();
                params[0].setUsuarioCadastro(id);
                params[0].setUsuarioUltimaAlteracao(id);
            }

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
