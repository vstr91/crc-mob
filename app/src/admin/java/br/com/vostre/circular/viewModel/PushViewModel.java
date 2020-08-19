package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseBairro;
import br.com.vostre.circular.utils.StringUtils;

public class PushViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;
    public LiveData<ItinerarioPartidaDestino> itinerario;

    public LiveData<List<ParadaBairro>> paradas;
    public LiveData<ParadaBairro> parada;

    public LiveData<List<PontoInteresseBairro>> pois;
    public LiveData<PontoInteresseBairro> poi;

    public LiveData<List<ItinerarioPartidaDestino>> getItinerarios() {
        return itinerarios;
    }

    public void setItinerarios(LiveData<List<ItinerarioPartidaDestino>> itinerarios) {
        this.itinerarios = itinerarios;
    }

    public PushViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerarios = appDatabase.itinerarioDAO().listarTodosComTotalHorariosSimplificado();
        itinerario = appDatabase.itinerarioDAO().carregar("");

        paradas = appDatabase.paradaDAO().listarTodosAtivosComBairro();
        parada = appDatabase.paradaDAO().carregarComBairro("");

        pois = appDatabase.pontoInteresseDAO().listarTodosAtivosComBairro();
        poi = appDatabase.pontoInteresseDAO().carregarComBairro("");
    }

    public void carregarItinerario(String id){
        itinerario = appDatabase.itinerarioDAO().carregar(id);
    }

    public void carregarParada(String id){
        parada = appDatabase.paradaDAO().carregarComBairro(id);
    }

    public void carregarPontoInteresse(String id){
        poi = appDatabase.pontoInteresseDAO().carregarComBairro(id);
    }

}
