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
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.LinkedList;
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
import es.usc.citius.hipster.algorithm.Algorithm;
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

    public List<ItinerarioPartidaDestino> itinerarios;
    public MutableLiveData<List<ItinerarioPartidaDestino>> resultadosItinerarios;

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
        resultadosItinerarios = new MutableLiveData<>();
    }

    public void carregaResultado(String hora, String dia){

        final BairroCidade partida = bairroPartida.getValue();
        final BairroCidade destino = bairroDestino.getValue();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GraphBuilder<String, Double> builder = GraphBuilder.create();

                itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosSync();

                for(ItinerarioPartidaDestino i : itinerarios){
                    builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(i.getItinerario().getDistancia());
                }

                HipsterDirectedGraph<String,Double> graph = builder.createDirectedGraph();

                SearchProblem p = GraphSearchProblem
                        .startingFrom(partida.getBairro().getId())
                        .in(graph)
                        .takeCostsFromEdges()
                        .build();

                Algorithm.SearchResult result = Hipster.createDijkstra(p).search(destino.getBairro().getId());

                System.out.println("DIJ >>> "+result);

                List<List> caminhos = result.getOptimalPaths();

                for(List<List> caminho : caminhos){
                    int cont = caminho.size();
                    List<BairroCidade> passos = new ArrayList<>();

                    for(int i = 0; i < cont; i++){

                        BairroCidade bairro = appDatabase.bairroDAO().carregarSync(String.valueOf(caminho.get(i)));

                        passos.add(bairro);
                    }

                    BairroCidade bairroAnterior = null;
                    ItinerarioPartidaDestino itinerarioAnterior = null;
                    List<ItinerarioPartidaDestino> itinerarios = new ArrayList<>();

                    for(BairroCidade b : passos){

                        if(bairroAnterior != null){

                            String hora = "";

                            if(itinerarioAnterior != null){
                                String proximoHorario = itinerarioAnterior.getProximoHorario();
                                String tempo = DateTimeFormat.forPattern("HH:mm").print(itinerarioAnterior.getItinerario().getTempo().getMillis());

                                String soma = DateTimeFormat.forPattern("HH:mm").print(DateTime.parse(proximoHorario));

                                System.out.println("AAA");
                            } else{
                                hora = "00:00";//DateTimeFormat.forPattern("HH:mm:00").print(DateTime.now());
                            }

                            ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO().carregarPorPartidaEDestinoComHorarioSync(bairroAnterior.getBairro().getId(), b.getBairro().getId(), hora);
                            itinerarioAnterior = itinerario;

                            itinerarios.add(itinerario);
                            bairroAnterior = b;
                        } else{
                            bairroAnterior = b;
                        }

                    }

                    resultadosItinerarios.postValue(itinerarios);
                    System.out.println("ITI >>> "+itinerarios.size());

                }
            }
        });



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
