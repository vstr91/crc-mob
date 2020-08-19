package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.utils.StringUtils;

public class BairrosViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<BairroCidade>> bairros;
    public BairroCidade bairro;

    public LiveData<List<CidadeEstado>> cidades;
    public CidadeEstado cidade;

    public static MutableLiveData<Integer> retorno;

    public LiveData<List<BairroCidade>> getBairros() {
        return bairros;
    }

    public void setBairros(LiveData<List<BairroCidade>> bairros) {
        this.bairros = bairros;
    }

    public BairroCidade getBairro() {
        return bairro;
    }

    public void setBairro(BairroCidade bairro) {
        this.bairro = bairro;
    }

    public BairrosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        bairro = new BairroCidade();
        bairros = appDatabase.bairroDAO().listarTodosComCidade();

        cidades = new MutableLiveData<>();
        cidades = appDatabase.cidadeDAO().listarTodosComEstado();

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);

    }

    public void salvarBairro(){

        bairro.getBairro().setCidade(cidade.getCidade().getId());

        if(bairro.getBairro().valida(bairro.getBairro())){
            add(bairro.getBairro());
        } else{
            retorno.setValue(0);
        }

    }

    public void editarBairro(){

        bairro.getBairro().setCidade(cidade.getCidade().getId());

        if(bairro.getBairro().valida(bairro.getBairro())){
            edit(bairro.getBairro());
        } else{
            retorno.setValue(0);
        }

    }

    // adicionar

    public void add(final Bairro bairro) {

        bairro.setDataCadastro(new DateTime());
        bairro.setUltimaAlteracao(new DateTime());
        bairro.setEnviado(false);
        bairro.setSlug(StringUtils.toSlug(bairro.getNome()));

        // se cidade relacionado estiver programado para data apos a programacao do bairro,
        // altera a data de programacao do bairro para ficar igual e evitar erros de
        // registro nao encontrado
        if((cidade.getCidade().getProgramadoPara() != null && bairro.getProgramadoPara() == null) ||
                (cidade.getCidade().getProgramadoPara() != null && bairro.getProgramadoPara() != null && cidade.getCidade().getProgramadoPara().isAfter(bairro.getProgramadoPara()))){
            bairro.setProgramadoPara(cidade.getCidade().getProgramadoPara());
        }

        new addAsyncTask(appDatabase).execute(bairro);
    }

    public static void addEstatico(final Bairro bairro, Context context) {

        if(appDatabase == null){
            appDatabase = AppDatabase.getAppDatabase(context);
        }

        bairro.setDataCadastro(new DateTime());
        bairro.setUltimaAlteracao(new DateTime());
        bairro.setEnviado(false);
        bairro.setSlug(StringUtils.toSlug(bairro.getNome()));

        new addAsyncTask(appDatabase).execute(bairro);
    }

    private static class addAsyncTask extends AsyncTask<Bairro, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Bairro... params) {
            db.bairroDAO().inserir((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(retorno != null){
                retorno.setValue(1);
            }

        }

    }

    // fim adicionar

    // editar

    public void edit(final Bairro bairro) {

        bairro.setUltimaAlteracao(new DateTime());
        bairro.setEnviado(false);
        bairro.setSlug(StringUtils.toSlug(bairro.getNome()));

        // se cidade relacionado estiver programado para data apos a programacao do bairro,
        // altera a data de programacao do bairro para ficar igual e evitar erros de
        // registro nao encontrado
        if((cidade.getCidade().getProgramadoPara() != null && bairro.getProgramadoPara() == null) ||
                (cidade.getCidade().getProgramadoPara() != null && bairro.getProgramadoPara() != null && cidade.getCidade().getProgramadoPara().isAfter(bairro.getProgramadoPara()))){
            bairro.setProgramadoPara(cidade.getCidade().getProgramadoPara());
        }

        new editAsyncTask(appDatabase).execute(bairro);
    }

    private static class editAsyncTask extends AsyncTask<Bairro, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Bairro... params) {
            db.bairroDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim editar

}
