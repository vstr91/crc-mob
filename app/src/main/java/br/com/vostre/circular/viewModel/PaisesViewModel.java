package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.util.StringUtil;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.PaisDAO;
import br.com.vostre.circular.utils.StringUtils;

public class PaisesViewModel extends AndroidViewModel {

    PaisDAO paisDAO;

    public LiveData<List<Pais>> paises;
    public Pais pais;

    public LiveData<List<Pais>> getPaises() {
        return paises;
    }

    public void setPaises(LiveData<List<Pais>> paises) {
        this.paises = paises;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public PaisesViewModel(Application app){
        super(app);
        pais = new Pais();
        paises = new MutableLiveData<>();
    }

    public void salvarPais(){

        if(pais.valida(pais)){
            adicionarPais();
            System.out.println(pais.getNome()+" | "+pais.getSigla()+" | "+pais.getAtivo());
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    private void adicionarPais(){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {

                pais.setDataCadastro(new DateTime());
                pais.setUltimaAlteracao(new DateTime());
                pais.setEnviado(false);
                pais.setSlug(StringUtils.toSlug(pais.getNome()));

                paisDAO = AppDatabase.getAppDatabase(getApplication()).paisDAO();
                paisDAO.inserir(pais);
                return null;
            }

        }.execute();
    }

    public LiveData<List<Pais>> carregarPaises(){
        new AsyncTask<Void, Void, LiveData<List<Pais>>>(){

            @Override
            protected LiveData<List<Pais>> doInBackground(Void... voids) {
                paisDAO = AppDatabase.getAppDatabase(getApplication()).paisDAO();
                paises = paisDAO.listarTodos();
                return paises;
            }

        }.execute();

        return paises;

    }

}
