package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.sqlite.db.SimpleSQLiteQuery;
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
import java.util.function.Consumer;

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

        if(bairroDestino != null){
            this.bairroDestino = appDatabase.bairroDAO().carregar(bairroDestino.getBairro().getId());
        }

    }

    public void setBairroDestinoConsulta(BairroCidade bairroDestino) {
        this.bairroDestinoConsulta = appDatabase.bairroDAO().carregar(bairroDestino.getBairro().getId());
    }

    public LiveData<BairroCidade> getBairroPartida() {
        return bairroPartida;
    }

    public void setBairroPartida(BairroCidade umBairroPartida) {

        if(umBairroPartida != null && (this.bairroPartida.getValue() == null || this.bairroPartida.getValue().getBairro().getId() != umBairroPartida.getBairro().getId())){
            this.bairroPartida = appDatabase.bairroDAO().carregar(umBairroPartida.getBairro().getId());

            this.cidadesDestino = appDatabase.cidadeDAO().listarTodosAtivasComEstadoFiltro(umBairroPartida.getBairro().getId());
        }

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

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                iniciaTabelaTemporaria();
            }
        });

        localAtual = new MutableLiveData<>();
        localAtual.postValue(new Location(LocationManager.GPS_PROVIDER));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());
    }

    private void iniciaTabelaTemporaria() {
        SimpleSQLiteQuery queryTemp = new SimpleSQLiteQuery(geraTabelaTemp());
        appDatabase.itinerarioDAO().geraTabelaTemp(queryTemp);

        appDatabase.itinerarioDAO().deletaTabelaTemp(new SimpleSQLiteQuery("DELETE FROM tpr"));

        SimpleSQLiteQuery queryPopula = new SimpleSQLiteQuery(populaTabelaTemp());
        appDatabase.itinerarioDAO().populaTabelaTemp(queryPopula);
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

        final SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT i.*, " +
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

                "( SELECT b.nome FROM parada pp INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pp.id = i.paradaInicial ) AS 'bairroPartida', " +

                "( SELECT b.nome FROM parada pp INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pp.id = i.paradaFinal ) AS 'bairroDestino', " +

                "( SELECT c.nome FROM parada pp INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pp.id = i.paradaInicial ) AS 'cidadePartida', " +

                "( SELECT c.nome FROM parada pp INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pp.id = i.paradaFinal ) AS 'cidadeDestino' " +

                "FROM itinerario i WHERE i.sigla LIKE '"+linha+"' AND i.ativo = 1 " +
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
                                geraQueryResultadoConsulta(bairro, diaAnterior, finalDia, diaSeguinte, hora, iti.split(";")[0]));

                        List<ItinerarioPartidaDestino> itins = appDatabase.itinerarioDAO()
                                .carregarPorDestinoComHorarioSync(query);

                        ItinerarioPartidaDestino itinerario = null;

                        ordenaPorHorario(itins);

                        if(itins.size() > 0){
                            itinerario = itins.get(0);
                        }

                        if(itinerario != null){
                            itinerario.setDistanciaPoi(1f);
                        }

                        if(itinerario == null){

                            query = new SimpleSQLiteQuery(
                                    geraQueryResultadoConsulta(bairro, finalDia, diaSeguinte,
                                            DataHoraUtils.getDiaSeguinte(diaSeguinte), "00:00", iti.split(";")[0]));

                             itins = appDatabase.itinerarioDAO()
                                    .carregarPorDestinoComHorarioSync(query);

                            ordenaPorHorario(itins);

                            if(itins.size() > 0){
                                itinerario = itins.get(0);
                            }

                            if(itinerario != null){
                                itinerario.setDistanciaPoi(2f);
                            }

                        }

                        if(itinerario != null){
                            itis.add(itinerario);
                        }

                    }

                    Collections.sort(itis, new Comparator<ItinerarioPartidaDestino>() {
                        @Override
                        public int compare(ItinerarioPartidaDestino itinerarioPartidaDestino, ItinerarioPartidaDestino t1) {

                            if((itinerarioPartidaDestino != null && itinerarioPartidaDestino.getProximoHorario() != null) &&
                                    (t1 != null && t1.getProximoHorario() != null)){

                                int comp1 = itinerarioPartidaDestino.getDistanciaPoi().compareTo(t1.getDistanciaPoi());

                                if(comp1 != 0){
                                    return comp1;
                                }

                                return DateTimeFormat.forPattern("HH:mm").parseLocalTime(itinerarioPartidaDestino.getProximoHorario())
                                        .compareTo(DateTimeFormat.forPattern("HH:mm").parseLocalTime(t1.getProximoHorario()));

                            } else{
                                return -1;
                            }

                        }
                    });

                    destacaItinerario = false;

                } else{
                    itinerariosDisponiveis = appDatabase.itinerarioDAO()
                            .carregarOpcoesPorPartidaEDestinoTrechoConsultaSync(bairro, bairroAtual);

                    itis = new ArrayList<>();

                    String itinerarioDestaque = itinerariosDisponiveis.get(0).split(";")[0];

                    ItinerarioPartidaDestino destaque = new ItinerarioPartidaDestino();
                    destaque.getItinerario().setId(itinerarioDestaque);

                    for(String iti : itinerariosDisponiveis){
                        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                                geraQueryResultadoConsulta(bairro, diaAnterior, finalDia, diaSeguinte, hora, iti.split(";")[0]));

                        List<ItinerarioPartidaDestino> itins = appDatabase.itinerarioDAO()
                                .carregarPorDestinoComHorarioSync(query);

                        ItinerarioPartidaDestino itinerario = null;

                        ordenaPorHorario(itins);

                        if(itins.size() > 0){
                            itinerario = itins.get(0);
                        }

                        if(itinerario != null){
                            itinerario.setDistanciaPoi(1f);
                        }

                        if(itinerario == null){
                            query = new SimpleSQLiteQuery(
                                    geraQueryResultadoConsulta(bairro, finalDia, diaSeguinte,
                                            DataHoraUtils.getDiaSeguinte(diaSeguinte), "00:00", iti.split(";")[0]));

                            itins = appDatabase.itinerarioDAO()
                                    .carregarPorDestinoComHorarioSync(query);

                            ordenaPorHorario(itins);

                            if(itins.size() > 0){
                                itinerario = itins.get(0);
                            }

                            if(itinerario != null){
                                itinerario.setDistanciaPoi(2f);
                            }

                        }

                        if(itinerario != null){
                            itis.add(itinerario);
                        }
                    }

                    Collections.sort(itis, new Comparator<ItinerarioPartidaDestino>() {
                        @Override
                        public int compare(ItinerarioPartidaDestino itinerarioPartidaDestino, ItinerarioPartidaDestino t1) {

                            if((itinerarioPartidaDestino != null && itinerarioPartidaDestino.getProximoHorario() != null) &&
                                    (t1 != null && t1.getProximoHorario() != null)){

                                int comp1 = itinerarioPartidaDestino.getDistanciaPoi().compareTo(t1.getDistanciaPoi());

                                if(comp1 != 0){
                                    return comp1;
                                }

                                return DateTimeFormat.forPattern("HH:mm").parseLocalTime(itinerarioPartidaDestino.getProximoHorario())
                                        .compareTo(DateTimeFormat.forPattern("HH:mm").parseLocalTime(t1.getProximoHorario()));

                            } else{
                                return -1;
                            }

                        }
                    });

                    int index = itis.indexOf(destaque);
                    int lastIndex = itis.lastIndexOf(destaque);

                    destaque = itis.get(index);

                    if(destaque.getIdBairroPartida() != null && destaque.getIdBairroPartida().equals(bairroAtual)){

                        if(lastIndex != index){
                            itis.remove(lastIndex);
                        }

                        if(index > 0){
                            itis.remove(destaque);

                            itis.add(0, destaque);
                        }

                        destacaItinerario = true;
                    } else{
                        destacaItinerario = false;
                    }



                }

                itinerariosPorDestino.postValue(itis);

            }
        });


    }

    private void ordenaPorHorario(List<ItinerarioPartidaDestino> itins) {
        Collections.sort(itins, new Comparator<ItinerarioPartidaDestino>() {
            @Override
            public int compare(ItinerarioPartidaDestino itinerarioPartidaDestino, ItinerarioPartidaDestino t1) {

                if((itinerarioPartidaDestino != null && itinerarioPartidaDestino.getProximoHorario() != null) &&
                        (t1 != null && t1.getProximoHorario() != null)){

                    return DateTimeFormat.forPattern("HH:mm").parseLocalTime(itinerarioPartidaDestino.getProximoHorario())
                            .compareTo(DateTimeFormat.forPattern("HH:mm").parseLocalTime(t1.getProximoHorario()));

                } else{
                    return -1;
                }

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

    public void carregaResultadoNovo(final String horaEscolhida, final String dia, final String diaSeguinte,
                                     final String diaAnterior, boolean inversao){

        if(!inversao){
            // setando partida e destino da consulta
            if(bairroPartida.getValue() != null){
                myPartida = bairroPartida.getValue();
            }

            if(bairroDestino.getValue() != null){
                myDestino = bairroDestino.getValue();
            }
        }

        final PeriodFormatter parser =
                new PeriodFormatterBuilder()
                        .appendHours().appendLiteral(":")
                        .appendMinutes().appendLiteral(":")
                        .appendSeconds().toFormatter();

        final PeriodFormatter printer =
                new PeriodFormatterBuilder()
                        .printZeroAlways().minimumPrintedDigits(2)
                        //.appendDays().appendLiteral(" dia(s) ")
                        .appendHours().appendLiteral(":")
                        .appendMinutes().appendLiteral(":")
                        .appendSeconds().toFormatter();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                String diaDaConsultaInicial = dia;
                String diaDaConsulta = dia;

                DateTime horaDaConsulta = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(horaEscolhida);

                // busca por itinerario - opcoes que contemplem a partida e o destino escolhidos
                SimpleSQLiteQuery queryOpcoes = new SimpleSQLiteQuery(
                        geraQueryItinerarios(myPartida.getBairro().getId(), myDestino.getBairro().getId(), todos));

                List<String> opcoes = appDatabase.itinerarioDAO()
                        .carregarOpcoesPorPartidaEDestinoSync(queryOpcoes);

                // ha uma ou mais opcoes de itinerario direto
                if(opcoes.size() > 0){
                    List<ItinerarioPartidaDestino> itinerarios = new ArrayList<>();
                    String itinerariosDisponiveis = TextUtils.join("','", opcoes);

                    SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                            geraQueryResultado(myPartida.getBairro().getId(),
                                    myDestino.getBairro().getId(), diaAnterior, dia, diaSeguinte, horaEscolhida, itinerariosDisponiveis));

                    ItinerarioPartidaDestino itinerario = ordenaConsultaProximoHorario(query);

                    if(itinerario != null){
                        //ha proximo horario disponivel no mesmo dia da consulta
                        itinerario = processaProximoHorario(itinerario, dia, horaEscolhida, parser, printer);
                    } else{
                        // nao ha itinerario com horario disponivel - busca no proximo dia, o primeiro horario
                        String diaAnt = dia;
                        String diaAt = diaSeguinte;
                        String diaSeg = DataHoraUtils.getDiaSeguinte(diaSeguinte);

                        query = new SimpleSQLiteQuery(
                                geraQueryResultado(myPartida.getBairro().getId(), myDestino.getBairro().getId(),
                                        diaAnt, diaAt, diaSeg, "00:00:00", itinerariosDisponiveis));

                        itinerario = ordenaConsultaProximoHorario(query);

                        if(itinerario != null){
                            //processando horario do dia seguinte
                            itinerario = processaProximoHorario(itinerario, diaAt, horaEscolhida, parser, printer);
                        }

                    }

                    if(itinerario != null && itinerario.getProximoHorario() != null){
                        itinerarios.add(itinerario);
                        resultadosItinerarios.postValue(itinerarios);
                    }

                } else{
                    //nao ha opcoes diretas de itinerario - realizando a busca por trechos com o algoritmo A-Star
                    GraphBuilder<String, Double> builder = GraphBuilder.create();

                    //testa e popula tabela temporaria para consulta
                    verificaTabelaTemporaria();

                    SimpleSQLiteQuery queryConsulta = new SimpleSQLiteQuery(consultaTabelaTempSimplificado());

                    itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosTesteNovoSync(queryConsulta);

                    //preparando algoritmo de busca
                    Algorithm.SearchResult result = preparaAStar(builder);
                    List<List> caminhos = result.getOptimalPaths();

                    //processando caminho encontrado
                    for(List<List> caminho : caminhos){
                        int cont = caminho.size();
                        List<BairroCidade> passos = new ArrayList<>();

                        //bairros por onde o caminho passa
                        for(int i = 0; i < cont; i++){
                            BairroCidade bairro = appDatabase.bairroDAO().carregarSync(String.valueOf(caminho.get(i)));
                            passos.add(bairro);
                        }

                        BairroCidade bairroAnterior = null;
                        ItinerarioPartidaDestino itinerarioAnterior = null;
                        List<ItinerarioPartidaDestino> itinerarios = new ArrayList<>();
                        boolean virouDia = false;
                        int contDias = 0;

                        for(BairroCidade b : passos){

                            if(bairroAnterior == null){
                                //primeiro passo
                                bairroAnterior = b;
                            } else{
                                //passo apos o primeiro
                                String hora = "";
                                Period period = Period.ZERO;

                                //se ja houver itinerario carregado, soma tempo de viagem para pesquisar saida do proximo destino
                                if(itinerarioAnterior != null){
                                    String proximoHorario = itinerarioAnterior.getProximoHorario()+":00";
                                    String tempo = "";

                                    //se for trecho, pega o tempo do trecho. Senao, pega o do itinerario completo
                                    if(itinerarioAnterior.getTempoTrecho() != null){
                                        tempo = DateTimeFormat.forPattern("HH:mm:ss").print(itinerarioAnterior.getTempoTrecho().getMillis());
                                    } else{
                                        tempo = DateTimeFormat.forPattern("HH:mm:ss").print(itinerarioAnterior.getItinerario().getTempo().getMillis());
                                    }

                                    //soma proxima partida ao tempo de viagem para fazer pesquisa do proximo itinerario
                                    period = period.plus(parser.parsePeriod(proximoHorario));
                                    period = period.plus(parser.parsePeriod(tempo));

                                    if(period.getMinutes() > 59){
                                        virouDia = true;
                                        contDias++;

                                        if(period.getHours() < 23){
                                            period = period.plusHours(1);
                                        } else{
                                            period = period.minusHours(period.getHours());
                                        }

                                        period = period.minusMinutes(60);
                                    } else if(period.getHours() > 23){
                                        virouDia = true;
                                        contDias++;

                                        period = period.minusHours(24);
                                    }

                                    hora = printer.print(period.normalizedStandard(PeriodType.time()));

                                } else{
                                    //se ainda nao tiver itinerario, usa a hora escolhida pelo usuario, se houver, ou a hora atual
                                    hora = horaEscolhida;
                                }

                                //busca proximo itinerario do caminho
                                // NOVO v2.3.x - busca por trecho isolado caso não encontre nas formas anteriores
                                String itinerariosDisponiveis = TextUtils.join("','",
                                        appDatabase.itinerarioDAO().carregarOpcoesPorPartidaEDestinoTrechoSync(bairroAnterior.getBairro().getId(), b.getBairro().getId()));

                                if(!itinerariosDisponiveis.isEmpty()){
                                    trechoIsolado = true;
                                } else{
                                    trechoIsolado = false;
                                }

                                SimpleSQLiteQuery query;

                                String diaAnteriorVirado = diaAnterior;
                                String diaVirado = dia;
                                String diaSeguinteVirado = diaSeguinte;

                                if(virouDia){

                                    for(int i = 0; i < contDias; i++){
                                        // passando para o(s) dia(s) seguinte(s)
                                        diaAnteriorVirado = DataHoraUtils.getDiaSeguinte(diaAnteriorVirado);
                                        diaVirado = DataHoraUtils.getDiaSeguinte(diaVirado);
                                        diaSeguinteVirado = DataHoraUtils.getDiaSeguinte(diaSeguinteVirado);

                                        diaDaConsulta = diaVirado;
                                    }

                                    query = new SimpleSQLiteQuery(
                                            geraQueryResultado(bairroAnterior.getBairro().getId(),
                                                    b.getBairro().getId(), diaAnteriorVirado,
                                                    diaVirado, diaSeguinteVirado, hora, itinerariosDisponiveis));
                                } else{
                                    query = new SimpleSQLiteQuery(
                                            geraQueryResultado(bairroAnterior.getBairro().getId(),
                                                    b.getBairro().getId(), diaAnterior, dia, diaSeguinte, hora, itinerariosDisponiveis));
                                }

                                ItinerarioPartidaDestino itinerario = ordenaConsultaProximoHorario(query);

                                if(itinerario != null){
                                    //existe trecho de itinerario que atenda
                                    itinerario.setTrechoIsolado(trechoIsolado);

                                    if(checaHoraDiaSeguinte(horaDaConsulta, hora) && diaDaConsulta == diaDaConsultaInicial){
                                        diaDaConsulta = DataHoraUtils.getDiaSeguinte(diaDaConsulta);
                                    }

                                    itinerario = processaProximoHorario(itinerario, diaDaConsulta, hora, parser, printer);

                                } else{
                                    //nao existe trecho de itinerario que atenda - buscando no dia seguinte

                                    String diaAnt = "";
                                    String diaAt = "";
                                    String diaSeg = "";

                                    if(virouDia){
                                        diaAnt = diaVirado;
                                        diaAt = diaSeguinteVirado;
                                        diaSeg = DataHoraUtils.getDiaSeguinte(diaSeguinteVirado);
                                    } else{
                                        diaAnt = dia;
                                        diaAt = diaSeguinte;
                                        diaSeg = DataHoraUtils.getDiaSeguinte(diaSeguinte);
                                    }

                                    query = new SimpleSQLiteQuery(
                                            geraQueryResultadoDiaSeguinte(bairroAnterior.getBairro().getId(), b.getBairro().getId(),
                                                    diaAnt, diaAt, diaSeg, itinerariosDisponiveis));

                                    itinerario = ordenaConsultaProximoHorario(query);

                                    if(itinerario != null){
                                        itinerario.setTrechoIsolado(trechoIsolado);

                                        itinerario = processaProximoHorario(itinerario, diaDaConsulta, hora, parser, printer);
                                    }

                                }

                                if(itinerario != null && itinerario.getProximoHorario() != null){

                                    if(trechoIsolado){
                                        HorarioItinerarioNome horarioItinerarioAnterior;
                                        HorarioItinerarioNome horarioItinerarioSeguinte;

                                        horarioItinerarioAnterior = appDatabase.horarioItinerarioDAO().carregarPorIdSync(itinerario.getIdHorarioAnterior());
                                        horarioItinerarioSeguinte = appDatabase.horarioItinerarioDAO().carregarPorIdSync(itinerario.getIdHorarioSeguinte());

                                        if(horarioItinerarioAnterior != null){
                                            itinerario.setItinerarioAnterior(appDatabase.itinerarioDAO()
                                                    .carregarSync(horarioItinerarioAnterior.getHorarioItinerario().getItinerario()));
                                        }

                                        if(horarioItinerarioSeguinte != null){
                                            itinerario.setItinerarioSeguinte(appDatabase.itinerarioDAO()
                                                    .carregarSync(horarioItinerarioSeguinte.getHorarioItinerario().getItinerario()));
                                        }

                                    }

                                    itinerarioAnterior = itinerario;

                                    //adiciona trecho a lista de itinerarios do caminho encontrado
                                    itinerarios.add(itinerario);
                                    bairroAnterior = b;
                                }

                            }

                        }
                        resultadosItinerarios.postValue(itinerarios);
                    }

                }

            }
        });

    }

    private boolean checaHoraDiaSeguinte(DateTime horaDaConsulta, String hora) {
        DateTime horaConsulta = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(hora);

        return horaConsulta.isBefore(horaDaConsulta);
    }

    private Algorithm.SearchResult preparaAStar(GraphBuilder<String, Double> builder) {

        for(ItinerarioPartidaDestino i : itinerarios){

            if(i.isFlagTrecho()){

                //aumentando distancias dos trechos para priorizar itinerarios 'inteiros' ao inves de 'partidos'
                if(i.getDistanciaTrechoMetros() != null){
                    Double soma = i.getDistanciaTrechoMetros().doubleValue()+50000;
                    builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(soma);
                } else{
                    builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(100000d);
                }

            } else{
                //itinerario 'inteiro' menor que 50Km - prioridade maxima na busca. Diminuindo distancia
                if(i.getItinerario() != null && i.getItinerario().getDistanciaMetros() != null && i.getItinerario().getDistanciaMetros() <= 50000){
                    builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino()).withEdge(1d);
                } else{
                    //usando distancia real do itinerario
                    if(i.getItinerario() != null && i.getItinerario().getDistanciaMetros() != null){
                        builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino())
                                .withEdge(i.getItinerario().getDistanciaMetros());
                    } else{
                        //excecao aos filtros acima - prioridade minima
                        builder.connect(i.getIdBairroPartida()).to(i.getIdBairroDestino())
                                .withEdge(100000d);
                    }

                }

            }
        }

        HipsterDirectedGraph<String,Double> graph = builder.createDirectedGraph();

        SearchProblem p = GraphSearchProblem
                .startingFrom(myPartida.getBairro().getId())
                .in(graph)
                .takeCostsFromEdges()
                .build();

        //buscando melhor caminho
        return Hipster.createAStar(p).search(myDestino.getBairro().getId());

    }

    private void verificaTabelaTemporaria() {
        List<ItinerarioPartidaDestino> rows = appDatabase.itinerarioDAO().testarTabelaTemp(new SimpleSQLiteQuery("SELECT * FROM tpr"));

        if (rows.size() == 0 || rows.get(0) == null) {
            appDatabase.itinerarioDAO().deletaTabelaTemp(new SimpleSQLiteQuery("DELETE FROM tpr"));

            SimpleSQLiteQuery queryPopula = new SimpleSQLiteQuery(populaTabelaTemp());
            appDatabase.itinerarioDAO().populaTabelaTemp(queryPopula);
        }
    }

    private ItinerarioPartidaDestino processaProximoHorario(ItinerarioPartidaDestino itinerario, String dia, String horaEscolhida, PeriodFormatter parser, PeriodFormatter printer) {
        // ha itinerario com horario disponivel
        itinerario.setDia(dia);
        itinerario.setHora(horaEscolhida);

        //se for trecho, pega os dados respectivos (tarifa, distancia, etc)
        if(itinerario.isFlagTrecho()) {
            //carrega todas as paradas, excetuando as do bairro de partida e de destino
            List<ParadaItinerario> pis = appDatabase.paradaItinerarioDAO().listarTrechosIntervalo(itinerario.getItinerario().getId(), myPartida.getBairro().getId(),
                    myDestino.getBairro().getId());

            Double total = 0d;
            Double distanciaTotal = 0d;
            DateTime tempoTotal = new DateTime();

            Period period = Period.ZERO;

            //soma valores das paradas entre a partida e o destino
            for(ParadaItinerario pi : pis){

                //valor da tarifa
                if(pi.getValorSeguinte() != null){
                    total += pi.getValorSeguinte();
                }

                //distancia
                if(pi.getDistanciaSeguinte() != null){
                    distanciaTotal += pi.getDistanciaSeguinte();
                }

                //tempo
                if(pi.getTempoSeguinte() != null){
                    String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(pi.getTempoSeguinte().getMillis());
                    period = period.plus(parser.parsePeriod(tempo));
                }

            }

            tempoTotal = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(printer.print(period.normalizedStandard(PeriodType.time())));

            //tarifa trecho somada
            if(total > 0){
                itinerario.setTarifaTrecho(total);
            }

            //distancia trecho somada
            if(distanciaTotal > 0){
                itinerario.setDistanciaTrecho(distanciaTotal);
            }

            //tempo trecho somado
            if(tempoTotal.getMinuteOfHour() > 0){
                itinerario.setTempoTrecho(tempoTotal);
            }

        }

        return itinerario;
    }

    private ItinerarioPartidaDestino ordenaConsultaProximoHorario(SimpleSQLiteQuery query) {
        List<ItinerarioPartidaDestino> itins = appDatabase.itinerarioDAO()
                .carregarPorPartidaEDestinoComHorarioNovoSync(query);

        if(itins.size() > 1){
            ordenaPorHorario(itins);
        }

        if(itins.size() > 0){
            return itins.get(0);
        } else{
            return null;
        }
    }

    public void carregaItinerarioResultado(){
        itinerarioResultado = appDatabase.itinerarioDAO()
                .carregar(itinerario.getValue().getHorarioItinerario().getItinerario());
    }

    public static String geraQueryItinerarios(String bairroPartida, String bairroDestino, boolean todos){

        return "SELECT i.id FROM itinerario i INNER JOIN " +
                "parada_itinerario pi ON pi.itinerario = i.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
                "bairro bp ON bp.id = pp.bairro INNER JOIN parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN " +
                "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro " +
                "WHERE i.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem AND pi.ativo = 1 AND pi2.ativo = 1 " +
                "AND ((pi.destaque = 1 OR pi.ordem = 1) AND (pi2.destaque = 1 " +
                "OR pi2.ordem = i.totalParadas )) " +
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
                "               pi3.itinerario = i.id " +
                "               AND bd2.id = '"+bairroDestino+"') < i.totalParadas" +
                "            ) OR " +
                "            (" +
                "               SELECT (SELECT pi3.ordem " +
                "                           FROM   parada_itinerario pi3 " +
                "                                  INNER JOIN parada pd2 " +
                "                                          ON pd2.id = pi3.parada " +
                "                                  INNER JOIN bairro bd2 " +
                "                                          ON bd2.id = pd2.bairro " +
                "                           WHERE " +
                "               pi3.itinerario = i.id " +
                "               AND bd2.id = '"+bairroPartida+"') > 1 " +
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
                "h.id AS idProximoHorario, "+

//                "IFNULL( ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
//                "                WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"'" +
//                "                ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario" +
//                "                WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) as 'idProximoHorario', " +

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

//                "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//                "WHERE pi.ordem = 1 " +
//                "AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idPartida', " +
//
//                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//                "WHERE pi.ordem = 1 " +
//                "AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'nomePartida', " +
//
//                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
//                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'nomeDestino', " +

                "( SELECT b.id FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaInicial) AS 'idBairroPartida', " +

                "( SELECT b.id FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaFinal) AS 'idBairroDestino', " +

                "( SELECT b.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaInicial ) AS 'bairroPartida', " +

                "( SELECT b.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaFinal) AS 'bairroDestino', " +

                "( SELECT c.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pp.id = i.paradaInicial) AS 'cidadePartida', " +

                "( SELECT c.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pp.id = i.paradaFinal) AS 'cidadeDestino' " +

                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario INNER JOIN " +
                "itinerario i ON i.id = hi.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
                "WHERE hi.itinerario IN ('" + itinerariosDisponiveis + "') AND " + dia + " = 1 " +
                "AND strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) > '" + hora + "' " +
                " ORDER BY proximoHorario LIMIT 1";
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
                "               pi3.itinerario = i.id " +
                "               AND bd2.id = '"+bairroDestino+"') < i.totalParadas" +
                "            ) OR " +
                "            (" +
                "               SELECT (SELECT pi3.ordem " +
                "                           FROM   parada_itinerario pi3 " +
                "                                  INNER JOIN parada pd2 " +
                "                                          ON pd2.id = pi3.parada " +
                "                                  INNER JOIN bairro bd2 " +
                "                                          ON bd2.id = pd2.bairro " +
                "                           WHERE " +
                "               pi3.itinerario = i.id " +
                "               AND bd2.id = '"+bairroPartida+"') > 1 " +
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

//                "( SELECT pp.id FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) " +
//                "AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'idPartida', " +
//
//                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//                "WHERE pi.ordem = ( SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1 ) " +
//                "AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'nomePartida', " +
//
//                "( SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//                "WHERE pi.ordem = ( SELECT MAX(ordem) FROM parada_itinerario " +
//                "WHERE itinerario = i.id AND ativo = 1 ) AND pi.itinerario = i.id AND pi.ativo = 1 ) AS 'nomeDestino', " +

                "( SELECT b.id FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaInicial ) AS 'idBairroPartida', " +

                "( SELECT b.id FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaFinal) AS 'idBairroDestino', " +

                "( SELECT b.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaInicial) AS 'bairroPartida', " +

                "( SELECT b.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaFinal) AS 'bairroDestino', " +

                "( SELECT c.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pp.id = i.paradaInicial) AS 'cidadePartida', " +

                "( SELECT c.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pp.id = i.paradaFinal) AS 'cidadeDestino' " +

                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario INNER JOIN " +
                "itinerario i ON i.id = hi.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
                "WHERE hi.itinerario IN ('" + itinerariosDisponiveis + "') AND " + diaAt + " = 1 --ORDER BY proximoHorario LIMIT 1";
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
                "               pi3.itinerario = i.id " +
                "               AND bd2.id = '"+bairroDestino+"') < i.totalParadas" +
                "            ) OR " +
                "            (" +
                "               SELECT (SELECT pi3.ordem " +
                "                           FROM   parada_itinerario pi3 " +
                "                                  INNER JOIN parada pd2 " +
                "                                          ON pd2.id = pi3.parada " +
                "                                  INNER JOIN bairro bd2 " +
                "                                          ON bd2.id = pd2.bairro " +
                "                           WHERE " +
                "               pi3.itinerario = i.id " +
                "               AND bd2.id = '"+bairroDestino+"') > 1 " +
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

                "'' AS 'bairroConsultaPartida', " +
                "'"+bairroDestino+"' AS 'bairroConsultaDestino'," +
                " e.nome AS 'nomeEmpresa', " +
                "strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) as 'proximoHorario', " +

                "IFNULL( ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
                "                WHERE itinerario = i.id AND "+dia+" = 1 AND hi.ativo = 1 AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= '"+hora+"'" +
                "                ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ), ( SELECT h.id FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario" +
                "                WHERE itinerario = i.id AND "+diaSeguinte+" = 1 AND hi.ativo = 1 ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) ) as 'idProximoHorario', " +

                "( SELECT b.id FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaInicial) AS 'idBairroPartida', " +

                "( SELECT b.id FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaFinal ) AS 'idBairroDestino', " +

                "( SELECT b.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaInicial) AS 'bairroPartida', " +

                "( SELECT b.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro WHERE pp.id = i.paradaFinal ) AS 'bairroDestino', " +

                "( SELECT c.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pp.id = i.paradaInicial) AS 'cidadePartida', " +

                "( SELECT c.nome FROM parada pp INNER JOIN " +
                "bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pp.id = i.paradaFinal) AS 'cidadeDestino' " +

                "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario INNER JOIN " +
                "itinerario i ON i.id = hi.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
                "WHERE hi.itinerario IN ('" + itinerariosDisponiveis + "') AND " + dia + " = 1 " +
                "AND strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) > '" + hora + "' " +
                "/*AND strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) <= '"
                + DateTimeFormat.forPattern("HH:mm:ss").print(DateTime.now().plusHours(8)) +"' ORDER BY proximoHorario LIMIT 1*/";
    }

    private String geraTabelaTemp(){
        return "CREATE TABLE IF NOT EXISTS tpr(" +
                "  sigla TEXT," +
                "  tarifa REAL," +
                "  distancia REAL," +
                "  distanciaMetros REAL," +
                "  tempo INT," +
                "  acessivel INT," +
                "  empresa TEXT," +
                "  observacao TEXT," +
                "  mostraRuas INT," +
                "  id TEXT," +
                "  ativo INT," +
                "  enviado INT," +
                "  data_cadastro INT," +
                "  usuario_cadastro TEXT," +
                "  ultima_alteracao INT," +
                "  usuario_ultima_alteracao TEXT," +
                "  programado_para INT," +
                "  \"tempo:1\" INT," +
                "  idBairroPartida TEXT," +
                "  bairroPartida TEXT," +
                "  cidadePartida TEXT," +
                "  idBairroDestino TEXT," +
                "  bairroDestino TEXT," +
                "  cidadeDestino TEXT," +
                "  distanciaTrecho," +
                "  distanciaTrechoMetros," +
                "  tempoTrecho," +
                "  tarifaTrecho," +
                "  inicio," +
                "  fim," +
                "  \"bairroDestino:1\" TEXT," +
                "  \"cidadeDestino:1\" TEXT, " +
                "  aliasBairroPartida TEXT," +
                "  aliasCidadePartida TEXT," +
                "  aliasBairroDestino TEXT," +
                "  aliasCidadeDestino TEXT" +
                ")";
    }

    private String populaTabelaTemp(){
        return "INSERT INTO tpr (sigla, tarifa, distancia, distanciaMetros, tempo, acessivel, empresa, observacao, mostraRuas, " +
                "id, ativo, enviado, data_cadastro, usuario_cadastro, ultima_alteracao, usuario_ultima_alteracao, programado_para, " +
                "idBairroPartida, bairroPartida, cidadePartida, idBairroDestino, bairroDestino, cidadeDestino, distanciaTrecho, " +
                "distanciaTrechoMetros, tempoTrecho, tarifaTrecho, inicio, fim, aliasBairroPartida, aliasCidadePartida, " +
                "aliasBairroDestino, aliasCidadeDestino)" +

                "SELECT i.sigla, i.tarifa, i.distancia, i.distanciaMetros, i.tempo AS 'tempo', i.acessivel, i.empresa, i.observacao, " +
                "i.mostraRuas, i.id, i.ativo, i.enviado, i.data_cadastro, i.usuario_cadastro, i.ultima_alteracao, " +
                "i.usuario_ultima_alteracao, i.programado_para, " +
                "b.id as 'idBairroPartida', " +
                "b.nome AS 'bairroPartida', c.nome AS 'cidadePartida', " +

                "(" +
                " SELECT b2.id FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario" +
                " INNER JOIN bairro b2 ON b2.id = p2.bairro" +
                " WHERE pi2.ordem = (SELECT MAX(pi.ordem)+1)" +
                ") AS 'idBairroDestino'," +
                "(" +
                " SELECT b2.nome FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario" +
                " INNER JOIN bairro b2 ON b2.id = p2.bairro" +
                " WHERE pi2.ordem = (SELECT MAX(pi.ordem)+1)" +
                ") AS 'bairroDestino'," +
                "(" +
                " SELECT c2.nome FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario" +
                " INNER JOIN bairro b2 ON b2.id = p2.bairro INNER JOIN cidade c2 ON c2.id = b2.cidade" +
                " WHERE pi2.ordem = (SELECT MAX(pi.ordem)+1)" +
                ") AS 'cidadeDestino', " +
                " SUM(pi.distanciaSeguinte) AS 'distanciaTrecho', SUM(pi.distanciaSeguinteMetros) AS 'distanciaTrechoMetros', " +
                " SUM(pi.tempoSeguinte) AS 'tempoTrecho', SUM(pi.valorSeguinte) AS 'tarifaTrecho', " +
                "MIN(pi.ordem) AS 'inicio', MAX(pi.ordem) AS 'fim', i.aliasBairroPartida, i.aliasCidadePartida, " +
                "i.aliasBairroDestino, i.aliasCidadeDestino" +

                " FROM parada_itinerario pi INNER JOIN " +
                "     parada p ON p.id = pi.parada INNER JOIN " +
                "     bairro b ON b.id = p.bairro INNER JOIN " +
                "     cidade c ON c.id = b.cidade INNER JOIN" +
                "     itinerario i ON i.id = pi.itinerario" +
                "     " +
                " WHERE pi.ativo = 1" +
                " GROUP BY i.id, b.id, b.nome, c.nome, i.tarifa" +
                " HAVING bairroDestino != ''" +
                " ORDER BY pi.itinerario, pi.ordem; ";
    }

    public String consultaTabelaTemp(){
        return "SELECT t1.sigla, t1.tarifa, t1.distancia, " +
                "CASE WHEN t1.distanciaMetros IS NULL THEN t1.distancia * 1000 ELSE t1.distanciaMetros END AS distanciaMetros, " +
                "t1.tempo, t1.id, t1.idBairroPartida, t1.bairroPartida AS 'bairroPartida', t1.cidadePartida AS 'cidadePartida', " +
                "t2.idBairroDestino, t2.bairroDestino AS 'bairroDestino', t2.cidadeDestino AS 'cidadeDestino'," +

        "CASE WHEN t1.fim = t2.fim THEN t1.distanciaTrecho ELSE (t2.distanciaTrecho + t1.distanciaTrecho) END AS 'distanciaTrecho'," +
                "CASE WHEN t1.fim = t2.fim THEN t1.distanciaTrechoMetros ELSE (t2.distanciaTrechoMetros + t1.distanciaTrechoMetros) END AS 'distanciaTrechoMetros'," +
                "CASE WHEN t1.fim = t2.fim THEN t1.tempoTrecho ELSE (t2.tempoTrecho + t1.tempoTrecho) END AS 'tempoTrecho'," +
                "CASE WHEN t1.fim = t2.fim THEN t1.tarifaTrecho ELSE (t2.tarifaTrecho + t1.tarifaTrecho) END AS 'tarifaTrecho', " +
                " " +
                "CASE WHEN (t1.inicio = 1 AND t2.idBairroDestino = (" +
                " SELECT p3.bairro " +
                " FROM parada_itinerario pi3 INNER JOIN parada p3 ON p3.id = pi3.parada " +
                " WHERE pi3.itinerario = t1.id " +
                " ORDER BY pi3.ordem DESC LIMIT 1 " +
                "    )) THEN 0 ELSE 1 END AS 'flagTrecho' " +
                "" +
                " FROM tpr t1 INNER JOIN tpr t2 ON t1.id = t2.id AND t1.fim <= t2.fim " +

                " ORDER BY t1.id, t1.inicio, t2.inicio";
    }

    public String consultaTabelaTempSimplificado(){
        return "SELECT " +
                "CASE WHEN t1.distanciaMetros IS NULL THEN t1.distancia * 1000 ELSE t1.distanciaMetros END AS distanciaMetros, " +
                "t1.idBairroPartida, " +
                "t2.idBairroDestino, " +

                "CASE WHEN t1.fim = t2.fim THEN t1.distanciaTrecho ELSE (t2.distanciaTrecho + t1.distanciaTrecho) END AS 'distanciaTrecho'," +
                "CASE WHEN t1.fim = t2.fim THEN t1.distanciaTrechoMetros ELSE (t2.distanciaTrechoMetros + t1.distanciaTrechoMetros) END AS 'distanciaTrechoMetros'," +
                " " +
                "CASE WHEN (t1.inicio = 1 AND t2.idBairroDestino = (" +
                " SELECT p3.bairro " +
                " FROM parada_itinerario pi3 INNER JOIN parada p3 ON p3.id = pi3.parada " +
                " WHERE pi3.itinerario = t1.id " +
                " ORDER BY pi3.ordem DESC LIMIT 1 " +
                "    )) THEN 0 ELSE 1 END AS 'flagTrecho' " +
                "" +
                " FROM tpr t1 INNER JOIN tpr t2 ON t1.id = t2.id AND t1.fim <= t2.fim " +

                " ORDER BY t1.id, t1.inicio, t2.inicio";
    }

}
