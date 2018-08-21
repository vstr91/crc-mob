package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;

public class FavoritosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<ParadaBairro>> paradas;
    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;


    public FavoritosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        //paradas = appDatabase.fav.listarTodos();
        //mensagensRecebidas = appDatabase.mensagemDAO().listarTodosRecebidos();
    }

}
