package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Feriado;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.utils.DataHoraUtils;
import br.com.vostre.circular.utils.LocationUtils;
import br.com.vostre.circular.utils.StringUtils;
import br.com.vostre.circular.view.listener.FeriadoListener;
import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphEdge;
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

    public BairroCidade myPartida = null;
    public BairroCidade myDestino = null;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    public CidadeEstado cidadeDestinoConsulta;
    public LiveData<List<CidadeEstado>> cidadesDestinoConsulta;
    public BairroCidade myDestinoConsulta = null;
    public LiveData<List<BairroCidade>> bairrosDestinoConsulta;
    public LiveData<BairroCidade> bairroDestinoConsulta;

    public LiveData<ItinerarioPartidaDestino> itinerarioResultado;

    public int escolhaAtual = 0; // 0 partida - 1 destino

    public static List<ItinerarioPartidaDestino> itinerarios;
    public MutableLiveData<List<ItinerarioPartidaDestino>> resultadosItinerarios;

    boolean todos = true;

    public static MutableLiveData<Boolean> isFeriado;

    boolean trechoIsolado = false;

    public LiveData<List<ItinerarioPartidaDestino>> itinerariosPorLinha;

    public MutableLiveData<List<ItinerarioPartidaDestino>> itinerariosPorDestino;

    public MutableLiveData<Location> localAtual;
    public LiveData<List<ParadaBairro>> paradasProximas;
    public boolean destacaItinerario = false;

    public boolean isTodos() {
        return todos;
    }

    public void setTodos(boolean todos) {
        this.todos = todos;
    }

    public CidadeEstado getCidadePartida() {
        return cidadePartida;
    }

    public void setCidadePartida(CidadeEstado cidadePartida) {
        this.cidadePartida = cidadePartida;
        bairros = appDatabase.bairroDAO().listarTodosAtivosComCidadePorCidade(cidadePartida.getCidade().getId());
    }

    public void setCidadeDestinoConsulta(CidadeEstado cidadeDestinoConsulta) {
        this.cidadeDestinoConsulta = cidadeDestinoConsulta;
        bairrosDestinoConsulta = appDatabase.bairroDAO().listarTodosAtivosComCidadePorCidade(cidadeDestinoConsulta.getCidade().getId());
    }

    public CidadeEstado getCidadeDestino() {
        return cidadeDestino;
    }

    public void setCidadeDestino(CidadeEstado cidadeDestino) {
        this.cidadeDestino = cidadeDestino;

        if(this.bairroPartida.getValue() == null){
            bairrosDestino = appDatabase.bairroDAO().listarTodosAtivosComCidadePorCidadeFiltro(cidadeDestino.getCidade().getId(), myPartida.getBairro().getId());
        } else{
            bairrosDestino = appDatabase.bairroDAO().listarTodosAtivosComCidadePorCidadeFiltro(cidadeDestino.getCidade().getId(), bairroPartida.getValue().getBairro().getId());
        }


    }

    public LiveData<BairroCidade> getBairroDestino() {
        return bairroDestino;
    }

    public void setBairroDestino(BairroCidade bairroDestino) {
        this.bairroDestino = appDatabase.bairroDAO().carregar(bairroDestino.getBairro().getId());
    }

    public void setBairroDestinoConsulta(BairroCidade bairroDestino) {
        this.bairroDestinoConsulta = appDatabase.bairroDAO().carregar(bairroDestino.getBairro().getId());
    }

    public LiveData<BairroCidade> getBairroPartida() {
        return bairroPartida;
    }

    public void setBairroPartida(BairroCidade umBairroPartida) {

        if(this.bairroPartida.getValue() == null || this.bairroPartida.getValue().getBairro().getId() != umBairroPartida.getBairro().getId()){
            this.bairroPartida = appDatabase.bairroDAO().carregar(umBairroPartida.getBairro().getId());
        }

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

        bairrosDestinoConsulta = appDatabase.bairroDAO().listarTodosAtivosComCidadePorCidade(null);
        cidadesDestinoConsulta = appDatabase.cidadeDAO().listarTodosAtivasComEstado();

        itinerariosPorLinha = appDatabase.itinerarioDAO().listarTodosAtivosPorLinhaComBairroEHorario("", "00:00:00");

        itinerariosPorDestino = new MutableLiveData<>();

        isFeriado = new MutableLiveData<>();
        isFeriado.setValue(false);

        localAtual = new MutableLiveData<>();
        localAtual.postValue(new Location(LocationManager.GPS_PROVIDER));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());
    }

    public void iniciarAtualizacoesPosicao(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    if(location.getAccuracy() <= 20){
                        localAtual.postValue(location);

                        if(localAtual.getValue() != null){
                            localAtual.getValue().setLatitude(localAtual.getValue().getLatitude());
                            localAtual.getValue().setLongitude(localAtual.getValue().getLongitude());
                        }

                    }

                }
            }
        };
    }

    public void buscarPorLinha(final String linha){

        String dia = DataHoraUtils.getDiaAtual();
        String diaSeguinte = DataHoraUtils.getDiaSeguinte();
        String hora = DataHoraUtils.getHoraAtual()+":00";

        if(isFeriado != null && isFeriado.getValue() != null && isFeriado.getValue()){
            dia = "domingo";
        }

        final SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT i.*, e.nome AS 'nomeEmpresa', " +
                "IFNULL(( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1), ( SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 " +
                "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) AS 'proximoHorario', " +

                "IFNULL((SELECT Strftime('%H:%M', Time(h.nome / 1000, 'unixepoch', 'localtime')) " +
                "FROM   horario_itinerario hi " +
                "INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE  itinerario = i.id " +
                "AND "+dia+" = 1 " +
                "AND hi.ativo = 1 " +
                "AND TIME(h.nome / 1000, 'unixepoch', 'localtime') >= '"+hora+"' " +
                "ORDER  BY TIME(h.nome / 1000, 'unixepoch', 'localtime') LIMIT  1), " +
                "(SELECT '23:59' " +
                "FROM   horario_itinerario hi " +
                "INNER JOIN horario h ON h.id = hi.horario " +
                "WHERE  itinerario = i.id " +
                "AND "+diaSeguinte+" = 1 " +
                "AND hi.ativo = 1 " +
                "ORDER  BY Time(h.nome / 1000, 'unixepoch', 'localtime') LIMIT  1)) AS 'flagDia', " +

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
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idPartida', " +

                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'nomePartida', " +

                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino', " +

                "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idBairroPartida', " +

                "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idBairroDestino', " +

                "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'bairroPartida', " +

                "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'bairroDestino', " +

                "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'cidadePartida', " +

                "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'cidadeDestino' " +

                "FROM itinerario i INNER JOIN empresa e ON e.id = i.empresa WHERE i.id IN (SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
                "WHERE i.sigla LIKE '"+linha+"' AND pi.ativo = 1 AND p.id <> (SELECT pi.parada FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
                "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AND proximoHorario IS NOT NULL) " +
                "ORDER BY flagDia, proximoHorario, cidadePartida, bairroPartida, cidadeDestino, bairroDestino");

        itinerariosPorLinha = appDatabase.itinerarioDAO()
                .listarTodosAtivosPorLinhaComBairroEHorarioCompleto(query);
    }

    public void buscarPorDestino(final String bairro, final String bairroAtual){

        String dia = DataHoraUtils.getDiaAtual();
        final String diaSeguinte = DataHoraUtils.getDiaSeguinte();
        final String diaAnterior = DataHoraUtils.getDiaAnterior();
        final String hora = DataHoraUtils.getHoraAtual()+":00";

        if(isFeriado != null && isFeriado.getValue() != null && isFeriado.getValue()){
            dia = "domingo";
        }

        final String finalDia = dia;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                List<String> itinerariosDisponiveis;
                List<ItinerarioPartidaDestino> itis;

                if(bairroAtual.isEmpty()){
                    itinerariosDisponiveis = appDatabase.itinerarioDAO().carregarOpcoesPorDestinoTrechoConsultaSync(bairro);

                    itis = new ArrayList<>();

                    for(String iti : itinerariosDisponiveis){
                        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                                geraQueryResultadoConsulta(bairro, diaAnterior, finalDia, diaSeguinte, hora, iti));

                        ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO()
                                .carregarPorPartidaEDestinoComHorarioSync(query);

                        itis.add(itinerario);
                    }

                    Collections.sort(itis, new Comparator<ItinerarioPartidaDestino>() {
                        @Override
                        public int compare(ItinerarioPartidaDestino itinerarioPartidaDestino, ItinerarioPartidaDestino t1) {

                            return DateTimeFormat.forPattern("HH:mm").parseLocalTime(itinerarioPartidaDestino.getProximoHorario())
                                    .compareTo(DateTimeFormat.forPattern("HH:mm").parseLocalTime(t1.getProximoHorario()));

                        }
                    });

                    destacaItinerario = false;

                } else{
                    itinerariosDisponiveis = appDatabase.itinerarioDAO().carregarOpcoesPorPartidaEDestinoTrechoConsultaSync(bairro, bairroAtual);

                    itis = new ArrayList<>();

                    String itinerarioDestaque = itinerariosDisponiveis.get(0);

                    ItinerarioPartidaDestino destaque = new ItinerarioPartidaDestino();
                    destaque.getItinerario().setId(itinerarioDestaque);

                    for(String iti : itinerariosDisponiveis){
                        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                                geraQueryResultadoConsulta(bairro, diaAnterior, finalDia, diaSeguinte, hora, iti));

                        ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO()
                                .carregarPorPartidaEDestinoComHorarioSync(query);

                        itis.add(itinerario);
                    }

                    Collections.sort(itis, new Comparator<ItinerarioPartidaDestino>() {
                        @Override
                        public int compare(ItinerarioPartidaDestino itinerarioPartidaDestino, ItinerarioPartidaDestino t1) {

                            return DateTimeFormat.forPattern("HH:mm").parseLocalTime(itinerarioPartidaDestino.getProximoHorario())
                                    .compareTo(DateTimeFormat.forPattern("HH:mm").parseLocalTime(t1.getProximoHorario()));

                        }
                    });

                    destaque = itis.get(itis.indexOf(destaque));

                    if(destaque.getIdBairroPartida().equals(bairroAtual)){

                        itis.remove(destaque);
                        itis.add(0, destaque);

                        destacaItinerario = true;
                    } else{
                        destacaItinerario = false;
                    }



                }

                itinerariosPorDestino.postValue(itis);

            }
        });


    }

    public void checaFeriado(final Calendar data){

        new FeriadoAsyncTask(appDatabase).execute(data);
    }

    public static class FeriadoAsyncTask extends AsyncTask<Calendar, Void, Void> {

        private AppDatabase db;
        Feriado feriado;
        FeriadoListener listener;

        FeriadoAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(final Calendar... params) {
            DateTime dt = new DateTime(params[0], DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo")));
            feriado = db.feriadoDAO().encontrarPorData(DateTimeFormat.forPattern("yyyy-MM-dd").print(dt));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(feriado != null){
                isFeriado.postValue(true);
            } else{
                isFeriado.postValue(false);
            }

        }

    }

    public void buscarParadasProximas(Context ctx, Location location){

        paradasProximas = LocationUtils.buscaParadasProximas(ctx, location, 500);

    }

    public void carregaResultado(final String horaEscolhida, final String dia, final String diaSeguinte,
                                 final String diaAnterior, boolean inversao){

//        if(!inversao){
//            myPartida = bairroPartida.getValue();
//            myDestino = bairroDestino.getValue();
//        }

        if(bairroPartida.getValue() != null){
            myPartida = bairroPartida.getValue();
        }

        if(bairroDestino.getValue() != null){
            myDestino = bairroDestino.getValue();
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                PeriodFormatter parser =
                        new PeriodFormatterBuilder()
                                .appendHours().appendLiteral(":")
                                .appendMinutes().appendLiteral(":")
                                .appendSeconds().toFormatter();

                PeriodFormatter printer =
                        new PeriodFormatterBuilder()
                                .printZeroAlways().minimumPrintedDigits(2)
                                //.appendDays().appendLiteral(" dia(s) ")
                                .appendHours().appendLiteral(":")
                                .appendMinutes().appendLiteral(":")
                                .appendSeconds().toFormatter();

                SimpleSQLiteQuery queryOpcoes = new SimpleSQLiteQuery(
                        geraQueryItinerarios(myPartida.getBairro().getId(), myDestino.getBairro().getId(), todos));

                List<String> opcoes = appDatabase.itinerarioDAO()
                        .carregarOpcoesPorPartidaEDestinoSync(queryOpcoes);

                if(opcoes.size() > 0){

                    List<ItinerarioPartidaDestino> itinerarios = new ArrayList<>();

                    String itinerariosDisponiveis = TextUtils.join("','", opcoes);

                    SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                            geraQueryResultado(myPartida.getBairro().getId(),
                                    myDestino.getBairro().getId(), diaAnterior, dia, diaSeguinte, horaEscolhida, itinerariosDisponiveis));

                    ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO()
                            .carregarPorPartidaEDestinoComHorarioSync(query);

                    if(itinerario != null){
                        itinerario.setDia(dia);
                        itinerario.setHora(horaEscolhida);

                        if(itinerario.isFlagTrecho()){

                            List<ParadaItinerario> pis = appDatabase.paradaItinerarioDAO().listarTrechosIntervalo(itinerario.getItinerario().getId(), myPartida.getBairro().getId(),
                                    myDestino.getBairro().getId());

                            Double total = 0d;
                            Double distanciaTotal = 0d;
                            DateTime tempoTotal = new DateTime();

                            Period period = Period.ZERO;

                            for(ParadaItinerario pi : pis){

                                if(pi.getValorSeguinte() != null){
                                    total += pi.getValorSeguinte();
                                }

                                if(pi.getDistanciaSeguinte() != null){
                                    distanciaTotal += pi.getDistanciaSeguinte();
                                }

                                if(pi.getTempoSeguinte() != null){
                                    String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(pi.getTempoSeguinte().getMillis());
                                    period = period.plus(parser.parsePeriod(tempo));
                                }

                            }

                            tempoTotal = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(printer.print(period.normalizedStandard(PeriodType.time())));

                            if(total > 0){
                                itinerario.setTarifaTrecho(total);
                            }

                            if(distanciaTotal > 0){
                                itinerario.setDistanciaTrecho(distanciaTotal);
                            }

                            if(tempoTotal.getMinuteOfHour() > 0){
                                itinerario.setTempoTrecho(tempoTotal);
                            }

                            Parada p = appDatabase.paradaDAO().carregarSync(itinerario.getParadaPartida());
                            itinerario.setNomePartida(p.getNome());
                            itinerario.setIdPartida(p.getId());

                        }

                    }

                    if(itinerario == null){

                        String diaAnt = dia;
                        String diaAt = diaSeguinte;
                        String diaSeg = DataHoraUtils.getDiaSeguinte(diaSeguinte);

                        query = new SimpleSQLiteQuery(
                                geraQueryResultadoDiaSeguinte(myPartida.getBairro().getId(), myDestino.getBairro().getId(),
                                        diaAnt, diaAt, diaSeg, itinerariosDisponiveis));

                        itinerario = appDatabase.itinerarioDAO()
                                .carregarPorPartidaEDestinoComHorarioSync(query);

                        if(itinerario != null){
                            itinerario.setDia(diaAt);
                            itinerario.setHora(horaEscolhida);

                            if(itinerario.isFlagTrecho()){

                                List<ParadaItinerario> pis = appDatabase.paradaItinerarioDAO().listarTrechosIntervalo(itinerario.getItinerario().getId(), myPartida.getBairro().getId(),
                                        myDestino.getBairro().getId());

                                Double total = 0d;
                                Double distanciaTotal = 0d;
                                DateTime tempoTotal = new DateTime();

                                Period period = Period.ZERO;

                                for(ParadaItinerario pi : pis){

                                    if(pi.getValorSeguinte() != null){
                                        total += pi.getValorSeguinte();
                                    }

                                    if(pi.getDistanciaSeguinte() != null){
                                        distanciaTotal += pi.getDistanciaSeguinte();
                                    }

                                    if(pi.getTempoSeguinte() != null){
                                        String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(pi.getTempoSeguinte().getMillis());
                                        period = period.plus(parser.parsePeriod(tempo));
                                    }

                                }

                                tempoTotal = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(printer.print(period.normalizedStandard(PeriodType.time())));

                                if(total > 0){
                                    itinerario.setTarifaTrecho(total);
                                }

                                if(distanciaTotal > 0){
                                    itinerario.setDistanciaTrecho(distanciaTotal);
                                }

                                if(tempoTotal.getMinuteOfHour() > 0){
                                    itinerario.setTempoTrecho(tempoTotal);
                                }

                                Parada pa = appDatabase.paradaDAO().carregarSync(itinerario.getParadaPartida());
                                itinerario.setNomePartida(pa.getNome());
                                itinerario.setIdPartida(pa.getId());

                            }

                        }


                    }

                    if(itinerario != null && itinerario.getProximoHorario() != null){

                        if(itinerario.getIdHorarioAnterior() != null){
                            String obsHorarioAnterior = appDatabase.horarioItinerarioDAO()
                                    .carregarObservacaoPorHorario(itinerario.getIdHorarioAnterior());
                            itinerario.setObservacaoHorarioAnterior(obsHorarioAnterior);
                        }

                        if(itinerario.getIdHorarioSeguinte() != null){
                            String obsHorarioSeguinte = appDatabase.horarioItinerarioDAO()
                                    .carregarObservacaoPorHorario(itinerario.getIdHorarioSeguinte());
                            itinerario.setObservacaoHorarioSeguinte(obsHorarioSeguinte);
                        }

                        if(itinerario.getIdProximoHorario() != null){
                            String obsProximoHorario = appDatabase.horarioItinerarioDAO()
                                    .carregarObservacaoPorHorario(itinerario.getIdProximoHorario());
                            itinerario.setObservacaoProximoHorario(obsProximoHorario);
                        }

                        itinerarios.add(itinerario);

                        resultadosItinerarios.postValue(itinerarios);
                    }

                } else{
                    // NAO ENCONTROU ITINERARIO DIRETO. BUSCANDO ITINERARIOS CONECTADOS QUE LEVEM AO DESTINO SELECIONADO
                    GraphBuilder<String, Double> builder = GraphBuilder.create();

                    itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosTesteNovoSync();
//                itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosSync();
//                    itinerarios.addAll(appDatabase.itinerarioDAO().listarTodosAtivosGraphSync());

//                    Collections.sort(itinerarios, new Comparator<ItinerarioPartidaDestino>() {
//                        @Override
//                        public int compare(ItinerarioPartidaDestino itinerarioPartidaDestino, ItinerarioPartidaDestino t1) {
//
//                            if(itinerarioPartidaDestino.isFlagTrecho() && (itinerarioPartidaDestino.getDistanciaTrechoMetros() != null && t1.getDistanciaTrechoMetros() != null)){
//                                return itinerarioPartidaDestino.getDistanciaTrechoMetros().compareTo(t1.getDistanciaTrechoMetros());
//                            } else{
//                                return itinerarioPartidaDestino.getItinerario().getDistanciaMetros().compareTo(t1.getItinerario().getDistanciaMetros());
//                            }
//
//                        }
//                    });

//                    List<Bairro> bairros = appDatabase.bairroDAO().listarTodosSync();
//
//                    String nodesDebug = "";
//                    String graphDebug = "";
//                    int in = 100;
//                    int ib = 100;
//
//                    for(Bairro b : bairros){
//                        nodesDebug = nodesDebug.concat("<node positionX=\""+ib+"\" positionY=\""+(ib+50)+"\" id=\""
//                                +b.getId()+"\" mainText=\""+ StringUtils.toSlug(b.getNome())+"\" upText=\"\"></node>");
//                        ib = ib+50;
//                    }
//
//                    String lines = "";

                    for(ItinerarioPartidaDestino i : itinerarios){

//                        BairroCidade partida = appDatabase.bairroDAO().carregarSync(i.getIdBairroPartida());
//                        BairroCidade destino = appDatabase.bairroDAO().carregarSync(i.getIdBairroDestino());

                        if(i.isFlagTrecho()){

                            if(i.getDistanciaTrecho() != null){
                                builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(i.getDistanciaTrecho()+50000);

//                                graphDebug = graphDebug.concat("<edge vertex1=\""+i.getIdBairroPartida()+"\" vertex2=\""+i.getIdBairroDestino()+"\" isDirect=\"true\" weight=\""
//                                        +(i.getDistanciaTrecho()+50000)+"\" useWeight=\"true\" id=\""+i.getIdBairroPartida()+i.getIdBairroDestino()
//                                        +"\" text=\"\" arrayStyleStart=\"\" arrayStyleFinish=\"\" model_width=\"4\" model_type=\"0\" model_curvedValue=\"0.1\"></edge>");
//
//
//
//                                lines = lines.concat(partida.getBairro().getId()+";"+partida.getBairro().getNome()+";"+partida.getNomeCidade()+";"
//                                        +destino.getBairro().getId()+";"+destino.getBairro().getNome()+";"+destino.getNomeCidade()+";"+(i.getDistanciaTrecho()+5000)+System.lineSeparator());

                            } else{
                                builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(100000d);

//                                graphDebug = graphDebug.concat("<edge vertex1=\""+i.getIdBairroPartida()+"\" vertex2=\""+i.getIdBairroDestino()+"\" isDirect=\"true\" weight=\""
//                                        +100000+"\" useWeight=\"true\" id=\""+i.getIdBairroPartida()+i.getIdBairroDestino()
//                                        +"\" text=\"\" arrayStyleStart=\"\" arrayStyleFinish=\"\" model_width=\"4\" model_type=\"0\" model_curvedValue=\"0.1\"></edge>");
//
//                                lines = lines.concat(partida.getBairro().getId()+";"+partida.getBairro().getNome()+";"+partida.getNomeCidade()+";"
//                                        +destino.getBairro().getId()+";"+destino.getBairro().getNome()+";"+destino.getNomeCidade()+";"+100000+System.lineSeparator());

                            }


                        } else{

                            if(i.getItinerario().getDistancia() <= 50000){
                                builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(1d);

//                                graphDebug = graphDebug.concat("<edge vertex1=\""+i.getIdBairroPartida()+"\" vertex2=\""+i.getIdBairroDestino()+"\" isDirect=\"true\" weight=\""
//                                        +1+"\" useWeight=\"true\" id=\""+i.getIdBairroPartida()+i.getIdBairroDestino()
//                                        +"\" text=\"\" arrayStyleStart=\"\" arrayStyleFinish=\"\" model_width=\"4\" model_type=\"0\" model_curvedValue=\"0.1\"></edge>");
//
//                                lines = lines.concat(partida.getBairro().getId()+";"+partida.getBairro().getNome()+";"+partida.getNomeCidade()+";"
//                                        +destino.getBairro().getId()+";"+destino.getBairro().getNome()+";"+destino.getNomeCidade()+";"+1+System.lineSeparator());

                            } else{
                                builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino())
                                        .withEdge((i.getItinerario().getDistancia()-50000));

//                                graphDebug = graphDebug.concat("<edge vertex1=\""+i.getIdBairroPartida()+"\" vertex2=\""+i.getIdBairroDestino()+"\" isDirect=\"true\" weight=\""
//                                        +(i.getItinerario().getDistancia()-50000)+"\" useWeight=\"true\" id=\""+i.getIdBairroPartida()+i.getIdBairroDestino()
//                                        +"\" text=\"\" arrayStyleStart=\"\" arrayStyleFinish=\"\" model_width=\"4\" model_type=\"0\" model_curvedValue=\"0.1\"></edge>");
//
//                                lines = lines.concat(partida.getBairro().getId()+";"+partida.getBairro().getNome()+";"+partida.getNomeCidade()+";"
//                                        +destino.getBairro().getId()+";"+destino.getBairro().getNome()+";"+destino.getNomeCidade()+";"+(i.getItinerario().getDistancia()-50000)+System.lineSeparator());

                            }

                        }

//                        in = in + 50;


                    }

                    HipsterDirectedGraph<String,Double> graph = builder.createDirectedGraph();

                    //Iterable<GraphEdge<String, Double>> a = graph.edgesOf(myPartida.getBairro().getId());

                    SearchProblem p = GraphSearchProblem
                            .startingFrom(myPartida.getBairro().getId())
                            .in(graph)
                            .takeCostsFromEdges()
                            .build();

                    Algorithm.SearchResult result = Hipster.createAStar(p).search(myDestino.getBairro().getId());

                    //System.out.println("RES: "+result);

                    // DEBUG GRAPH
/*
                    List<List> paths = result.getOptimalPaths();

                    for(List a : paths){

                        for(Object b : a){

                        }

                    }

                    for(GraphEdge<String, Double> g : graph.edges()){
                        Log.d("AA", g.getVertex1() + ";" + g.getVertex2() + ";" + g.getEdgeValue() + ";" + g.getType().name());

                        BairroCidade partida = appDatabase.bairroDAO().carregarSync(g.getVertex1());
                        BairroCidade destino = appDatabase.bairroDAO().carregarSync(g.getVertex2());

                        if((partida.getNomeCidade().equals("Barra do Piraí") && destino.getNomeCidade().equals("Barra do Pirai")) ||
                                (partida.getNomeCidade().equals("Barra do Piraí") && destino.getNomeCidade().equals("Volta Redonda")) ||
                                (partida.getNomeCidade().equals("Barra do Piraí") && destino.getNomeCidade().equals("Barra Mansa")) ||
                                (partida.getNomeCidade().equals("Volta Redonda") && destino.getNomeCidade().equals("Barra Mansa")) ||
                                (partida.getNomeCidade().equals("Volta Redonda") && destino.getNomeCidade().equals("Resende")) ||
                                (partida.getNomeCidade().equals("Barra Mansa") && destino.getNomeCidade().equals("Resende"))){

                            System.out.println("VERTEX LOAD: "+partida.getBairro().getNome()+" ("+partida.getNomeCidadeComEstado()+")"+" | "
                                    +destino.getBairro().getNome()+" ("+destino.getNomeCidadeComEstado()+")"+": "+g.getEdgeValue()+" || "+g.getType().name());

                        }



                    }
*/
                    // FIM DEBUG GRAPH


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

                                Period period = Period.ZERO;

                                if(itinerarioAnterior != null){
                                    String proximoHorario = itinerarioAnterior.getProximoHorario()+":00";

                                    String tempo = "";

                                    if(itinerarioAnterior.getTempoTrecho() != null){
                                        tempo = DateTimeFormat.forPattern("HH:mm:ss").print(itinerarioAnterior.getTempoTrecho().getMillis());
                                    } else{
                                        tempo = DateTimeFormat.forPattern("HH:mm:ss").print(itinerarioAnterior.getItinerario().getTempo().getMillis());
                                    }

//                                    String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(itinerarioAnterior.getItinerario().getTempo().getMillis());

                                    period = period.plus(parser.parsePeriod(proximoHorario));
                                    period = period.plus(parser.parsePeriod(tempo));

                                    hora = printer.print(period.normalizedStandard(PeriodType.time()));

                                } else{
                                    hora = horaEscolhida;//"00:00";//DateTimeFormat.forPattern("HH:mm:00").print(DateTime.now());
                                }

                                SimpleSQLiteQuery queryIti = new SimpleSQLiteQuery(
                                        geraQueryItinerarios(bairroAnterior.getBairro().getId(), b.getBairro().getId(), todos));

                                List<String> itis = appDatabase.itinerarioDAO()
                                        .carregarOpcoesPorPartidaEDestinoSync(queryIti);

                                String itinerariosDisponiveis = TextUtils.join("','", itis);

                                // NOVO v2.3.x - busca por trecho isolado caso não encontre nas formas anteriores
                                if(itinerariosDisponiveis.isEmpty()){
                                    itinerariosDisponiveis = TextUtils.join("','",
                                            appDatabase.itinerarioDAO().carregarOpcoesPorPartidaEDestinoTrechoSync(bairroAnterior.getBairro().getId(), b.getBairro().getId()));

                                    if(!itinerariosDisponiveis.isEmpty()){
                                        trechoIsolado = true;
                                    } else{
                                        trechoIsolado = false;
                                    }

                                }

                                SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                                        geraQueryResultado(bairroAnterior.getBairro().getId(),
                                                b.getBairro().getId(), diaAnterior, dia, diaSeguinte, hora, itinerariosDisponiveis));

                                ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO()
                                        .carregarPorPartidaEDestinoComHorarioSync(query);

                                if(itinerario != null){
                                    itinerario.setDia(dia);
                                    itinerario.setHora(hora);

                                    itinerario.setTrechoIsolado(trechoIsolado);

                                    if(itinerario.isFlagTrecho()){

                                        List<ParadaItinerario> pis = appDatabase.paradaItinerarioDAO().listarTrechosIntervalo(itinerario.getItinerario().getId(), myPartida.getBairro().getId(),
                                                myDestino.getBairro().getId());

                                        Double total = 0d;
                                        Double distanciaTotal = 0d;
                                        DateTime tempoTotal = new DateTime();

                                        Period per = Period.ZERO;

                                        for(ParadaItinerario pi : pis){

                                            if(pi.getValorSeguinte() != null){
                                                total += pi.getValorSeguinte();
                                            }

                                            if(pi.getDistanciaSeguinte() != null){
                                                distanciaTotal += pi.getDistanciaSeguinte();
                                            }

                                            if(pi.getTempoSeguinte() != null){
                                                String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(pi.getTempoSeguinte().getMillis());
                                                per = per.plus(parser.parsePeriod(tempo));
                                            }

                                        }

                                        tempoTotal = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(printer.print(per.normalizedStandard(PeriodType.time())));

                                        if(total > 0){
                                            itinerario.setTarifaTrecho(total);
                                        }

                                        if(distanciaTotal > 0){
                                            itinerario.setDistanciaTrecho(distanciaTotal);
                                        }

                                        if(tempoTotal.getMinuteOfHour() > 0){
                                            itinerario.setTempoTrecho(tempoTotal);
                                        }

                                        Parada pa = appDatabase.paradaDAO().carregarSync(itinerario.getParadaPartida());
                                        itinerario.setNomePartida(pa.getNome());
                                        itinerario.setIdPartida(pa.getId());

                                    }

                                }

                                if(itinerario == null){

                                    String diaAnt = dia;
                                    String diaAt = diaSeguinte;
                                    String diaSeg = DataHoraUtils.getDiaSeguinte(diaSeguinte);

                                    query = new SimpleSQLiteQuery(
                                            geraQueryResultadoDiaSeguinte(bairroAnterior.getBairro().getId(), b.getBairro().getId(),
                                                    diaAnt, diaAt, diaSeg, itinerariosDisponiveis));

                                    itinerario = appDatabase.itinerarioDAO()
                                            .carregarPorPartidaEDestinoComHorarioSync(query);

                                    if(itinerario != null){
                                        itinerario.setDia(diaAt);
                                        itinerario.setHora(hora);

                                        itinerario.setTrechoIsolado(trechoIsolado);

                                        if(itinerario.isFlagTrecho()){

                                            List<ParadaItinerario> pis = appDatabase.paradaItinerarioDAO().listarTrechosIntervalo(itinerario.getItinerario().getId(), myPartida.getBairro().getId(),
                                                    myDestino.getBairro().getId());

                                            Double total = 0d;
                                            Double distanciaTotal = 0d;
                                            DateTime tempoTotal = new DateTime();

                                            Period per = Period.ZERO;

                                            for(ParadaItinerario pi : pis){

                                                if(pi.getValorSeguinte() != null){
                                                    total += pi.getValorSeguinte();
                                                }

                                                if(pi.getDistanciaSeguinte() != null){
                                                    distanciaTotal += pi.getDistanciaSeguinte();
                                                }

                                                if(pi.getTempoSeguinte() != null){
                                                    String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(pi.getTempoSeguinte().getMillis());
                                                    per = per.plus(parser.parsePeriod(tempo));
                                                }

                                            }

                                            tempoTotal = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(printer.print(per.normalizedStandard(PeriodType.time())));

                                            if(total > 0){
                                                itinerario.setTarifaTrecho(total);
                                            }

                                            if(distanciaTotal > 0){
                                                itinerario.setDistanciaTrecho(distanciaTotal);
                                            }

                                            if(tempoTotal.getMinuteOfHour() > 0){
                                                itinerario.setTempoTrecho(tempoTotal);
                                            }

                                            Parada pa = appDatabase.paradaDAO().carregarSync(itinerario.getParadaPartida());
                                            itinerario.setNomePartida(pa.getNome());
                                            itinerario.setIdPartida(pa.getId());

                                        }
                                    }


                                }

                                if(itinerario != null && itinerario.getProximoHorario() != null){

                                    if(itinerario.getIdHorarioAnterior() != null){
                                        String obsHorarioAnterior = appDatabase.horarioItinerarioDAO()
                                                .carregarObservacaoPorHorario(itinerario.getIdHorarioAnterior());
                                        itinerario.setObservacaoHorarioAnterior(obsHorarioAnterior);
                                    }

                                    if(itinerario.getIdHorarioSeguinte() != null){
                                        String obsHorarioSeguinte = appDatabase.horarioItinerarioDAO()
                                                .carregarObservacaoPorHorario(itinerario.getIdHorarioSeguinte());
                                        itinerario.setObservacaoHorarioSeguinte(obsHorarioSeguinte);
                                    }

                                    if(itinerario.getIdProximoHorario() != null){
                                        String obsProximoHorario = appDatabase.horarioItinerarioDAO()
                                                .carregarObservacaoPorHorario(itinerario.getIdProximoHorario());
                                        itinerario.setObservacaoProximoHorario(obsProximoHorario);
                                    }

                                    if(trechoIsolado){
                                        HorarioItinerarioNome horarioItinerarioAnterior;
                                        HorarioItinerarioNome horarioItinerarioSeguinte;

                                        horarioItinerarioAnterior = appDatabase.horarioItinerarioDAO().carregarPorIdSync(itinerario.getIdHorarioAnterior());
                                        horarioItinerarioSeguinte = appDatabase.horarioItinerarioDAO().carregarPorIdSync(itinerario.getIdHorarioSeguinte());

                                        itinerario.setItinerarioAnterior(appDatabase.itinerarioDAO().carregarSync(horarioItinerarioAnterior.getHorarioItinerario().getItinerario()));
                                        itinerario.setItinerarioSeguinte(appDatabase.itinerarioDAO().carregarSync(horarioItinerarioSeguinte.getHorarioItinerario().getItinerario()));

                                    }

                                    itinerarioAnterior = itinerario;

                                    itinerarios.add(itinerario);
                                    bairroAnterior = b;
                                } else{
                                    // Busca por trecho

                                }


                            } else{
                                bairroAnterior = b;
                            }

                        }

                        resultadosItinerarios.postValue(itinerarios);

                    }
                }

            }
        });



        itinerario = appDatabase.horarioItinerarioDAO()
                .carregarProximoPorPartidaEDestino(myPartida.getBairro().getId(),
                        myDestino.getBairro().getId(), horaEscolhida);
    }

    public void carregaResultadoInvertido(final String horaEscolhida, final String dia, final String diaSeguinte, final String diaAnterior){

//        final BairroCidade bairro = myPartida;
//        myPartida = myDestino;
//        myDestino = bairro;

        setBairroPartida(myPartida);
        setBairroDestino(myDestino);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                PeriodFormatter parser =
                        new PeriodFormatterBuilder()
                                .appendHours().appendLiteral(":")
                                .appendMinutes().appendLiteral(":")
                                .appendSeconds().toFormatter();

                PeriodFormatter printer =
                        new PeriodFormatterBuilder()
                                .printZeroAlways().minimumPrintedDigits(2)
                                //.appendDays().appendLiteral(" dia(s) ")
                                .appendHours().appendLiteral(":")
                                .appendMinutes().appendLiteral(":")
                                .appendSeconds().toFormatter();

                SimpleSQLiteQuery queryOpcoes = new SimpleSQLiteQuery(
                        geraQueryItinerarios(myPartida.getBairro().getId(), myDestino.getBairro().getId(), todos));

                List<String> opcoes = appDatabase.itinerarioDAO()
                        .carregarOpcoesPorPartidaEDestinoSync(queryOpcoes);

                if(opcoes.size() > 0){

                    List<ItinerarioPartidaDestino> itinerarios = new ArrayList<>();

                    String itinerariosDisponiveis = TextUtils.join("','", opcoes);

                    SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                            geraQueryResultado(myPartida.getBairro().getId(),
                                    myDestino.getBairro().getId(), diaAnterior, dia, diaSeguinte, horaEscolhida, itinerariosDisponiveis));

                    ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO()
                            .carregarPorPartidaEDestinoComHorarioSync(query);

                    if(itinerario != null){
                        itinerario.setDia(dia);
                        itinerario.setHora(horaEscolhida);

                        if(itinerario.isFlagTrecho()){

                            List<ParadaItinerario> pis = appDatabase.paradaItinerarioDAO().listarTrechosIntervalo(itinerario.getItinerario().getId(), myPartida.getBairro().getId(),
                                    myDestino.getBairro().getId());

                            Double total = 0d;
                            Double distanciaTotal = 0d;
                            DateTime tempoTotal = new DateTime();

                            Period period = Period.ZERO;

                            for(ParadaItinerario pi : pis){

                                if(pi.getValorSeguinte() != null){
                                    total += pi.getValorSeguinte();
                                }

                                if(pi.getDistanciaSeguinte() != null){
                                    distanciaTotal += pi.getDistanciaSeguinte();
                                }

                                if(pi.getTempoSeguinte() != null){
                                    String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(pi.getTempoSeguinte().getMillis());
                                    period = period.plus(parser.parsePeriod(tempo));
                                }

                            }

                            tempoTotal = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(printer.print(period.normalizedStandard(PeriodType.time())));

                            if(total > 0){
                                itinerario.setTarifaTrecho(total);
                            }

                            if(distanciaTotal > 0){
                                itinerario.setDistanciaTrecho(distanciaTotal);
                            }

                            if(tempoTotal.getMinuteOfHour() > 0){
                                itinerario.setTempoTrecho(tempoTotal);
                            }

                            Parada p = appDatabase.paradaDAO().carregarSync(itinerario.getParadaPartida());
                            itinerario.setNomePartida(p.getNome());
                            itinerario.setIdPartida(p.getId());

                        }

                    }

                    if(itinerario == null){

                        String diaAnt = dia;
                        String diaAt = diaSeguinte;
                        String diaSeg = DataHoraUtils.getDiaSeguinte(diaSeguinte);

                        query = new SimpleSQLiteQuery(
                                geraQueryResultadoDiaSeguinte(myPartida.getBairro().getId(), myDestino.getBairro().getId(),
                                        diaAnt, diaAt, diaSeg, itinerariosDisponiveis));

                        itinerario = appDatabase.itinerarioDAO()
                                .carregarPorPartidaEDestinoComHorarioSync(query);

                        if(itinerario != null){
                            itinerario.setDia(diaAt);
                            itinerario.setHora(horaEscolhida);

                            if(itinerario.isFlagTrecho()){

                                List<ParadaItinerario> pis = appDatabase.paradaItinerarioDAO().listarTrechosIntervalo(itinerario.getItinerario().getId(), myPartida.getBairro().getId(),
                                        myDestino.getBairro().getId());

                                Double total = 0d;
                                Double distanciaTotal = 0d;
                                DateTime tempoTotal = new DateTime();

                                Period period = Period.ZERO;

                                for(ParadaItinerario pi : pis){

                                    if(pi.getValorSeguinte() != null){
                                        total += pi.getValorSeguinte();
                                    }

                                    if(pi.getDistanciaSeguinte() != null){
                                        distanciaTotal += pi.getDistanciaSeguinte();
                                    }

                                    if(pi.getTempoSeguinte() != null){
                                        String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(pi.getTempoSeguinte().getMillis());
                                        period = period.plus(parser.parsePeriod(tempo));
                                    }

                                }

                                tempoTotal = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(printer.print(period.normalizedStandard(PeriodType.time())));

                                if(total > 0){
                                    itinerario.setTarifaTrecho(total);
                                }

                                if(distanciaTotal > 0){
                                    itinerario.setDistanciaTrecho(distanciaTotal);
                                }

                                if(tempoTotal.getMinuteOfHour() > 0){
                                    itinerario.setTempoTrecho(tempoTotal);
                                }

                                Parada pa = appDatabase.paradaDAO().carregarSync(itinerario.getParadaPartida());
                                itinerario.setNomePartida(pa.getNome());
                                itinerario.setIdPartida(pa.getId());

                            }

                        }


                    }

                    if(itinerario != null && itinerario.getProximoHorario() != null){

                        if(itinerario.getIdHorarioAnterior() != null){
                            String obsHorarioAnterior = appDatabase.horarioItinerarioDAO()
                                    .carregarObservacaoPorHorario(itinerario.getIdHorarioAnterior());
                            itinerario.setObservacaoHorarioAnterior(obsHorarioAnterior);
                        }

                        if(itinerario.getIdHorarioSeguinte() != null){
                            String obsHorarioSeguinte = appDatabase.horarioItinerarioDAO()
                                    .carregarObservacaoPorHorario(itinerario.getIdHorarioSeguinte());
                            itinerario.setObservacaoHorarioSeguinte(obsHorarioSeguinte);
                        }

                        if(itinerario.getIdProximoHorario() != null){
                            String obsProximoHorario = appDatabase.horarioItinerarioDAO()
                                    .carregarObservacaoPorHorario(itinerario.getIdProximoHorario());
                            itinerario.setObservacaoProximoHorario(obsProximoHorario);
                        }

                        itinerarios.add(itinerario);

                        resultadosItinerarios.postValue(itinerarios);
                    }

                    // DIJKSTRA - NAO EXISTE LINHA DIRETA ENTRE OS LOCAIS SELECIONADOS. BUSCANDO ALTERNATIVAS

                } else{
                    GraphBuilder<String, Double> builder = GraphBuilder.create();

                    itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosTesteNovoSync();

//                    itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosTesteNovoSync();
//                itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosSync();
//                    itinerarios.addAll(appDatabase.itinerarioDAO().listarTodosAtivosGraphSync());

//                    Collections.sort(itinerarios, new Comparator<ItinerarioPartidaDestino>() {
//                        @Override
//                        public int compare(ItinerarioPartidaDestino itinerarioPartidaDestino, ItinerarioPartidaDestino t1) {
//
//                            if(itinerarioPartidaDestino.isFlagTrecho() && (itinerarioPartidaDestino.getDistanciaTrecho() != null && t1.getDistanciaTrecho() != null)){
//                                return itinerarioPartidaDestino.getDistanciaTrecho().compareTo(t1.getDistanciaTrecho());
//                            } else{
//                                return itinerarioPartidaDestino.getItinerario().getDistancia().compareTo(t1.getItinerario().getDistancia());
//                            }
//
//                        }
//                    });

                    for(ItinerarioPartidaDestino i : itinerarios){

                        if(i.isFlagTrecho()){

                            if(i.getDistanciaTrecho() != null){
                                builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(i.getDistanciaTrecho()+50000);
                            } else{
                                builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(100000d);
                            }


                        } else{

                            if(i.getItinerario().getDistancia() <= 50000){
                                builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(1d);
                            } else{
                                builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino())
                                        .withEdge((i.getItinerario().getDistancia()-50000));
                            }

                        }


                    }

                    HipsterDirectedGraph<String,Double> graph = builder.createDirectedGraph();

                    SearchProblem p = GraphSearchProblem
                            .startingFrom(myPartida.getBairro().getId())
                            .in(graph)
                            .takeCostsFromEdges()
                            .build();

                    Algorithm.SearchResult result = Hipster.createAStar(p).search(myDestino.getBairro().getId());

                    System.out.println("RES: "+result);

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

                                Period period = Period.ZERO;

                                if(itinerarioAnterior != null){
                                    String proximoHorario = itinerarioAnterior.getProximoHorario();

                                    String tempo = "";

                                    if(itinerarioAnterior.getTempoTrecho() != null){
                                        tempo = DateTimeFormat.forPattern("HH:mm:ss").print(itinerarioAnterior.getTempoTrecho().getMillis());
                                    } else{
                                        tempo = DateTimeFormat.forPattern("HH:mm:ss").print(itinerarioAnterior.getItinerario().getTempo().getMillis());
                                    }

                                    period = period.plus(parser.parsePeriod(proximoHorario+":00"));
                                    period = period.plus(parser.parsePeriod(tempo));

                                    hora = printer.print(period.normalizedStandard(PeriodType.time()));

                                } else{
                                    hora = horaEscolhida;//"00:00";//DateTimeFormat.forPattern("HH:mm:00").print(DateTime.now());
                                }

                                SimpleSQLiteQuery queryIti = new SimpleSQLiteQuery(
                                        geraQueryItinerarios(bairroAnterior.getBairro().getId(), b.getBairro().getId(), todos));

                                List<String> itis = appDatabase.itinerarioDAO()
                                        .carregarOpcoesPorPartidaEDestinoSync(queryIti);

                                String itinerariosDisponiveis = TextUtils.join("','", itis);

                                // NOVO v2.3.x - busca por trecho isolado caso não encontre nas formas anteriores
                                if(itinerariosDisponiveis.isEmpty()){
                                    itinerariosDisponiveis = TextUtils.join("','",
                                            appDatabase.itinerarioDAO().carregarOpcoesPorPartidaEDestinoTrechoSync(bairroAnterior.getBairro().getId(), b.getBairro().getId()));

                                    if(!itinerariosDisponiveis.isEmpty()){
                                        trechoIsolado = true;
                                    } else{
                                        trechoIsolado = false;
                                    }

                                }

                                SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                                        geraQueryResultado(bairroAnterior.getBairro().getId(),
                                                b.getBairro().getId(), diaAnterior, dia, diaSeguinte, hora, itinerariosDisponiveis));

                                ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO()
                                        .carregarPorPartidaEDestinoComHorarioSync(query);

                                if(itinerario != null){
                                    itinerario.setDia(dia);
                                    itinerario.setHora(hora);

                                    itinerario.setTrechoIsolado(trechoIsolado);

                                    if(itinerario.isFlagTrecho()){

                                        List<ParadaItinerario> pis = appDatabase.paradaItinerarioDAO().listarTrechosIntervalo(itinerario.getItinerario().getId(), myPartida.getBairro().getId(),
                                                myDestino.getBairro().getId());

                                        Double total = 0d;
                                        Double distanciaTotal = 0d;
                                        DateTime tempoTotal = new DateTime();

                                        Period per = Period.ZERO;

                                        for(ParadaItinerario pi : pis){

                                            if(pi.getValorSeguinte() != null){
                                                total += pi.getValorSeguinte();
                                            }

                                            if(pi.getDistanciaSeguinte() != null){
                                                distanciaTotal += pi.getDistanciaSeguinte();
                                            }

                                            if(pi.getTempoSeguinte() != null){
                                                String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(pi.getTempoSeguinte().getMillis());
                                                per = per.plus(parser.parsePeriod(tempo));
                                            }

                                        }

                                        tempoTotal = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(printer.print(per.normalizedStandard(PeriodType.time())));

                                        if(total > 0){
                                            itinerario.setTarifaTrecho(total);
                                        }

                                        if(distanciaTotal > 0){
                                            itinerario.setDistanciaTrecho(distanciaTotal);
                                        }

                                        if(tempoTotal.getMinuteOfHour() > 0){
                                            itinerario.setTempoTrecho(tempoTotal);
                                        }

                                        Parada pa = appDatabase.paradaDAO().carregarSync(itinerario.getParadaPartida());
                                        itinerario.setNomePartida(pa.getNome());
                                        itinerario.setIdPartida(pa.getId());

                                    }

                                }

                                if(itinerario == null){

                                    String diaAnt = dia;
                                    String diaAt = diaSeguinte;
                                    String diaSeg = DataHoraUtils.getDiaSeguinte(diaSeguinte);

                                    query = new SimpleSQLiteQuery(
                                            geraQueryResultadoDiaSeguinte(bairroAnterior.getBairro().getId(), b.getBairro().getId(),
                                                    diaAnt, diaAt, diaSeg, itinerariosDisponiveis));

                                    itinerario = appDatabase.itinerarioDAO()
                                            .carregarPorPartidaEDestinoComHorarioSync(query);

                                    if(itinerario != null){
                                        itinerario.setDia(diaAt);
                                        itinerario.setHora(hora);

                                        itinerario.setTrechoIsolado(trechoIsolado);

                                        if(itinerario.isFlagTrecho()){

                                            List<ParadaItinerario> pis = appDatabase.paradaItinerarioDAO().listarTrechosIntervalo(itinerario.getItinerario().getId(), myPartida.getBairro().getId(),
                                                    myDestino.getBairro().getId());

                                            Double total = 0d;
                                            Double distanciaTotal = 0d;
                                            DateTime tempoTotal = new DateTime();

                                            Period per = Period.ZERO;

                                            for(ParadaItinerario pi : pis){

                                                if(pi.getValorSeguinte() != null){
                                                    total += pi.getValorSeguinte();
                                                }

                                                if(pi.getDistanciaSeguinte() != null){
                                                    distanciaTotal += pi.getDistanciaSeguinte();
                                                }

                                                if(pi.getTempoSeguinte() != null){
                                                    String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(pi.getTempoSeguinte().getMillis());
                                                    per = per.plus(parser.parsePeriod(tempo));
                                                }

                                            }

                                            tempoTotal = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(printer.print(per.normalizedStandard(PeriodType.time())));

                                            if(total > 0){
                                                itinerario.setTarifaTrecho(total);
                                            }

                                            if(distanciaTotal > 0){
                                                itinerario.setDistanciaTrecho(distanciaTotal);
                                            }

                                            if(tempoTotal.getMinuteOfHour() > 0){
                                                itinerario.setTempoTrecho(tempoTotal);
                                            }

                                            Parada pa = appDatabase.paradaDAO().carregarSync(itinerario.getParadaPartida());
                                            itinerario.setNomePartida(pa.getNome());
                                            itinerario.setIdPartida(pa.getId());

                                        }
                                    }


                                }

                                if(itinerario != null && itinerario.getProximoHorario() != null){

                                    if(itinerario.getIdHorarioAnterior() != null){
                                        String obsHorarioAnterior = appDatabase.horarioItinerarioDAO()
                                                .carregarObservacaoPorHorario(itinerario.getIdHorarioAnterior());
                                        itinerario.setObservacaoHorarioAnterior(obsHorarioAnterior);
                                    }

                                    if(itinerario.getIdHorarioSeguinte() != null){
                                        String obsHorarioSeguinte = appDatabase.horarioItinerarioDAO()
                                                .carregarObservacaoPorHorario(itinerario.getIdHorarioSeguinte());
                                        itinerario.setObservacaoHorarioSeguinte(obsHorarioSeguinte);
                                    }

                                    if(itinerario.getIdProximoHorario() != null){
                                        String obsProximoHorario = appDatabase.horarioItinerarioDAO()
                                                .carregarObservacaoPorHorario(itinerario.getIdProximoHorario());
                                        itinerario.setObservacaoProximoHorario(obsProximoHorario);
                                    }

                                    if(trechoIsolado){
                                        HorarioItinerarioNome horarioItinerarioAnterior;
                                        HorarioItinerarioNome horarioItinerarioSeguinte;

                                        horarioItinerarioAnterior = appDatabase.horarioItinerarioDAO().carregarPorIdSync(itinerario.getHorarioAnterior());
                                        horarioItinerarioSeguinte = appDatabase.horarioItinerarioDAO().carregarPorIdSync(itinerario.getHorarioSeguinte());

                                        itinerario.setItinerarioAnterior(appDatabase.itinerarioDAO().carregarSync(horarioItinerarioAnterior.getHorarioItinerario().getItinerario()));
                                        itinerario.setItinerarioSeguinte(appDatabase.itinerarioDAO().carregarSync(horarioItinerarioSeguinte.getHorarioItinerario().getItinerario()));

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

//    public boolean checaInversao(String partida, String destino){
//        itinerarioResultado = appDatabase.itinerarioDAO()
//                .carregar(itinerario.getValue().getHorarioItinerario().getItinerario());
//    }

    public static String geraQueryItinerarios(String bairroPartida, String bairroDestino, boolean todos){

//        if(todos){
//            return "SELECT i.id FROM itinerario i INNER JOIN " +
//                    "parada_itinerario pi ON pi.itinerario = i.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
//                    "bairro bp ON bp.id = pp.bairro INNER JOIN parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN " +
//                    "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
//                    "WHERE i.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem " +
//                    //"AND ((pi.destaque = 1 OR pi.ordem = 1) AND (pi2.destaque = 1 " +
//                    //"OR pi2.ordem = (SELECT MAX(pi3.ordem) FROM parada_itinerario pi3 WHERE pi3.itinerario = i.id) )) " +
//                    "AND bp.id = '" + bairroPartida + "' AND bd.id = '" + bairroDestino + "' ORDER BY i.id";
//        } else{
//            return "SELECT i.id FROM itinerario i INNER JOIN " +
//                    "parada_itinerario pi ON pi.itinerario = i.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
//                    "bairro bp ON bp.id = pp.bairro INNER JOIN parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN " +
//                    "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
//                    "WHERE i.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem " +
//                    "AND ((pi.destaque = 1 OR pi.ordem = 1) AND (pi2.destaque = 1 " +
//                    "OR pi2.ordem = (SELECT MAX(pi3.ordem) FROM parada_itinerario pi3 WHERE pi3.itinerario = i.id) )) " +
//                    "AND bp.id = '" + bairroPartida + "' AND bd.id = '" + bairroDestino + "' ORDER BY i.id";
//        }

        return "SELECT i.id FROM itinerario i INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem AND pi.ativo = 1 AND pi2.ativo = 1 " +
                "AND ((pi.destaque = 1 OR pi.ordem = 1) AND (pi2.destaque = 1 " +
                "OR pi2.ordem = (SELECT MAX(pi3.ordem) FROM parada_itinerario pi3 WHERE pi3.itinerario = i.id) )) " +
                "AND bp.id = '" + bairroPartida + "' AND bd.id = '" + bairroDestino + "' ORDER BY i.id";


    }

    private String geraQueryResultado(String bairroPartida, String bairroDestino,
                                      String diaAnterior, String dia, String diaSeguinte,
                                      String hora, String itinerariosDisponiveis){
        return "SELECT i.*, " +
                "(" +
                "SELECT pi.valorSeguinte FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem = pi2.ordem-1 AND i2.id = i.id " +
                "AND bp.id = '"+bairroPartida+"' " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id LIMIT 1) AS 'tarifaTrecho',  " +

                "(" +
                "           (" +
                "               SELECT (SELECT pi3.ordem " +
                "                           FROM   parada_itinerario pi3 " +
                "                                  INNER JOIN parada pd2 " +
                "                                          ON pd2.id = pi3.parada " +
                "                                  INNER JOIN bairro bd2 " +
                "                                          ON bd2.id = pd2.bairro " +
                "                           WHERE " +
                "               pi3.itinerario = pi4.itinerario " +
                "               AND bd2.id = '"+bairroDestino+"') < COUNT(*)" +
                "               FROM   parada_itinerario pi4 " +
                "               WHERE  pi4.itinerario = i.id AND pi4.ativo = 1" +
                "            ) OR " +
                "            (" +
                "               SELECT (SELECT pi3.ordem " +
                "                           FROM   parada_itinerario pi3 " +
                "                                  INNER JOIN parada pd2 " +
                "                                          ON pd2.id = pi3.parada " +
                "                                  INNER JOIN bairro bd2 " +
                "                                          ON bd2.id = pd2.bairro " +
                "                           WHERE " +
                "               pi3.itinerario = pi4.itinerario " +
                "               AND bd2.id = '"+bairroPartida+"') > 1 " +
                "               FROM   parada_itinerario pi4 " +
                "               WHERE  pi4.itinerario = i.id AND pi4.ativo = 1" +
                "            )" +
                "        ) AS 'flagTrecho',  " +

                "(" +
                "SELECT pi.distanciaSeguinte FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem = pi2.ordem-1 AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bp.id = '"+bairroPartida+"' " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id) AS 'distanciaTrecho', " +

                "(" +
                "SELECT pi.tempoSeguinte FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem = pi2.ordem-1 AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bp.id = '"+bairroPartida+"' " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id) AS 'tempoTrecho', " +

                "(SELECT pp.id FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN " +
                "parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bp.id = '"+bairroPartida+"' " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id, pi.ordem) AS 'paradaPartida', " +

                "(SELECT pd.id FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN " +
                "parada pp ON pp.id = pi.parada INNER JOIN bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bp.id = '"+bairroPartida+"' " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id ASC, pi2.ordem DESC) AS 'paradaDestino', " +

                "'"+bairroPartida+"' AS 'bairroConsultaPartida', " +
                "'"+bairroDestino+"' AS 'bairroConsultaDestino'," +
                " e.nome AS 'nomeEmpresa', " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) as 'proximoHorario', " +

                "IFNULL( ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "                WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"'" +
                "                ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario" +
                "                WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) as 'idProximoHorario', " +

                "IFNULL((SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome DESC LIMIT 1) , " +
                "( SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'horarioAnterior', " +

                "IFNULL( (SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                "( SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'idHorarioAnterior', " +

                "IFNULL( (SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                "( SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'obsHorarioAnterior', " +

                "IFNULL( (SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                "( SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'observacaoHorarioAnterior', " +

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

                "IFNULL( (SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome LIMIT 1 ) , " +
                "( SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + diaSeguinte + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome LIMIT 1 ) ) AS obsHorarioSeguinte, " +

                "IFNULL( (SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome LIMIT 1 ) , " +
                "( SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaSeguinte + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome LIMIT 1 ) ) AS observacaoHorarioSeguinte, " +

                "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) " +
                "AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idPartida', " +

                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) " +
                "AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'nomePartida', " +

                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'nomeDestino', " +

                "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +

                "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idBairroDestino', " +

                "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'bairroPartida', " +

                "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'bairroDestino', " +

                "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'cidadePartida', " +

                "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) " +
                "AND pi.itinerario = i.id AND pi.ativo = 1      ) AS 'cidadeDestino' " +

                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario INNER JOIN " +
                "itinerario i ON i.id = hi.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
                "WHERE hi.itinerario IN ('" + itinerariosDisponiveis + "') AND " + dia + " = 1 " +
                "AND strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) > '" + hora + "' ORDER BY proximoHorario LIMIT 1";
    }

    private String geraQueryResultadoDiaSeguinte(String bairroPartida, String bairroDestino,
                                                 String diaAnt, String diaAt, String diaSeg, String itinerariosDisponiveis){
        return "SELECT i.*, " +
                "(" +
                "SELECT pi.valorSeguinte FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem = pi2.ordem-1 AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bp.id = '"+bairroPartida+"' " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id) AS 'tarifaTrecho', " +

                "(" +
                "           (" +
                "               SELECT (SELECT pi3.ordem " +
                "                           FROM   parada_itinerario pi3 " +
                "                                  INNER JOIN parada pd2 " +
                "                                          ON pd2.id = pi3.parada " +
                "                                  INNER JOIN bairro bd2 " +
                "                                          ON bd2.id = pd2.bairro " +
                "                           WHERE " +
                "               pi3.itinerario = pi4.itinerario " +
                "               AND bd2.id = '"+bairroDestino+"') < COUNT(*)" +
                "               FROM   parada_itinerario pi4 " +
                "               WHERE  pi4.itinerario = i.id AND pi4.ativo = 1" +
                "            ) OR " +
                "            (" +
                "               SELECT (SELECT pi3.ordem " +
                "                           FROM   parada_itinerario pi3 " +
                "                                  INNER JOIN parada pd2 " +
                "                                          ON pd2.id = pi3.parada " +
                "                                  INNER JOIN bairro bd2 " +
                "                                          ON bd2.id = pd2.bairro " +
                "                           WHERE " +
                "               pi3.itinerario = pi4.itinerario " +
                "               AND bd2.id = '"+bairroPartida+"') > 1 " +
                "               FROM   parada_itinerario pi4 " +
                "               WHERE  pi4.itinerario = i.id AND pi4.ativo = 1" +
                "            )" +
                "        ) AS 'flagTrecho',  " +

                "(" +
                "SELECT pi.distanciaSeguinte FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem = pi2.ordem-1 AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bp.id = '"+bairroPartida+"' " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id) AS 'distanciaTrecho', " +

                "(" +
                "SELECT pi.tempoSeguinte FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem = pi2.ordem-1 AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bp.id = '"+bairroPartida+"' " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id) AS 'tempoTrecho', " +

                "(SELECT pp.id FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN " +
                "parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bp.id = '"+bairroPartida+"' " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id) AS 'paradaPartida', " +

                "(SELECT pd.id FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN " +
                "parada pp ON pp.id = pi.parada INNER JOIN bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bp.id = '"+bairroPartida+"' " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id) AS 'paradaDestino', " +

                "'"+bairroPartida+"' AS 'bairroConsultaPartida', " +
                "'"+bairroDestino+"' AS 'bairroConsultaDestino', " +
                "e.nome AS 'nomeEmpresa', " +

                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) as 'proximoHorario', " +

                "h.id as 'idProximoHorario', " +

                "IFNULL((SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
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

                "IFNULL( (SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                "( SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + diaAnt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'obsHorarioAnterior', " +

                "IFNULL( (SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                "( SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaAnt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'observacaoHorarioAnterior', " +

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

                "IFNULL( (SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome LIMIT 1 ) , " +
                "( SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + diaSeg + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome LIMIT 1 ) ) AS obsHorarioSeguinte, " +

                "IFNULL( (SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaAt + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome LIMIT 1 ) , " +
                "( SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaSeg + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome LIMIT 1 ) ) AS observacaoHorarioSeguinte, " +

                "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) " +
                "AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idPartida', " +
                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) " +
                "AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'nomePartida', " +
                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'nomeDestino', " +
                "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idBairroPartida', " +
                "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idBairroDestino', " +
                "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'bairroPartida', " +
                "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'bairroDestino', " +
                "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'cidadePartida', " +
                "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) " +
                "AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'cidadeDestino' " +
                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario INNER JOIN " +
                "itinerario i ON i.id = hi.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
                "WHERE hi.itinerario IN ('" + itinerariosDisponiveis + "') AND " + diaAt + " = 1 ORDER BY proximoHorario LIMIT 1";
    }

    private String geraQueryResultadoConsulta(String bairroDestino,
                                      String diaAnterior, String dia, String diaSeguinte,
                                      String hora, String itinerariosDisponiveis){
        return "SELECT i.*, " +
                "(" +
                "SELECT pi.valorSeguinte FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem = pi2.ordem-1 AND i2.id = i.id " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id LIMIT 1) AS 'tarifaTrecho',  " +

                "(" +
                "           (" +
                "               SELECT (SELECT pi3.ordem " +
                "                           FROM   parada_itinerario pi3 " +
                "                                  INNER JOIN parada pd2 " +
                "                                          ON pd2.id = pi3.parada " +
                "                                  INNER JOIN bairro bd2 " +
                "                                          ON bd2.id = pd2.bairro " +
                "                           WHERE " +
                "               pi3.itinerario = pi4.itinerario " +
                "               AND bd2.id = '"+bairroDestino+"') < COUNT(*)" +
                "               FROM   parada_itinerario pi4 " +
                "               WHERE  pi4.itinerario = i.id AND pi4.ativo = 1" +
                "            ) OR " +
                "            (" +
                "               SELECT (SELECT pi3.ordem " +
                "                           FROM   parada_itinerario pi3 " +
                "                                  INNER JOIN parada pd2 " +
                "                                          ON pd2.id = pi3.parada " +
                "                                  INNER JOIN bairro bd2 " +
                "                                          ON bd2.id = pd2.bairro " +
                "                           WHERE " +
                "               pi3.itinerario = pi4.itinerario " +
                "               AND bd2.id = '"+bairroPartida+"') > 1 " +
                "               FROM   parada_itinerario pi4 " +
                "               WHERE  pi4.itinerario = i.id AND pi4.ativo = 1" +
                "            )" +
                "        ) AS 'flagTrecho',  " +

                "(" +
                "SELECT pi.distanciaSeguinte FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem = pi2.ordem-1 AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id) AS 'distanciaTrecho', " +

                "(" +
                "SELECT pi.tempoSeguinte FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem = pi2.ordem-1 AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id) AS 'tempoTrecho', " +

                "(SELECT pp.id FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN " +
                "parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id, pi.ordem) AS 'paradaPartida', " +

                "(SELECT pd.id FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN " +
                "parada pp ON pp.id = pi.parada INNER JOIN bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem AND pi.ativo = 1 AND pi2.ativo = 1 AND i2.id = i.id " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id ASC, pi2.ordem DESC) AS 'paradaDestino', " +

                "'' AS 'bairroConsultaPartida', " +
                "'"+bairroDestino+"' AS 'bairroConsultaDestino'," +
                " e.nome AS 'nomeEmpresa', " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) as 'proximoHorario', " +

                "IFNULL( ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "                WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"'" +
                "                ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario" +
                "                WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) as 'idProximoHorario', " +

                "IFNULL((SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome DESC LIMIT 1) , " +
                "( SELECT strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) " +
                "FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'horarioAnterior', " +

                "IFNULL( (SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                "( SELECT hi2.id FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'idHorarioAnterior', " +

                "IFNULL( (SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                "( SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'obsHorarioAnterior', " +

                "IFNULL( (SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) < " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome DESC LIMIT 1 ) , " +
                "( SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaAnterior + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome DESC LIMIT 1 ) ) AS 'observacaoHorarioAnterior', " +

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

                "IFNULL( (SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome LIMIT 1 ) , " +
                "( SELECT i2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario INNER JOIN " +
                "itinerario i2 ON i2.id = hi2.itinerario " +
                "WHERE hi2." + diaSeguinte + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome LIMIT 1 ) ) AS obsHorarioSeguinte, " +

                "IFNULL( (SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + dia + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "AND strftime('%H:%M', TIME(h2.nome/1000, 'unixepoch', 'localtime')) > " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
                "ORDER BY h2.nome LIMIT 1 ) , " +
                "( SELECT hi2.observacao FROM horario_itinerario hi2 INNER JOIN horario h2 ON h2.id = hi2.horario " +
                "WHERE hi2." + diaSeguinte + " = 1 AND hi2.itinerario IN ('" + itinerariosDisponiveis + "') " +
                "ORDER BY h2.nome LIMIT 1 ) ) AS observacaoHorarioSeguinte, " +

                "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) " +
                "AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idPartida', " +

                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) " +
                "AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'nomePartida', " +

                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'nomeDestino', " +

                "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +

                "( SELECT b.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idBairroDestino', " +

                "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'bairroPartida', " +

                "( SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'bairroDestino', " +

                "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario " +
                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'cidadePartida', " +

                "( SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) " +
                "AND pi.itinerario = i.id AND pi.ativo = 1      ) AS 'cidadeDestino' " +

                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario INNER JOIN " +
                "itinerario i ON i.id = hi.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
                "WHERE hi.itinerario IN ('" + itinerariosDisponiveis + "') AND " + dia + " = 1 " +
                "AND strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) > '" + hora + "' ORDER BY proximoHorario LIMIT 1";
    }

    private String geraQueryTarifaTrecho(String bairroPartida, String bairroDestino, String itinerario){
        return "SELECT 7.3 FROM itinerario i2 INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i2.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN " +
                "parada_itinerario pi2 ON pi2.itinerario = i2.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem AND i2.id = '"+itinerario+"' " +
                "AND bp.id = '"+bairroPartida+"' " +
                "AND bd.id = '"+bairroDestino+"' ORDER BY i2.id LIMIT 1";
    }

}
