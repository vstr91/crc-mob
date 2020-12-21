package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ImagemParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.utils.StringUtils;

public class ImagensParadasViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<ImagemParadaBairro>> imagensParadas;
    public ImagemParada imagemParada;

    public static MutableLiveData<Integer> retorno;

    public ImagensParadasViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        imagemParada = new ImagemParada();
        imagensParadas = appDatabase.imagemParadaDAO().listarTodosPorStatus();

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void editar(){

        if(imagemParada.valida(imagemParada)){
            edit(imagemParada);
        } else{
            retorno.setValue(0);
        }

    }

    // editar

    public void edit(final ImagemParada imagemParada) {

        imagemParada.setUltimaAlteracao(new DateTime());
        imagemParada.setEnviado(false);

        new editAsyncTask(appDatabase).execute(imagemParada);
    }

    private static class editAsyncTask extends AsyncTask<ImagemParada, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ImagemParada... params) {
            db.imagemParadaDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ImagensParadasViewModel.retorno.setValue(1);
        }

    }

    // fim editar

    public void aceitaSugestao(final ImagemParada p){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                p.setStatus(1);
                p.setUltimaAlteracao(DateTime.now());
                p.setEnviado(false);

                if(p.valida(p)){

                    appDatabase.imagemParadaDAO().editar(p);

                } else{
//                    retorno.postValue(0);
                }

            }
        });


    }

    public void rejeitaSugestao(final ImagemParada p){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                p.setStatus(2);
                p.setEnviado(false);
                p.setUltimaAlteracao(DateTime.now());
                appDatabase.imagemParadaDAO().editar(p);
//                retorno.postValue(1);
            }
        });


    }

    public void reiniciarSugestao(final ImagemParada p){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                p.setStatus(0);
                p.setEnviado(false);
                p.setUltimaAlteracao(DateTime.now());
                appDatabase.imagemParadaDAO().editar(p);
//                retorno.postValue(1);
            }
        });


    }

}
