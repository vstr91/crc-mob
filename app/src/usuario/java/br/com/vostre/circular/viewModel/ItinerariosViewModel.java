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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.apache.commons.lang3.StringUtils;
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

    public void carregaResultado(final String horaEscolhida, final String dia, final String diaSeguinte){

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

                            SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT i.*, e.nome AS 'nomeEmpresa', " +
                                    "IFNULL(( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1), ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'proximoHorario', " +
                                    "IFNULL( ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'idProximoHorario', " +
                                    "IFNULL( ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') < " +
                                    "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ), " +
                                    "( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ) ) AS 'horarioAnterior', " +
                                    "IFNULL( ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') < " +
                                    "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ), " +
                                    "( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ) ) AS 'idHorarioAnterior', " +
                                    "IFNULL( ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') > " +
                                    "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), " +
                                    "( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') > " +
                                    "( IFNULL( ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'horarioSeguinte', " +
                                    "IFNULL(( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 " +
                                    "AND TIME(h.nome/1000, 'unixepoch', 'localtime') > ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 " +
                                    "AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1) " +
                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
                                    "AND TIME(h.nome/1000, 'unixepoch', 'localtime') > ( IFNULL( ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 " +
                                    "AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), " +
                                    "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                    "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) ) " +
                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'idHorarioSeguinte', " +
                                    "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idPartida', ( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'nomePartida', " +
                                    "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'nomeDestino', " +
                                    "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroPartida', " +
                                    "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                                    "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroDestino', " +
                                    "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroPartida', " +
                                    "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                                    "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroDestino', " +
                                    "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'cidadePartida', " +
                                    "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                                    "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'cidadeDestino' " +
                                    "FROM itinerario i INNER JOIN empresa e ON e.id = i.empresa WHERE i.id IN ( SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
                                    "WHERE itinerario IN ( SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
                                    "WHERE p.bairro = '"+bairroAnterior.getBairro().getId()+"' AND pi.ordem = 1 ) AND p.bairro = '"+b.getBairro().getId()+"' AND pi.ordem > 1 ) AND proximoHorario IS NOT NULL");


                            String queryNova = "SELECT i.*, e.nome AS 'nomeEmpresa',\n" +
                                    "IFNULL(\n" +
                                    "\t\t(\n" +
                                    "\t\t\tSELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf'\n" +
                                    "\t\t\tAND sabado = 1 AND hi.ativo = 1 \n" +
                                    "\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '23:45:00' \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1\n" +
                                    "\t\t), \n" +
                                    "\t\t(\n" +
                                    "\t\t\tSELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf'\n" +
                                    "\t\t\tAND domingo = 1 \n" +
                                    "\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1)\n" +
                                    "\t\t) AS 'proximoHorario',\n" +
                                    "\n" +
                                    "IFNULL( \n" +
                                    "\t\t(\n" +
                                    "\t\t\tSELECT h.id \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf' \n" +
                                    "\t\t\tAND sabado = 1 \n" +
                                    "\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '23:45:00' \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1\n" +
                                    "\t\t), \n" +
                                    "\t\t(\n" +
                                    "\t\t\tSELECT h.id \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf'\n" +
                                    "\t\t\tAND domingo = 1 \n" +
                                    "\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 )\n" +
                                    "\t\t) AS 'idProximoHorario', \n" +
                                    "\tIFNULL(\n" +
                                    "\t\t(\n" +
                                    "\t\t\tSELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf'\n" +
                                    "\t\t\tAND sabado = 1 \n" +
                                    "\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') < \n" +
                                    "\t\t\t\t(\n" +
                                    "\t\t\t\t\tSELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\t\t\tFROM horario_itinerario hi INNER JOIN \n" +
                                    "\t\t\t\t\t     horario h ON h.id = hi.horario \n" +
                                    "\t\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t\t\tAND sabado = 1 \n" +
                                    "\t\t\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '23:45:00' \n" +
                                    "\t\t\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 \n" +
                                    "\t\t\t\t) \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 \n" +
                                    "\t\t), \n" +
                                    "\t\t(\n" +
                                    "\t\t\tSELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf'\n" +
                                    "\t\t\tAND domingo = 1 \n" +
                                    "\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1\n" +
                                    "\t\t)\n" +
                                    "\t) AS 'horarioAnterior', \n" +
                                    "\tIFNULL(\n" +
                                    "\t\t(\n" +
                                    "\t\t\tSELECT h.id \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf'\n" +
                                    "\t\t\tAND sabado = 1 \n" +
                                    "\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') < \n" +
                                    "\t\t\t(\n" +
                                    "\t\t\t\tSELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\t\tFROM horario_itinerario hi INNER JOIN \n" +
                                    "\t\t\t\t     horario h ON h.id = hi.horario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t\tAND sabado = 1 \n" +
                                    "\t\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '23:45:00' \n" +
                                    "\t\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1\n" +
                                    "\t\t\t) \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 \n" +
                                    "\t\t), \n" +
                                    "\t\t( \n" +
                                    "\t\t\tSELECT h.id \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf' \n" +
                                    "\t\t\tAND domingo = 1 \n" +
                                    "\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 \n" +
                                    "\t\t) \n" +
                                    "\t) AS 'idHorarioAnterior', \n" +
                                    "\tIFNULL( \n" +
                                    "\t\t( \n" +
                                    "\t\t\tSELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf'\n" +
                                    "\t\t\tAND sabado = 1 \n" +
                                    "\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') > \n" +
                                    "\t\t\t( \n" +
                                    "\t\t\t\tSELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\t\tFROM horario_itinerario hi INNER JOIN \n" +
                                    "\t\t\t\t     horario h ON h.id = hi.horario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t\tAND sabado = 1 \n" +
                                    "\t\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '23:45:00' \n" +
                                    "\t\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 \n" +
                                    "\t\t\t) \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 \n" +
                                    "\t\t), \n" +
                                    "\t\t( \n" +
                                    "\t\t\tSELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf'\n" +
                                    "\t\t\tAND domingo = 1 \n" +
                                    "\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') > \n" +
                                    "\t\t\t( \n" +
                                    "\t\t\t\tIFNULL( \n" +
                                    "\t\t\t\t\t( \n" +
                                    "\t\t\t\t\t\tSELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\t\t\t\tFROM horario_itinerario hi INNER JOIN \n" +
                                    "\t\t\t\t\t\t     horario h ON h.id = hi.horario \n" +
                                    "\t\t\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t\t\t\tAND sabado = 1 \n" +
                                    "\t\t\t\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\t\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '23:45:00' \n" +
                                    "\t\t\t\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 \n" +
                                    "\t\t\t\t\t), \n" +
                                    "\t\t\t\t\t( \n" +
                                    "\t\t\t\t\t\tSELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\t\t\t\tFROM horario_itinerario hi INNER JOIN \n" +
                                    "\t\t\t\t\t\t     horario h ON h.id = hi.horario \n" +
                                    "\t\t\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t\t\t\tAND domingo = 1 \n" +
                                    "\t\t\t\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\t\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 \n" +
                                    "\t\t\t\t\t) \n" +
                                    "\t\t\t\t)\n" +
                                    "\t\t\t) \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 \n" +
                                    "\t\t) \n" +
                                    "\t) AS 'horarioSeguinte', \n" +
                                    "\tIFNULL(\n" +
                                    "\t\t( \n" +
                                    "\t\t\tSELECT h.id \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf'\n" +
                                    "\t\t\tAND sabado = 1 \n" +
                                    "\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') > \n" +
                                    "\t\t\t( \n" +
                                    "\t\t\t\tSELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\t\tFROM horario_itinerario hi INNER JOIN \n" +
                                    "\t\t\t\t     horario h ON h.id = hi.horario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t\tAND sabado = 1 \n" +
                                    "\t\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '23:45:00' \n" +
                                    "\t\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1\n" +
                                    "\t\t\t) \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 \n" +
                                    "\t\t), \n" +
                                    "\t\t( \n" +
                                    "\t\t\tSELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\tFROM itinerario i INNER JOIN\n" +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN\n" +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN\n" +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN\n" +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN\n" +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN\n" +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN\n" +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN\n" +
                                    "     horario h ON h.id = hi.horario\n" +
                                    "WHERE i.ativo = 1\n" +
                                    "AND   pp.id <> pd.id\n" +
                                    "AND   pi.ordem < pi2.ordem\n" +
                                    "AND   bp.id = '4c6002e3-8824-4660-82e1-2c6997938167'\n" +
                                    "AND   bd.id = '542ef648-08a6-4488-ac53-f3e1f27cc8cf'\n" +
                                    "\t\t\tAND domingo = 1 \n" +
                                    "\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') > \n" +
                                    "\t\t\t( \n" +
                                    "\t\t\t\tIFNULL( \n" +
                                    "\t\t\t\t\t( \n" +
                                    "\t\t\t\t\t\tSELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\t\t\t\tFROM horario_itinerario hi INNER JOIN \n" +
                                    "\t\t\t\t\t\t     horario h ON h.id = hi.horario \n" +
                                    "\t\t\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t\t\t\tAND sabado = 1 \n" +
                                    "\t\t\t\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\t\t\t\tAND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '23:45:00' \n" +
                                    "\t\t\t\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 \n" +
                                    "\t\t\t\t\t), \n" +
                                    "\t\t\t\t\t( \n" +
                                    "\t\t\t\t\t\tSELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) \n" +
                                    "\t\t\t\t\t\tFROM horario_itinerario hi INNER JOIN \n" +
                                    "\t\t\t\t\t\t     horario h ON h.id = hi.horario \n" +
                                    "\t\t\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t\t\t\tAND domingo = 1 \n" +
                                    "\t\t\t\t\t\tAND hi.ativo = 1 \n" +
                                    "\t\t\t\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 \n" +
                                    "\t\t\t\t\t) \n" +
                                    "\t\t\t\t) \n" +
                                    "\t\t\t) \n" +
                                    "\t\t\tORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 \n" +
                                    "\t\t) \n" +
                                    "\t) AS 'idHorarioSeguinte',\n" +
                                    "\n" +
                                    "( \n" +
                                    "\t\tSELECT pp.id \n" +
                                    "\t\tFROM parada_itinerario pi INNER JOIN \n" +
                                    "\t\t     parada pp ON pp.id = pi.parada \n" +
                                    "\t\tWHERE pi.ordem = ( \n" +
                                    "\t\t\t\tSELECT MIN(ordem) \n" +
                                    "\t\t\t\tFROM parada_itinerario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t      ) \n" +
                                    "\t\tAND pi.itinerario = i.id \n" +
                                    "\t) AS 'idPartida', \n" +
                                    "\t( \n" +
                                    "\t\tSELECT nome \n" +
                                    "\t\tFROM parada_itinerario pi INNER JOIN \n" +
                                    "\t\t     parada pp ON pp.id = pi.parada \n" +
                                    "\t\tWHERE pi.ordem = ( \n" +
                                    "\t\t\t\tSELECT MIN(ordem) \n" +
                                    "\t\t\t\tFROM parada_itinerario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t      ) AND pi.itinerario = i.id \n" +
                                    "\t) AS 'nomePartida', \n" +
                                    "\t( \n" +
                                    "\t\tSELECT nome \n" +
                                    "\t\tFROM parada_itinerario pi INNER JOIN \n" +
                                    "\t\t     parada pp ON pp.id = pi.parada \n" +
                                    "\t\tWHERE pi.ordem = ( \n" +
                                    "\t\t\t\tSELECT MAX(ordem) \n" +
                                    "\t\t\t\tFROM parada_itinerario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t      ) AND pi.itinerario = i.id \n" +
                                    "\t) AS 'nomeDestino', \n" +
                                    "\t( \n" +
                                    "\t\tSELECT b.id \n" +
                                    "\t\tFROM parada_itinerario pi INNER JOIN \n" +
                                    "\t\t     parada pp ON pp.id = pi.parada INNER JOIN \n" +
                                    "\t\t     bairro b ON b.id = pp.bairro \n" +
                                    "\t\tWHERE pi.ordem = ( \n" +
                                    "\t\t\t\tSELECT MIN(ordem) \n" +
                                    "\t\t\t\tFROM parada_itinerario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t      ) \n" +
                                    "\t\tAND pi.itinerario = i.id \n" +
                                    "\t) AS 'idBairroPartida', \n" +
                                    "\t( \n" +
                                    "\t\tSELECT b.id \n" +
                                    "\t\tFROM parada_itinerario pi INNER JOIN \n" +
                                    "\t\t     parada pp ON pp.id = pi.parada INNER JOIN \n" +
                                    "\t\t     bairro b ON b.id = pp.bairro \n" +
                                    "\t\tWHERE pi.ordem = ( \n" +
                                    "\t\t\t\tSELECT MAX(ordem) \n" +
                                    "\t\t\t\tFROM parada_itinerario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t      ) \n" +
                                    "\t\tAND pi.itinerario = i.id \n" +
                                    "\t) AS 'idBairroDestino', \n" +
                                    "\t( \n" +
                                    "\t\tSELECT b.nome \n" +
                                    "\t\tFROM parada_itinerario pi INNER JOIN \n" +
                                    "\t\t     parada pp ON pp.id = pi.parada INNER JOIN \n" +
                                    "\t\t     bairro b ON b.id = pp.bairro \n" +
                                    "\t\tWHERE pi.ordem = ( \n" +
                                    "\t\t\t\tSELECT MIN(ordem) \n" +
                                    "\t\t\t\tFROM parada_itinerario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t      ) \n" +
                                    "\t\tAND pi.itinerario = i.id \n" +
                                    "\t) AS 'bairroPartida', \n" +
                                    "\t( \n" +
                                    "\t\tSELECT b.nome \n" +
                                    "\t\tFROM parada_itinerario pi INNER JOIN \n" +
                                    "\t\t     parada pp ON pp.id = pi.parada INNER JOIN \n" +
                                    "\t\t     bairro b ON b.id = pp.bairro \n" +
                                    "\t\tWHERE pi.ordem = ( \n" +
                                    "\t\t\t\tSELECT MAX(ordem) \n" +
                                    "\t\t\t\tFROM parada_itinerario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t      ) \n" +
                                    "\t\tAND pi.itinerario = i.id \n" +
                                    "\t) AS 'bairroDestino', \n" +
                                    "\t( \n" +
                                    "\t\tSELECT c.nome \n" +
                                    "\t\tFROM parada_itinerario pi INNER JOIN \n" +
                                    "\t\t     parada pp ON pp.id = pi.parada INNER JOIN \n" +
                                    "\t\t     bairro b ON b.id = pp.bairro INNER JOIN \n" +
                                    "\t\t     cidade c ON c.id = b.cidade \n" +
                                    "\t\tWHERE pi.ordem = ( \n" +
                                    "\t\t\t\tSELECT MIN(ordem) \n" +
                                    "\t\t\t\tFROM parada_itinerario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t      ) \n" +
                                    "\t\tAND pi.itinerario = i.id \n" +
                                    "\t) AS 'cidadePartida', \n" +
                                    "\t( \n" +
                                    "\t\tSELECT c.nome \n" +
                                    "\t\tFROM parada_itinerario pi INNER JOIN \n" +
                                    "\t\t     parada pp ON pp.id = pi.parada INNER JOIN \n" +
                                    "\t\t     bairro b ON b.id = pp.bairro INNER JOIN \n" +
                                    "\t\t     cidade c ON c.id = b.cidade \n" +
                                    "\t\tWHERE pi.ordem = ( \n" +
                                    "\t\t\t\tSELECT MAX(ordem) \n" +
                                    "\t\t\t\tFROM parada_itinerario \n" +
                                    "\t\t\t\tWHERE itinerario = i.id \n" +
                                    "\t\t\t      ) \n" +
                                    "\t\tAND pi.itinerario = i.id \n" +
                                    " ) AS 'cidadeDestino' " +
                                    "FROM itinerario i INNER JOIN " +
                                    "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN " +
                                    "     parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "     bairro bp ON bp.id = pp.bairro INNER JOIN " +
                                    "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN " +
                                    "     parada pd ON pd.id = pi2.parada INNER JOIN " +
                                    "     bairro bd ON bd.id = pd.bairro INNER JOIN " +
                                    "     horario_itinerario hi ON hi.itinerario = i.id INNER JOIN " +
                                    "     horario h ON h.id = hi.horario INNER JOIN " +
                                    "     empresa e ON e.id = i.empresa " +
                                    "WHERE i.ativo = 1 " +
                                    "AND   pp.id <> pd.id " +
                                    "AND   pi.ordem < pi2.ordem " +
                                    "AND   bp.id = '"+bairroAnterior.getBairro().getId()+"' " +
                                    "AND   bd.id = '"+b.getBairro().getId()+"' " +
                                    "ORDER BY h.nome LIMIT 1";



                            ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO()
                                    .carregarPorPartidaEDestinoComHorarioSync(query);

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

    public void carregaResultadoInvertido(final String horaEscolhida, final String dia, final String diaSeguinte){

        final BairroCidade bairro = myPartida;
        myPartida = myDestino;
        myDestino = bairro;
        boolean flagDiaSeguinte = false;

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

//                            SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT i.*, e.nome AS 'nomeEmpresa', " +
//                                    "IFNULL(( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
//                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1), ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
//                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
//                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'proximoHorario', " +
//                                    "IFNULL( ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
//                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'idProximoHorario', " +
//                                    "IFNULL( ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') < " +
//                                    "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
//                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ), " +
//                                    "( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ) ) AS 'horarioAnterior', " +
//                                    "IFNULL( ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') < " +
//                                    "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
//                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ), " +
//                                    "( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
//                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') DESC LIMIT 1 ) ) AS 'idHorarioAnterior', " +
//                                    "IFNULL( ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') > " +
//                                    "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
//                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), " +
//                                    "( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') > " +
//                                    "( IFNULL( ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
//                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
//                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
//                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) ) ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'horarioSeguinte', " +
//                                    "IFNULL(( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 " +
//                                    "AND TIME(h.nome/1000, 'unixepoch', 'localtime') > ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
//                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 " +
//                                    "AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1) " +
//                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
//                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
//                                    "AND TIME(h.nome/1000, 'unixepoch', 'localtime') > ( IFNULL( ( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
//                                    "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 " +
//                                    "AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), " +
//                                    "( SELECT strftime('%H:%M:%S', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                                    "WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) ) " +
//                                    "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'idHorarioSeguinte', " +
//                                    "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
//                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idPartida', ( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'nomePartida', " +
//                                    "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
//                                    "WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'nomeDestino', " +
//                                    "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
//                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroPartida', " +
//                                    "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
//                                    "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'idBairroDestino', " +
//                                    "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
//                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroPartida', " +
//                                    "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
//                                    "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'bairroDestino', " +
//                                    "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
//                                    "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'cidadePartida', " +
//                                    "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
//                                    "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id ) AND pi.itinerario = i.id ) AS 'cidadeDestino' " +
//                                    "FROM itinerario i INNER JOIN empresa e ON e.id = i.empresa WHERE i.id IN ( SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
//                                    "WHERE itinerario IN ( SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
//                                    "WHERE p.bairro = '"+bairroAnterior.getBairro().getId()+"' AND pi.ordem = 1 ) AND p.bairro = '"+b.getBairro().getId()+"' AND pi.ordem > 1 ) LIMIT 1");

//                            ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO()
//                                    .carregarPorPartidaEDestinoComHorarioSync(query);

                            SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT i.id FROM itinerario i INNER JOIN " +
                                    "parada_itinerario pi ON pi.itinerario = i.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                                    "bairro bp ON bp.id = pp.bairro INNER JOIN parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN " +
                                    "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                                    "WHERE i.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem " +
                                    "AND bp.id = '"+bairroAnterior.getBairro().getId()+"' AND bd.id = '"+b.getBairro().getId()+"' ORDER BY i.id");

                            List<String> itinerariosDisponiveis = appDatabase.itinerarioDAO()
                                    .carregarOpcoesPorPartidaEDestinoSync(query);

                            if(itinerariosDisponiveis.size() > 0) {

                                SimpleSQLiteQuery queryProximoHorario = new SimpleSQLiteQuery("SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                        "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                        "WHERE itinerario IN('"+StringUtils.join(itinerariosDisponiveis, ",")+"') AND "+dia+" = 1 " +
                                        "AND strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) > '"+hora+"' ORDER BY h.nome LIMIT 1");

                                String proximoHorario = appDatabase.horarioItinerarioDAO()
                                        .carregarProximoHorarioPorItinerario(queryProximoHorario);

                                if(proximoHorario == null) {
                                    queryProximoHorario = new SimpleSQLiteQuery("SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                                            "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                                            "WHERE itinerario IN('"+StringUtils.join(itinerariosDisponiveis, ",")+"') " +
                                            "AND "+diaSeguinte+" = 1 ORDER BY h.nome LIMIT 1");

                                    proximoHorario = appDatabase.horarioItinerarioDAO()
                                            .carregarProximoHorarioPorItinerario(queryProximoHorario);
                                }

                                HorarioItinerarioNome horario = appDatabase.horarioItinerarioDAO().carregarPorIdSync(proximoHorario);



                            }

//                            if(itinerario.getProximoHorario() != null){
//
//                                if(itinerario.getIdHorarioAnterior() != null){
//                                    String obsHorarioAnterior = appDatabase.horarioItinerarioDAO()
//                                            .carregarObservacaoPorHorario(itinerario.getIdHorarioAnterior(), itinerario.getItinerario().getId());
//                                    itinerario.setObservacaoHorarioAnterior(obsHorarioAnterior);
//                                }
//
//                                if(itinerario.getIdHorarioSeguinte() != null){
//                                    String obsHorarioSeguinte = appDatabase.horarioItinerarioDAO()
//                                            .carregarObservacaoPorHorario(itinerario.getIdHorarioSeguinte(), itinerario.getItinerario().getId());
//                                    itinerario.setObservacaoHorarioSeguinte(obsHorarioSeguinte);
//                                }
//
//                                if(itinerario.getIdProximoHorario() != null){
//                                    String obsProximoHorario = appDatabase.horarioItinerarioDAO()
//                                            .carregarObservacaoPorHorario(itinerario.getIdProximoHorario(), itinerario.getItinerario().getId());
//                                    itinerario.setObservacaoProximoHorario(obsProximoHorario);
//                                }

                                itinerarioAnterior = itinerario;

                                itinerarios.add(itinerario);
                                bairroAnterior = b;
//                            }


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
