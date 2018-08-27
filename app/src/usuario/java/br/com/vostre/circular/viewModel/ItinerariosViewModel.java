package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
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
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterDirectedGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;

public class ItinerariosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<HorarioItinerarioNome> itinerario;
    public LiveData<HorarioItinerarioNome> horarioAnterior;
    public LiveData<HorarioItinerarioNome> horarioSeguinte;

    public LiveData<List<CidadeEstado>> cidades;
    public LiveData<List<CidadeEstado>> cidadesDestino;
    public CidadeEstado cidadePartida;
    public CidadeEstado cidadeDestino;

    public LiveData<List<BairroCidade>> bairros;
    public LiveData<BairroCidade> bairroPartida;
    public LiveData<BairroCidade> bairroDestino;

    public LiveData<ItinerarioPartidaDestino> itinerarioResultado;

    public int escolhaAtual = 0; // 0 partida - 1 destino

    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;

    public CidadeEstado getCidadePartida() {
        return cidadePartida;
    }

    public void setCidadePartida(CidadeEstado cidadePartida) {
        this.cidadePartida = cidadePartida;
        bairros = appDatabase.bairroDAO().listarTodosComCidadePorCidade(cidadePartida.getCidade().getId());
    }

    public CidadeEstado getCidadeDestino() {
        return cidadeDestino;
    }

    public void setCidadeDestino(CidadeEstado cidadeDestino) {
        this.cidadeDestino = cidadeDestino;
        bairros = appDatabase.bairroDAO().listarTodosComCidadePorCidade(cidadeDestino.getCidade().getId());
    }

    public LiveData<BairroCidade> getBairroDestino() {
        return bairroDestino;
    }

    public void setBairroDestino(BairroCidade bairroDestino) {
        this.bairroDestino = appDatabase.bairroDAO().carregar(bairroDestino.getBairro().getId());
    }

    public LiveData<BairroCidade> getBairroPartida() {
        return bairroPartida;
    }

    public void setBairroPartida(BairroCidade umBairroPartida) {
        this.bairroPartida = appDatabase.bairroDAO().carregar(umBairroPartida.getBairro().getId());
        //this.cidadesDestino = appDatabase.cidadeDAO().listarTodosAtivasComEstado();//appDatabase.itinerarioDAO().carregarDestinosPorPartida(umBairroPartida.getBairro().getId());
    }

    public LiveData<HorarioItinerarioNome> getItinerario() {
        return itinerario;
    }

    public void setItinerario(LiveData<HorarioItinerarioNome> itinerario) {
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
        itinerario = appDatabase.horarioItinerarioDAO()
                .carregarProximoPorPartidaEDestino("", "", "domingo");
        cidades = appDatabase.cidadeDAO().listarTodosAtivasComEstado();
        cidadesDestino = appDatabase.cidadeDAO().listarTodosAtivasComEstado();//appDatabase.itinerarioDAO().carregarDestinosPorPartida("");
        bairroPartida = appDatabase.bairroDAO().carregar(null);
        bairroDestino = appDatabase.bairroDAO().carregar(null);
        bairros = appDatabase.bairroDAO().listarTodosComCidadePorCidade(null);
        itinerarioResultado = appDatabase.itinerarioDAO().carregar("");
        this.itinerarios = appDatabase.itinerarioDAO().listarTodosAtivos();
    }

    public void carregaResultado(String hora, String dia){

        BairroCidade partida = bairroPartida.getValue();
        BairroCidade destino = bairroDestino.getValue();

        GraphBuilder<String, Double> builder = GraphBuilder.create();

        List<ItinerarioPartidaDestino> iti = itinerarios.getValue();

        for(ItinerarioPartidaDestino i : iti){
            builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(i.getItinerario().getDistancia());
        }

        HipsterDirectedGraph<String,Double> graph = builder.createDirectedGraph();

// Create the search problem. For graph problems, just use
// the GraphSearchProblem util class to generate the problem with ease.
        SearchProblem p = GraphSearchProblem
                .startingFrom(partida.getBairro().getId())
                .in(graph)
                .takeCostsFromEdges()
                .build();

// Search the shortest path from "A" to "F"
        System.out.println("DIJ >>> "+Hipster.createDijkstra(p).search(destino.getBairro().getId()));

        itinerario = appDatabase.horarioItinerarioDAO()
                .carregarProximoPorPartidaEDestino(partida.getBairro().getId(),
                        destino.getBairro().getId(), "00:00:00");
    }

    public void carregaResultadoDiaSeguinte(String dia){
        itinerario = appDatabase.horarioItinerarioDAO()
                .carregarPrimeiroPorPartidaEDestino(bairroPartida.getValue().getBairro().getId(),
                        bairroDestino.getValue().getBairro().getId(), "00:00");
    }

    public void carregaItinerarioResultado(){
        itinerarioResultado = appDatabase.itinerarioDAO()
                .carregar(itinerario.getValue().getHorarioItinerario().getItinerario());
    }

    public void carregaHorarios(HorarioItinerarioNome horario){
        horarioAnterior = appDatabase.horarioItinerarioDAO()
                .carregarAnteriorPorPartidaEDestino(bairroPartida.getValue().getBairro().getId(),
                        bairroDestino.getValue().getBairro().getId(),
                        DateTimeFormat.forPattern("HH:mm:00").print(horario.getNomeHorario()));

        horarioSeguinte = appDatabase.horarioItinerarioDAO()
                .carregarSeguintePorPartidaEDestino(bairroPartida.getValue().getBairro().getId(),
                        bairroDestino.getValue().getBairro().getId(),
                        DateTimeFormat.forPattern("HH:mm:00").print(horario.getNomeHorario()));
    }

}
