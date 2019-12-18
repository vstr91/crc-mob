package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.ViagemItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.utils.StringUtils;

public class ViagensItinerarioViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<ViagemItinerario>> viagens;
    public ViagemItinerario viagem;

    public static MutableLiveData<Integer> retorno;

    public void setItinerario(String itinerario){
        viagens = appDatabase.viagemItinerarioDAO().listarTodosPorItinerario(itinerario);
    }

    public ViagensItinerarioViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        viagem = new ViagemItinerario();
        viagens = appDatabase.viagemItinerarioDAO().listarTodosPorItinerario("-1");

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

}
