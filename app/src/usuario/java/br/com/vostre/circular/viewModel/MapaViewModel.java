package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import br.com.vostre.circular.model.Feriado;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.PontoInteresseSugestao;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;
import br.com.vostre.circular.utils.DataHoraUtils;
import br.com.vostre.circular.utils.ImageUtils;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.StringUtils;
import br.com.vostre.circular.view.listener.FeriadoListener;

public class MapaViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<ParadaBairro>> paradas;
    public LiveData<List<ParadaSugestaoBairro>> paradasSugeridas;
    public LiveData<List<PontoInteresse>> pois;
    public LiveData<List<PontoInteresseSugestaoBairro>> poisSugeridos;
    public MutableLiveData<Location> localAtual;
    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    ParadaBairro parada;
    public ParadaSugestaoBairro paradaNova;
    public Bitmap foto;
    public MutableLiveData<List<ItinerarioPartidaDestino>> itinerarios;
    public LiveData<List<BairroCidade>> bairros;

    public Bitmap fotoParada;
    public BairroCidade bairro;

    public static MutableLiveData<Integer> retorno;

    public PontoInteresseSugestaoBairro poiNovo;
    public Bitmap fotoPoi;
    public BairroCidade bairroPoi;

    public static MutableLiveData<Boolean> isFeriado;

    public ParadaSugestaoBairro getParadaNova() {
        return paradaNova;
    }

    public void setParadaNova(ParadaSugestaoBairro paradaNova) {
        this.paradaNova = paradaNova;

        if(paradaNova.getParada().getImagem() != null){
            File foto = new File(getApplication().getFilesDir(), paradaNova.getParada().getImagem());

            if(foto.exists() && foto.canRead()){
                this.fotoParada = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.fotoParada = null;
        }
    }

    public void setPoiNovo(PontoInteresseSugestaoBairro poiNovo) {
        this.poiNovo = poiNovo;

        if(poiNovo.getPontoInteresse().getImagem() != null){
            File foto = new File(getApplication().getFilesDir(), poiNovo.getPontoInteresse().getImagem());

            if(foto.exists() && foto.canRead()){
                this.fotoPoi = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.fotoPoi = null;
        }
    }

    public Bitmap getFotoParada() {
        return fotoParada;
    }

    public void setFotoParada(Bitmap foto) {
        this.fotoParada = foto;
    }

    public ParadaBairro getParada() {
        return parada;
    }

    public void setParada(final ParadaBairro parada, boolean isFeriado) {
        this.parada = parada;

        String dia = DataHoraUtils.getDiaAtual();
        String diaSeguinte = DataHoraUtils.getDiaSeguinte();
        String hora = DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime());

        if(isFeriado){
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
                "FROM itinerario i INNER JOIN empresa e ON e.id = i.empresa WHERE i.id IN (SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
                "WHERE p.id = '"+parada.getParada().getId()+"' AND p.id <> (SELECT pi.parada FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
                "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AND proximoHorario IS NOT NULL) " +
                "ORDER BY flagDia, proximoHorario");

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

                PeriodFormatter printerSemSegundo =
                        new PeriodFormatterBuilder()
                                .printZeroAlways().minimumPrintedDigits(2)
                                //.appendDays().appendLiteral(" dia(s) ")
                                .appendHours().appendLiteral(":")
                                .appendMinutes().toFormatter();

                List<ItinerarioPartidaDestino> itis = appDatabase.itinerarioDAO()
                        .listarTodosAtivosPorParadaComBairroEHorarioCompletoSync(query);

                for(ItinerarioPartidaDestino i : itis){
                    List<ParadaItinerario> paradas = appDatabase.paradaItinerarioDAO()
                            .listarParadasAtivasPorItinerarioComBairroSync(i.getItinerario().getId(), parada.getParada().getId());

                    DateTime tempoTotal = new DateTime();

                    Period period = Period.ZERO;

                    for(ParadaItinerario p : paradas){
                        String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(p.getTempoSeguinte().getMillis());
                        period = period.plus(parser.parsePeriod(tempo));
                    }

                    tempoTotal = DateTimeFormat.forPattern("HH:mm:ss")
                            .parseDateTime(printer.print(period.normalizedStandard(PeriodType.time())));

                    i.setTempoAcumulado(tempoTotal);

                    String proxHorario = i.getProximoHorario();

                    Period per = Period.ZERO;
                    per = per.plus(parser.parsePeriod(proxHorario+":00"));

                    per = per.plus(parser.parsePeriod(DateTimeFormat.forPattern("HH:mm:ss")
                            .print(tempoTotal)));

                    i.setHorarioEstimado(printerSemSegundo.print(per.normalizedStandard(PeriodType.time())));

                }

                itinerarios.postValue(itis);

            }
        });

//        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada.getParada().getId(),
//                DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime()));

        if(parada.getParada().getImagem() != null){
            File foto = new File(getApplication().getFilesDir(), parada.getParada().getImagem());

            if(foto.exists() && foto.canRead()){
                this.foto = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.foto = null;
        }

    }

    public void setItinerario(String itinerario) {
        this.paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro(itinerario);
    }

    public LiveData<List<ParadaBairro>> getParadas() {
        return paradas;
    }

    public void setParadas(LiveData<List<ParadaBairro>> paradas) {
        this.paradas = paradas;
    }

    public MapaViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        paradas = appDatabase.paradaDAO().listarTodosAtivosComBairroComItinerario();
        paradasSugeridas = appDatabase.paradaSugestaoDAO().listarTodosPendentesComBairroPorUsuario(PreferenceUtils
                .carregarUsuarioLogado(getApplication().getApplicationContext()));
        pois = appDatabase.pontoInteresseDAO().listarTodosAtivos();
        poisSugeridos = appDatabase.pontoInteresseSugestaoDAO().listarTodosPendentesComBairroPorUsuario(PreferenceUtils
                .carregarUsuarioLogado(getApplication().getApplicationContext()));
        bairros = appDatabase.bairroDAO().listarTodosComCidade();
        paradaNova = new ParadaSugestaoBairro();
        poiNovo = new PontoInteresseSugestaoBairro();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());

        localAtual = new MutableLiveData<>();
        localAtual.postValue(new Location(LocationManager.GPS_PROVIDER));

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);

        itinerarios = new MutableLiveData<>();
        isFeriado = new MutableLiveData<>();
        isFeriado.setValue(null);

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

    public void salvarParada(){

        paradaNova.getParada().setBairro(bairro.getBairro().getId());

        if(foto != null){
            salvarFoto();
        }

        if(paradaNova.getParada().valida(paradaNova.getParada())){
            add(paradaNova.getParada());
        } else{
            retorno.setValue(0);
        }

    }

    public void editarParada(){

        if(bairro != null){
            paradaNova.getParada().setBairro(bairro.getBairro().getId());
        }

        if(foto != null){
            salvarFoto();
        }

        if(paradaNova.getParada().valida(paradaNova.getParada())){
            edit(paradaNova.getParada());
        } else{
            retorno.setValue(0);
        }

    }

    public void salvarPontoInteresse(){

        poiNovo.getPontoInteresse().setBairro(bairro.getBairro().getId());

        if(fotoPoi != null){
            salvarFotoPoi();
        }

        if(poiNovo.getPontoInteresse().valida(poiNovo.getPontoInteresse())){
            addPoi(poiNovo.getPontoInteresse());
        } else{
            retorno.setValue(0);
        }

    }

    public void editarPontoInteresse(){

        if(bairro != null){
            poiNovo.getPontoInteresse().setBairro(bairro.getBairro().getId());
        }

        if(fotoPoi != null){
            salvarFotoPoi();
        }

        if(poiNovo.getPontoInteresse().valida(poiNovo.getPontoInteresse())){
            editPoi(poiNovo.getPontoInteresse());
        } else{
            retorno.setValue(0);
        }

    }

    private void salvarFoto() {
        FileOutputStream fos = null;
        File file = new File(getApplication().getFilesDir(),  UUID.randomUUID().toString()+".png");

        try {
            fos = new FileOutputStream(file);
            foto = ImageUtils.scaleDown(foto, 600, true);
            foto.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();

                    if(paradaNova.getParada().getImagem() != null && !paradaNova.getParada().getImagem().isEmpty()){
                        File fotoAntiga = new File(getApplication().getFilesDir(), paradaNova.getParada().getImagem());

                        if(fotoAntiga.exists() && fotoAntiga.canWrite() && fotoAntiga.getName() != file.getName()){
                            fotoAntiga.delete();
                        }
                    }

                    paradaNova.getParada().setImagem(file.getName());
                    paradaNova.getParada().setImagemEnviada(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void salvarFotoPoi() {
        FileOutputStream fos = null;
        File file = new File(getApplication().getFilesDir(),  UUID.randomUUID().toString()+".png");

        try {
            fos = new FileOutputStream(file);
            fotoPoi = ImageUtils.scaleDown(fotoPoi, 600, true);
            fotoPoi.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();

                    if(poiNovo.getPontoInteresse().getImagem() != null && !poiNovo.getPontoInteresse().getImagem().isEmpty()){
                        File fotoAntiga = new File(getApplication().getFilesDir(), poiNovo.getPontoInteresse().getImagem());

                        if(fotoAntiga.exists() && fotoAntiga.canWrite() && fotoAntiga.getName() != file.getName()){
                            fotoAntiga.delete();
                        }
                    }

                    poiNovo.getPontoInteresse().setImagem(file.getName());
                    poiNovo.getPontoInteresse().setImagemEnviada(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // adicionar

    public void add(final ParadaSugestao parada) {

        parada.setDataCadastro(new DateTime());
        parada.setUltimaAlteracao(new DateTime());
        parada.setEnviado(false);
        parada.setSlug(StringUtils.toSlug(parada.getNome()));

        String id = PreferenceUtils.carregarUsuarioLogado(getApplication().getApplicationContext());

        if(parada.getImagem() != null && !parada.getImagem().isEmpty()){
            parada.setImagemEnviada(false);
        }

        parada.setUsuarioCadastro(id);
        parada.setUsuarioUltimaAlteracao(id);
        parada.setStatus(0);

        parada.setBairro(bairro.getBairro().getId());

        new addAsyncTask(appDatabase).execute(parada);
    }

    private static class addAsyncTask extends AsyncTask<ParadaSugestao, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ParadaSugestao... params) {
            db.paradaSugestaoDAO().inserir((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim adicionar

    // editar

    public static void edit(final ParadaSugestao parada, Context context) {

        if(appDatabase == null){
            appDatabase = AppDatabase.getAppDatabase(context.getApplicationContext());
        }

        parada.setUltimaAlteracao(new DateTime());
        parada.setEnviado(false);
        parada.setSlug(StringUtils.toSlug(parada.getNome()));
        parada.setStatus(0);

        String id = PreferenceUtils.carregarUsuarioLogado(context.getApplicationContext());

        if(parada.getImagem() != null && !parada.getImagem().isEmpty()){
            parada.setImagemEnviada(false);
        }

        parada.setUsuarioUltimaAlteracao(id);

        new editAsyncTask(appDatabase).execute(parada);
    }

    public void edit(final ParadaSugestao parada) {

        parada.setUltimaAlteracao(new DateTime());
        parada.setEnviado(false);
        parada.setSlug(StringUtils.toSlug(parada.getNome()));
        parada.setStatus(0);

        String id = PreferenceUtils.carregarUsuarioLogado(getApplication().getApplicationContext());

        if(parada.getImagem() != null && !parada.getImagem().isEmpty()){
            parada.setImagemEnviada(false);
        }

        parada.setUsuarioUltimaAlteracao(id);

        new editAsyncTask(appDatabase).execute(parada);
    }

    private static class editAsyncTask extends AsyncTask<ParadaSugestao, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ParadaSugestao... params) {
            db.paradaSugestaoDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim editar

    // adicionar poi

    public void addPoi(final PontoInteresseSugestao poi) {

        poi.setDataCadastro(new DateTime());
        poi.setUltimaAlteracao(new DateTime());
        poi.setEnviado(false);
        poi.setSlug(StringUtils.toSlug(poi.getNome()));

        String id = PreferenceUtils.carregarUsuarioLogado(getApplication().getApplicationContext());

        if(poi.getImagem() != null && !poi.getImagem().isEmpty()){
            poi.setImagemEnviada(false);
        }

        poi.setUsuarioCadastro(id);
        poi.setUsuarioUltimaAlteracao(id);
        poi.setStatus(0);

        poi.setBairro(bairro.getBairro().getId());

        new addPoiAsyncTask(appDatabase).execute(poi);
    }

    private static class addPoiAsyncTask extends AsyncTask<PontoInteresseSugestao, Void, Void> {

        private AppDatabase db;

        addPoiAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final PontoInteresseSugestao... params) {
            db.pontoInteresseSugestaoDAO().inserir((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim adicionar poi

    // editar poi

    public static void editPoi(final PontoInteresseSugestao poi, Context context) {

        if(appDatabase == null){
            appDatabase = AppDatabase.getAppDatabase(context.getApplicationContext());
        }

        poi.setUltimaAlteracao(new DateTime());
        poi.setEnviado(false);
        poi.setSlug(StringUtils.toSlug(poi.getNome()));
        poi.setStatus(0);

        String id = PreferenceUtils.carregarUsuarioLogado(context.getApplicationContext());

        if(poi.getImagem() != null && !poi.getImagem().isEmpty()){
            poi.setImagemEnviada(false);
        }

        poi.setUsuarioUltimaAlteracao(id);

        new editPoiAsyncTask(appDatabase).execute(poi);
    }

    public void editPoi(final PontoInteresseSugestao poi) {

        poi.setUltimaAlteracao(new DateTime());
        poi.setEnviado(false);
        poi.setSlug(StringUtils.toSlug(poi.getNome()));
        poi.setStatus(0);

        String id = PreferenceUtils.carregarUsuarioLogado(getApplication().getApplicationContext());

        if(poi.getImagem() != null && !poi.getImagem().isEmpty()){
            poi.setImagemEnviada(false);
        }

        poi.setUsuarioUltimaAlteracao(id);

        new editPoiAsyncTask(appDatabase).execute(poi);
    }

    private static class editPoiAsyncTask extends AsyncTask<PontoInteresseSugestao, Void, Void> {

        private AppDatabase db;

        editPoiAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final PontoInteresseSugestao... params) {
            db.pontoInteresseSugestaoDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim editar poi

}
