package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Problema;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ProblemaTipo;
import br.com.vostre.circular.utils.StringUtils;

public class ProblemasViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<ProblemaTipo>> abertos;
    public LiveData<List<ProblemaTipo>> resolvidos;
    public Problema problema;

    public static MutableLiveData<Integer> retorno;

    public Bitmap fotoProblema;

    public Problema getProblema() {
        return problema;
    }

    public void setProblema(Problema problema) {
        this.problema = problema;
    }

    public ProblemasViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        problema = new Problema();
        abertos = appDatabase.problemaDAO().listarTodosAbertos();
        resolvidos = appDatabase.problemaDAO().listarTodosResolvidos();

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void editarProblema(){

        if(problema.valida(problema)){
            edit(problema);
        } else{
            retorno.setValue(0);
        }

    }

    // editar

    public void edit(final Problema problema) {

        problema.setUltimaAlteracao(new DateTime());
        problema.setEnviado(false);

        if(fotoProblema != null){
            salvarFotoProblema();
        }

        new editAsyncTask(appDatabase).execute(problema);
    }

    private static class editAsyncTask extends AsyncTask<Problema, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Problema... params) {
            db.problemaDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim editar

    // editar

    public static void editProblema(final Problema problema, Context context) {

        if(appDatabase == null){
            appDatabase = AppDatabase.getAppDatabase(context.getApplicationContext());
        }

        new editProblemaAsyncTask(appDatabase).execute(problema);
    }

    public void editProblema(final Problema problema) {

        problema.setUltimaAlteracao(new DateTime());
        problema.setEnviado(false);

        if(fotoProblema != null){
            salvarFotoProblema();
        }

        new editProblemaAsyncTask(appDatabase).execute(problema);
    }

    private static class editProblemaAsyncTask extends AsyncTask<Problema, Void, Void> {

        private AppDatabase db;

        editProblemaAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Problema... params) {
            db.problemaDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim editar

    private void salvarFotoProblema() {
        FileOutputStream fos = null;
        File file = new File(getApplication().getFilesDir(),  UUID.randomUUID().toString()+".png");

        try {
            fos = new FileOutputStream(file);
            fotoProblema.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();

                    if(problema.getImagem() != null && !problema.getImagem().isEmpty()){
                        File fotoAntiga = new File(getApplication().getFilesDir(), problema.getImagem());

                        if(fotoAntiga.exists() && fotoAntiga.canWrite() && fotoAntiga.getName() != file.getName()){
                            fotoAntiga.delete();
                        }
                    }

                    problema.setImagem(file.getName());
                    problema.setImagemEnviada(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
