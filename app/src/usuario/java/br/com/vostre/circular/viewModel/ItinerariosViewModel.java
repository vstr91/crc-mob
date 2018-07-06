package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;

public class ItinerariosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<Itinerario>> itinerarios;
    public Itinerario itinerario;

    public LiveData<List<CidadeEstado>> cidades;
    public CidadeEstado cidadePartida;

    public LiveData<List<BairroCidade>> bairros;
    public BairroCidade bairroPartida;

    public CidadeEstado getCidadePartida() {
        return cidadePartida;
    }

    public void setCidadePartida(CidadeEstado cidadePartida) {
        this.cidadePartida = cidadePartida;
        bairros = appDatabase.bairroDAO().listarTodosComCidadePorCidade(cidadePartida.getCidade().getId());
    }

    public BairroCidade getBairroPartida() {
        return bairroPartida;
    }

    public void setBairroPartida(BairroCidade bairroPartida) {
        this.bairroPartida = bairroPartida;
    }

    public LiveData<List<Itinerario>> getItinerarios() {
        return itinerarios;
    }

    public void setItinerarios(LiveData<List<Itinerario>> itinerarios) {
        this.itinerarios = itinerarios;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public LiveData<List<CidadeEstado>> getCidades() {
        return cidades;
    }

    public void setCidades(LiveData<List<CidadeEstado>> cidades) {
        this.cidades = cidades;
    }

    public ItinerariosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerario = new Itinerario();
        itinerarios = appDatabase.itinerarioDAO().listarTodos();
        cidades = appDatabase.cidadeDAO().listarTodosAtivasComEstado();
        bairroPartida = new BairroCidade();
        bairros = appDatabase.bairroDAO().listarTodosComCidadePorCidade(null);
    }

}
