package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.BindingAdapter;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.Spinner;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.EstadoDAO;
import br.com.vostre.circular.model.dao.PaisDAO;
import br.com.vostre.circular.utils.StringUtils;
import br.com.vostre.circular.view.adapter.PaisAdapter;

public class EstadosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    EstadoDAO estadoDAO;

    public LiveData<List<Estado>> estados;
    public Estado estado;

    public LiveData<List<Pais>> paises;

    int paisEscolhido;

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
        paises = appDatabase.paisDAO().listarTodos();
    }

    public void salvarEstado(){

        if(estado.valida(estado)){
            add(estado);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarEstado(){

        if(estado.valida(estado)){
            edit(estado);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    @BindingAdapter({"bind:items"})
    public static void entries(Spinner spinner, PaisAdapter adapter) {
        spinner.setAdapter(adapter);
    }

    // adicionar

    public void add(final Estado estado) {

        estado.setDataCadastro(new DateTime());
        estado.setUltimaAlteracao(new DateTime());
        estado.setEnviado(false);
        estado.setSlug(StringUtils.toSlug(estado.getNome()));

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

    }

    // fim adicionar

    // editar

    public void edit(final Estado estado) {

        estado.setUltimaAlteracao(new DateTime());
        estado.setEnviado(false);
        estado.setSlug(StringUtils.toSlug(estado.getNome()));

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

    }

    // fim editar

}
