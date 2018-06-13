package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.EntidadeBase;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.PaisDAO;
import br.com.vostre.circular.utils.StringUtils;

public class BaseViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public BaseViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
    }

    public void salvar(List<? extends EntidadeBase> dados, String entidade){
        add(dados, entidade);
    }

    // adicionar

    public void add(final List<? extends EntidadeBase> entidadeBase, String entidade) {

        new addAsyncTask(appDatabase, entidade).execute(entidadeBase);
    }

    private static class addAsyncTask extends AsyncTask<List<? extends EntidadeBase>, Void, Void> {

        private AppDatabase db;
        private String entidade;

        addAsyncTask(AppDatabase appDatabase, String entidade) {
            db = appDatabase;
            this.entidade = entidade;
        }

        @Override
        protected Void doInBackground(final List<? extends EntidadeBase>... params) {

            switch(entidade){
                case "pais":
                    db.paisDAO().deletarTodos();
                    db.paisDAO().inserirTodos((List<Pais>) params[0]);
                    break;
                case "empresa":
                    break;
                case "estado":
                    break;
                case "cidade":
                    break;
                case "bairro":
                    break;
                case "parada":
                    break;
                case "itinerario":
                    break;
                case "horario":
                    break;
                case "parada_itinerario":
                    break;
                case "secao_itinerario":
                    break;
                case "horario_itinerario":
                    break;
                case "mensagem":
                    break;
                case "parametro":
                    break;
                case "ponto_interesse":
                    break;
                case "usuario":
                    break;
            }

            return null;
        }

    }

    // fim adicionar

}
