package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;

public class DetalhesEmpresaViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;
    public Itinerario itinerario;

    public LiveData<List<Onibus>> onibus;

    public LiveData<Empresa> empresa;

    public Onibus umOnibus;

    public LiveData<Empresa> getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = appDatabase.empresaDAO().carregar(empresa);
    }

    public Onibus getUmOnibus() {
        return umOnibus;
    }

    public void setUmOnibus(Onibus onibus) {
        this.umOnibus = onibus;
    }

    public DetalhesEmpresaViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerario = new Itinerario();
        umOnibus = new Onibus();
        empresa = appDatabase.empresaDAO().carregar("");
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorEmpresa("");
        onibus = appDatabase.onibusDAO().listarTodosAtivos();
    }

    public void carregarItinerarios(String empresa){
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorEmpresa(empresa);
        onibus = appDatabase.onibusDAO().listarTodosAtivosPorEmpresa(empresa);
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

    // ONIBUS

    public void salvarOnibus(){

        umOnibus.setEmpresa(empresa.getValue().getId());

        if(umOnibus.valida(umOnibus)){
            umOnibus.setPlaca(umOnibus.getPlaca().toUpperCase());
            add(umOnibus);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarOnibus(){

        umOnibus.setEmpresa(empresa.getValue().getId());

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

        new addOnibusAsyncTask(appDatabase).execute(onibus);
    }

    private static class addOnibusAsyncTask extends AsyncTask<Onibus, Void, Void> {

        private AppDatabase db;

        addOnibusAsyncTask(AppDatabase appDatabase) {
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

        new editOnibusAsyncTask(appDatabase).execute(onibus);
    }

    private static class editOnibusAsyncTask extends AsyncTask<Onibus, Void, Void> {

        private AppDatabase db;

        editOnibusAsyncTask(AppDatabase appDatabase) {
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
