package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.databinding.BindingAdapter;
import android.os.AsyncTask;
import androidx.appcompat.widget.AppCompatSpinner;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.utils.StringUtils;

public class EstadosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<Estado>> estados;
    public Estado estado;

    public LiveData<List<Pais>> paises;
    public Pais pais;

    public static MutableLiveData<Integer> retorno;

    public LiveData<List<Estado>> getEstados() {
        return estados;
    }

    public void setEstados(LiveData<List<Estado>> estados) {
        this.estados = estados;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public EstadosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        estado = new Estado();
        estados = appDatabase.estadoDAO().listarTodos();

        paises = new MutableLiveData<>();
        paises = appDatabase.paisDAO().listarTodosAtivos();

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);

    }

    public void salvarEstado(){

        estado.setPais(pais.getId());

        if(estado.valida(estado)){
            estado.setSigla(estado.getSigla().toUpperCase());
            add(estado);
        } else{
            retorno.setValue(0);
        }

    }

    public void editarEstado(){

        estado.setPais(pais.getId());

        if(estado.valida(estado)){
            estado.setSigla(estado.getSigla().toUpperCase());
            edit(estado);
        } else{
            retorno.setValue(0);
        }

    }

    // adicionar

    public void add(final Estado estado) {

        estado.setDataCadastro(new DateTime());
        estado.setUltimaAlteracao(new DateTime());
        estado.setEnviado(false);
        estado.setSlug(StringUtils.toSlug(estado.getNome()));

        // se pais relacionado estiver programado para data apos a programacao do estado,
        // altera a data de programacao do estado para ficar igual e evitar erros de
        // registro nao encontrado
        if((pais.getProgramadoPara() != null && estado.getProgramadoPara() == null) ||
                (pais.getProgramadoPara() != null && estado.getProgramadoPara() != null && pais.getProgramadoPara().isAfter(estado.getProgramadoPara()))){
            estado.setProgramadoPara(pais.getProgramadoPara());
        }

        estado.setPais(pais.getId());

        new addAsyncTask(appDatabase).execute(estado);
    }

    private static class addAsyncTask extends AsyncTask<Estado, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Estado... params) {
            db.estadoDAO().inserir((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim adicionar

    // editar

    public void edit(final Estado estado) {

        estado.setUltimaAlteracao(new DateTime());
        estado.setEnviado(false);
        estado.setSlug(StringUtils.toSlug(estado.getNome()));

        // se pais relacionado estiver programado para data apos a programacao do estado,
        // altera a data de programacao do estado para ficar igual e evitar erros de
        // registro nao encontrado
        if((pais.getProgramadoPara() != null && estado.getProgramadoPara() == null) ||
                (pais.getProgramadoPara() != null && estado.getProgramadoPara() != null && pais.getProgramadoPara().isAfter(estado.getProgramadoPara()))){
            estado.setProgramadoPara(pais.getProgramadoPara());
        }

        new editAsyncTask(appDatabase).execute(estado);
    }

    private static class editAsyncTask extends AsyncTask<Estado, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Estado... params) {
            db.estadoDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim editar

}
