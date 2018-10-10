package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.MensagemResposta;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.MensagemDAO;
import br.com.vostre.circular.model.dao.MensagemRespostaDAO;

public class DetalheMensagensViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<MensagemResposta>> respostas;
    public Mensagem mensagem;
    public MensagemResposta resposta;

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    public MensagemResposta getResposta() {
        return resposta;
    }

    public void setResposta(MensagemResposta resposta) {
        this.resposta = resposta;
    }

    public DetalheMensagensViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        mensagem = new Mensagem();
        resposta = new MensagemResposta();
        respostas = appDatabase.mensagemRespostaDAO().carregarTodosPorIdMensagem(mensagem.getId());
    }

    public void carregarRespostas(Mensagem mensagem){
        respostas = appDatabase.mensagemRespostaDAO().carregarTodosPorIdMensagem(mensagem.getId());
    }

    public void marcarComoLida(Mensagem mensagem){
        mensagem.setLida(true);
        mensagem.setUltimaAlteracao(DateTime.now());
        new marcarLidaAsyncTask(appDatabase).execute(mensagem);
    }

    public void salvarResposta(){

        if(resposta.valida(resposta)){
            add(resposta);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    // adicionar

    public void add(final MensagemResposta resposta) {

        resposta.setDataCadastro(new DateTime());
        resposta.setUltimaAlteracao(new DateTime());
        resposta.setEnviado(false);

        new addAsyncTask(appDatabase).execute(resposta);
    }

    private static class addAsyncTask extends AsyncTask<MensagemResposta, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final MensagemResposta... params) {
            db.mensagemRespostaDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    private static class marcarLidaAsyncTask extends AsyncTask<Mensagem, Void, Void> {

        private AppDatabase db;

        marcarLidaAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Mensagem... params) {
            db.mensagemDAO().editar((params[0]));
            return null;
        }

    }

}
