package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

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
import br.com.vostre.circular.utils.DataHoraUtils;
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
    public LiveData<List<BairroCidade>> bairrosDestino;
    public LiveData<BairroCidade> bairroPartida;
    public LiveData<BairroCidade> bairroDestino;

    public boolean partidaEscolhida = false;
    public boolean destinoEscolhido = false;

    BairroCidade myPartida = null;
    BairroCidade myDestino = null;

    public LiveData<ItinerarioPartidaDestino> itinerarioResultado;

    public int escolhaAtual = 0; // 0 partida - 1 destino

    public List<ItinerarioPartidaDestino> itinerarios;
    public MutableLiveData<List<ItinerarioPartidaDestino>> resultadosItinerarios;

    public CidadeEstado getCidadePartida() {
        return cidadePartida;
    }

    public void setCidadePartida(CidadeEstado cidadePartida) {
        this.cidadePartida = cidadePartida;
        bairros = appDatabase.bairroDAO().listarTodosAtivosComCidadePorCidade(cidadePartida.getCidade().getId());
    }

    public CidadeEstado getCidadeDestino() {
        return cidadeDestino;
    }

    public void setCidadeDestino(CidadeEstado cidadeDestino) {
        this.cidadeDestino = cidadeDestino;
        bairrosDestino = appDatabase.bairroDAO().listarTodosAtivosComCidadePorCidadeFiltro(cidadeDestino.getCidade().getId(), bairroPartida.getValue().getBairro().getId());
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
        this.cidadesDestino = appDatabase.cidadeDAO().listarTodosAtivasComEstadoFiltro(umBairroPartida.getBairro().getId());
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
        bairros = appDatabase.bairroDAO().listarTodosAtivosComCidadePorCidade(null);
        bairrosDestino = appDatabase.bairroDAO().listarTodosAtivosComCidadePorCidade(null);
        itinerarioResultado = appDatabase.itinerarioDAO().carregar("");
        resultadosItinerarios = new MutableLiveData<>();
    }

    public void carregaResultado(final String horaEscolhida, final String dia, final String diaSeguinte, final String diaAnterior){

        myPartida = bairroPartida.getValue();
        myDestino = bairroDestino.getValue();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GraphBuilder<String, Double> builder = GraphBuilder.create();

                itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosTesteSync();

                for(ItinerarioPartidaDestino i : itinerarios){
                    builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(i.getItinerario().getDistancia());
                }

                HipsterDirectedGraph<String,Double> graph = builder.createDirectedGraph();

                SearchProblem p = GraphSearchProblem
                        .startingFrom(myPartida.getBairro().getId())
                        .in(graph)
                        .takeCostsFromEdges()
                        .build();

                Algorithm.SearchResult result = Hipster.createDijkstra(p).search(myDestino.getBairro().getId());

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

                            PeriodFormatter parser =
                                    new PeriodFormatterBuilder()
                                            .appendHours().appendLiteral(":")
                                            .appendMinutes().toFormatter();

                            PeriodFormatter printer =
                                    new PeriodFormatterBuilder()
                                            .printZeroAlways().minimumPrintedDigits(2)
                                            //.appendDays().appendLiteral(" dia(s) ")
                                            .appendHours().appendLiteral(":")
                                            .appendMinutes().toFormatter();

                            Period period = Period.ZERO;

                            if(itinerarioAnterior != null){
                                String proximoHorario = itinerarioAnterior.getProximoHorario();
                                String tempo = DateTimeFormat.forPattern("HH:mm").print(itinerarioAnterior.getItinerario().getTempo().getMillis());

                                period = period.plus(parser.parsePeriod(proximoHorario));
                                period = period.plus(parser.parsePeriod(tempo));

                                hora = printer.print(period);

                            } else{
                                hora = horaEscolhida;//"00:00";//DateTimeFormat.forPattern("HH:mm:00").print(DateTime.now());
                            }

                            SimpleSQLiteQuery queryIti = new SimpleSQLiteQuery("SELECT i.id FROM itinerario i INNER JOIN " +
                                    "parada_itinerario pi ON pi.itinerario = i.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro bp ON bp.id = pp.bairro INNER JOIN parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN " +
                                    "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                                    "WHERE i.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem " +
                                    "AND bp.id = '" + bairroAnterior.getBairro().getId() + "' AND bd.id = '" + b.getBairro().getId() + "' ORDER BY i.id");

                            List<String> itis = appDatabase.itinerarioDAO()
                                    .carregarOpcoesPorPartidaEDestinoSync(queryIti);

                            String itinerariosDisponiveis = TextUtils.join("','", itis);

                            SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT i.*, e.nome AS 'nomeEmpresa', " +
                                    "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) as 'proximoHorario', " +
                                    "h.id as 'idProximoHorario', IFNULL((SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                                    "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "ORDER BY h2.nome DESC LIMIT 1) , " +
                                    "( SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'horarioAnterior', " +
                                    "IFNULL( (SELECT h2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                                    "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                                    "( SELECT h2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'idHorarioAnterior', " +
                                    "IFNULL( (SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                                    "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "ORDER BY h2.nome LIMIT 1 ) , " +
                                    "( SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + diaSeguinte + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "ORDER BY h2.nome LIMIT 1 ) ) AS horarioSeguinte, " +
                                    "IFNULL( (SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                                    "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "ORDER BY h2.nome LIMIT 1 ) , " +
                                    "( SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + diaSeguinte + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "ORDER BY h2.nome LIMIT 1 ) ) AS idHorarioSeguinte, " +
                                    "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                    "AND pi.itinerario = i.id ) AS 'idPartida', " +
                                    "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                    "AND pi.itinerario = i.id ) AS 'nomePartida', " +
                                    "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                    "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'nomeDestino', " +
                                    "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroPartida', " +
                                    "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroDestino', " +
                                    "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroPartida', " +
                                    "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroDestino', " +
                                    "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'cidadePartida', " +
                                    "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                                    "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                    "AND pi.itinerario = i.id ) AS 'cidadeDestino' " +
                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario INNER JOIN " +
                                    "itinerario i ON i.id = hi.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
                                    "WHERE hi.itinerario IN ('" + itinerariosDisponiveis + "') AND " + dia + " = 1 " +
                                    "AND strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) > '" + hora + "' ORDER BY proximoHorario LIMIT 1");

                            ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO()
                                    .carregarPorPartidaEDestinoComHorarioSync(query);

                            if(itinerario == null){

                                String diaAnt = dia;
                                String diaAt = diaSeguinte;
                                String diaSeg = DataHoraUtils.getDiaSeguinte(diaSeguinte);

                                query = new SimpleSQLiteQuery("SELECT i.*, e.nome AS 'nomeEmpresa', " +
                                        "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) as 'proximoHorario', " +
                                        "h.id as 'idProximoHorario', IFNULL((SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                        "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                                        "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                        "ORDER BY h2.nome DESC LIMIT 1) , " +
                                        "( SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                        "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAnt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'horarioAnterior', " +
                                        "IFNULL( (SELECT h2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                                        "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                        "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                                        "( SELECT h2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAnt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'idHorarioAnterior', " +
                                        "IFNULL( (SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                        "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                                        "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                        "ORDER BY h2.nome LIMIT 1 ) , " +
                                        "( SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                        "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaSeg + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "ORDER BY h2.nome LIMIT 1 ) ) AS horarioSeguinte, " +
                                        "IFNULL( (SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                                        "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                        "ORDER BY h2.nome LIMIT 1 ) , " +
                                        "( SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaSeg + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "ORDER BY h2.nome LIMIT 1 ) ) AS idHorarioSeguinte, " +
                                        "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                        "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                        "AND pi.itinerario = i.id ) AS 'idPartida', " +
                                        "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                        "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                        "AND pi.itinerario = i.id ) AS 'nomePartida', " +
                                        "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                        "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'nomeDestino', " +
                                        "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroPartida', " +
                                        "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroDestino', " +
                                        "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroPartida', " +
                                        "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroDestino', " +
                                        "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                                        "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'cidadePartida', " +
                                        "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                                        "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                        "AND pi.itinerario = i.id ) AS 'cidadeDestino' " +
                                        "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario INNER JOIN " +
                                        "itinerario i ON i.id = hi.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
                                        "WHERE hi.itinerario IN ('" + itinerariosDisponiveis + "') AND " + diaAt + " = 1 ORDER BY proximoHorario LIMIT 1");

                                itinerario = appDatabase.itinerarioDAO()
                                        .carregarPorPartidaEDestinoComHorarioSync(query);
                            }

                            if(itinerario.getProximoHorario() != null){

                                if(itinerario.getIdHorarioAnterior() != null){
                                    String obsHorarioAnterior = appDatabase.horarioItinerarioDAO()
                                            .carregarObservacaoPorHorario(itinerario.getIdHorarioAnterior(), itinerario.getItinerario().getId());
                                    itinerario.setObservacaoHorarioAnterior(obsHorarioAnterior);
                                }

                                if(itinerario.getIdHorarioSeguinte() != null){
                                    String obsHorarioSeguinte = appDatabase.horarioItinerarioDAO()
                                            .carregarObservacaoPorHorario(itinerario.getIdHorarioSeguinte(), itinerario.getItinerario().getId());
                                    itinerario.setObservacaoHorarioSeguinte(obsHorarioSeguinte);
                                }

                                if(itinerario.getIdProximoHorario() != null){
                                    String obsProximoHorario = appDatabase.horarioItinerarioDAO()
                                            .carregarObservacaoPorHorario(itinerario.getIdProximoHorario(), itinerario.getItinerario().getId());
                                    itinerario.setObservacaoProximoHorario(obsProximoHorario);
                                }

                                itinerarioAnterior = itinerario;

                                itinerarios.add(itinerario);
                                bairroAnterior = b;
                            }


                        } else{
                            bairroAnterior = b;
                        }

                    }

                    resultadosItinerarios.postValue(itinerarios);

                }
            }
        });



        itinerario = appDatabase.horarioItinerarioDAO()
                .carregarProximoPorPartidaEDestino(myPartida.getBairro().getId(),
                        myDestino.getBairro().getId(), horaEscolhida);
    }

    public void carregaResultadoInvertido(final String horaEscolhida, final String dia, final String diaSeguinte, final String diaAnterior){

        final BairroCidade bairro = myPartida;
        myPartida = myDestino;
        myDestino = bairro;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GraphBuilder<String, Double> builder = GraphBuilder.create();

                itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosTesteSync();

                for(ItinerarioPartidaDestino i : itinerarios){
                    builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(i.getItinerario().getDistancia());
                }

                HipsterDirectedGraph<String,Double> graph = builder.createDirectedGraph();

                SearchProblem p = GraphSearchProblem
                        .startingFrom(myPartida.getBairro().getId())
                        .in(graph)
                        .takeCostsFromEdges()
                        .build();

                Algorithm.SearchResult result = Hipster.createDijkstra(p).search(myDestino.getBairro().getId());

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

                            PeriodFormatter parser =
                                    new PeriodFormatterBuilder()
                                            .appendHours().appendLiteral(":")
                                            .appendMinutes().toFormatter();

                            PeriodFormatter printer =
                                    new PeriodFormatterBuilder()
                                            .printZeroAlways().minimumPrintedDigits(2)
                                            //.appendDays().appendLiteral(" dia(s) ")
                                            .appendHours().appendLiteral(":")
                                            .appendMinutes().toFormatter();

                            Period period = Period.ZERO;

                            if(itinerarioAnterior != null){
                                String proximoHorario = itinerarioAnterior.getProximoHorario();
                                String tempo = DateTimeFormat.forPattern("HH:mm").print(itinerarioAnterior.getItinerario().getTempo().getMillis());

                                period = period.plus(parser.parsePeriod(proximoHorario));
                                period = period.plus(parser.parsePeriod(tempo));

                                hora = printer.print(period);

                            } else{
                                hora = horaEscolhida;//"00:00";//DateTimeFormat.forPattern("HH:mm:00").print(DateTime.now());
                            }

                            SimpleSQLiteQuery queryIti = new SimpleSQLiteQuery("SELECT i.id FROM itinerario i INNER JOIN " +
                                    "parada_itinerario pi ON pi.itinerario = i.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro bp ON bp.id = pp.bairro INNER JOIN parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN " +
                                    "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                                    "WHERE i.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem " +
                                    "AND bp.id = '" + bairroAnterior.getBairro().getId() + "' AND bd.id = '" + b.getBairro().getId() + "' ORDER BY i.id");

                            List<String> itis = appDatabase.itinerarioDAO()
                                    .carregarOpcoesPorPartidaEDestinoSync(queryIti);

                            String itinerariosDisponiveis = TextUtils.join("','", itis);

                            SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT i.*, e.nome AS 'nomeEmpresa', " +
                                    "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) as 'proximoHorario', " +
                                    "h.id as 'idProximoHorario', IFNULL((SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                                    "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "ORDER BY h2.nome DESC LIMIT 1) , " +
                                    "( SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'horarioAnterior', " +
                                    "IFNULL( (SELECT h2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                                    "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                                    "( SELECT h2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'idHorarioAnterior', " +
                                    "IFNULL( (SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                                    "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "ORDER BY h2.nome LIMIT 1 ) , " +
                                    "( SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + diaSeguinte + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "ORDER BY h2.nome LIMIT 1 ) ) AS horarioSeguinte, " +
                                    "IFNULL( (SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                                    "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "ORDER BY h2.nome LIMIT 1 ) , " +
                                    "( SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                    "WHERE hi2." + diaSeguinte + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                    "ORDER BY h2.nome LIMIT 1 ) ) AS idHorarioSeguinte, " +
                                    "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                    "AND pi.itinerario = i.id ) AS 'idPartida', " +
                                    "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                    "AND pi.itinerario = i.id ) AS 'nomePartida', " +
                                    "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                    "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'nomeDestino', " +
                                    "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroPartida', " +
                                    "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroDestino', " +
                                    "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroPartida', " +
                                    "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroDestino', " +
                                    "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'cidadePartida', " +
                                    "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                                    "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                    "AND pi.itinerario = i.id ) AS 'cidadeDestino' " +
                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario INNER JOIN " +
                                    "itinerario i ON i.id = hi.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
                                    "WHERE hi.itinerario IN ('" + itinerariosDisponiveis + "') AND " + dia + " = 1 " +
                                    "AND strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) > '" + hora + "' ORDER BY proximoHorario LIMIT 1");

                            ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO()
                                    .carregarPorPartidaEDestinoComHorarioSync(query);

                            if(itinerario == null){

                                String diaAnt = dia;
                                String diaAt = diaSeguinte;
                                String diaSeg = DataHoraUtils.getDiaSeguinte(diaSeguinte);

                                query = new SimpleSQLiteQuery("SELECT i.*, e.nome AS 'nomeEmpresa', " +
                                        "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) as 'proximoHorario', " +
                                        "h.id as 'idProximoHorario', IFNULL((SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                        "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                                        "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                        "ORDER BY h2.nome DESC LIMIT 1) , " +
                                        "( SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                        "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAnt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'horarioAnterior', " +
                                        "IFNULL( (SELECT h2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                                        "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                        "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                                        "( SELECT h2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAnt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'idHorarioAnterior', " +
                                        "IFNULL( (SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                        "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                                        "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                        "ORDER BY h2.nome LIMIT 1 ) , " +
                                        "( SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                                        "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaSeg + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "ORDER BY h2.nome LIMIT 1 ) ) AS horarioSeguinte, " +
                                        "IFNULL( (SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                                        "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                        "ORDER BY h2.nome LIMIT 1 ) , " +
                                        "( SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                                        "WHERE hi2." + diaSeg + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                                        "ORDER BY h2.nome LIMIT 1 ) ) AS idHorarioSeguinte, " +
                                        "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                        "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                        "AND pi.itinerario = i.id ) AS 'idPartida', " +
                                        "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                        "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                        "AND pi.itinerario = i.id ) AS 'nomePartida', " +
                                        "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                        "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'nomeDestino', " +
                                        "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroPartida', " +
                                        "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroDestino', " +
                                        "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroPartida', " +
                                        "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroDestino', " +
                                        "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                                        "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                        "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'cidadePartida', " +
                                        "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                        "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                                        "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) " +
                                        "AND pi.itinerario = i.id ) AS 'cidadeDestino' " +
                                        "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario INNER JOIN " +
                                        "itinerario i ON i.id = hi.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
                                        "WHERE hi.itinerario IN ('" + itinerariosDisponiveis + "') AND " + diaAt + " = 1 ORDER BY proximoHorario LIMIT 1");

                                itinerario = appDatabase.itinerarioDAO()
                                        .carregarPorPartidaEDestinoComHorarioSync(query);
                            }

                            if(itinerario.getProximoHorario() != null){

                                if(itinerario.getIdHorarioAnterior() != null){
                                    String obsHorarioAnterior = appDatabase.horarioItinerarioDAO()
                                            .carregarObservacaoPorHorario(itinerario.getIdHorarioAnterior(), itinerario.getItinerario().getId());
                                    itinerario.setObservacaoHorarioAnterior(obsHorarioAnterior);
                                }

                                if(itinerario.getIdHorarioSeguinte() != null){
                                    String obsHorarioSeguinte = appDatabase.horarioItinerarioDAO()
                                            .carregarObservacaoPorHorario(itinerario.getIdHorarioSeguinte(), itinerario.getItinerario().getId());
                                    itinerario.setObservacaoHorarioSeguinte(obsHorarioSeguinte);
                                }

                                if(itinerario.getIdProximoHorario() != null){
                                    String obsProximoHorario = appDatabase.horarioItinerarioDAO()
                                            .carregarObservacaoPorHorario(itinerario.getIdProximoHorario(), itinerario.getItinerario().getId());
                                    itinerario.setObservacaoProximoHorario(obsProximoHorario);
                                }

                                itinerarioAnterior = itinerario;

                                itinerarios.add(itinerario);
                                bairroAnterior = b;
                            }


                        } else{
                            bairroAnterior = b;
                        }

                    }

                    resultadosItinerarios.postValue(itinerarios);

                }
            }
        });



        itinerario = appDatabase.horarioItinerarioDAO()
                .carregarProximoPorPartidaEDestino(myPartida.getBairro().getId(),
                        myDestino.getBairro().getId(), horaEscolhida);
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
