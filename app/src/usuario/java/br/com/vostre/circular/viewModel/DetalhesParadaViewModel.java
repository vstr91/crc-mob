package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import android.content.Context;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Feriado;
import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.utils.DataHoraUtils;
import br.com.vostre.circular.utils.ImageUtils;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.SessionUtils;
import br.com.vostre.circular.utils.StringUtils;
import br.com.vostre.circular.view.listener.FeriadoListener;

public class DetalhesParadaViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<ParadaBairro> parada;
    public ParadaBairro umaParada;
    public MutableLiveData<Location> localAtual;

    public ParadaBairro paradaBairro;
    public PontoInteresse pontoInteresse;

    public MutableLiveData<List<ItinerarioPartidaDestino>> itinerarios;
    public LiveData<List<PontoInteresse>> pois;
    public LiveData<List<ImagemParada>> imagensParada;

    public Bitmap foto;

    public static MutableLiveData<Boolean> isFeriado;

    public static MutableLiveData<Integer> retorno;

    public ParadaBairro getParadaBairro() {
        return paradaBairro;
    }

    public void setParadaBairro(ParadaBairro paradaBairro) {
        this.paradaBairro = paradaBairro;
    }

    public PontoInteresse getPontoInteresse() {
        return pontoInteresse;
    }

    public void setPontoInteresse(PontoInteresse pontoInteresse) {
        this.pontoInteresse = pontoInteresse;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public void getImagensParada(String parada){
        this.imagensParada = appDatabase.imagemParadaDAO().listarTodosAtivosPorParada(parada);
    }

    public void setParada(final String parada, boolean isFeriado) {
        //foto = BitmapFactory.decodeFile(parada.getParada().getImagem());
        this.parada = appDatabase.paradaDAO().carregarComBairro(parada);

        String dia = DataHoraUtils.getDiaAtual();
        String diaSeguinte = DataHoraUtils.getDiaSeguinte();
        String hora = DataHoraUtils.getHoraAtual()+":00";

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

                "( SELECT pp.id FROM parada pp WHERE pp.id = i.paradaInicial ) AS 'idPartida', " +

                "( SELECT nome FROM parada pp " +
                "WHERE pp.id = i.paradaInicial ) AS 'nomePartida', " +

                "( SELECT nome FROM parada pp WHERE pp.id = i.paradaFinal) AS 'nomeDestino', " +

                "( SELECT b.id FROM parada pp INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pp.id = i.paradaInicial) AS 'idBairroPartida', " +

                "( SELECT b.id FROM parada pp INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pp.id = i.paradaFinal) AS 'idBairroDestino', " +

                "( SELECT b.nome FROM parada pp INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pp.id = i.paradaInicial ) AS 'bairroPartida', " +

                "( SELECT b.nome FROM parada pp INNER JOIN bairro b ON b.id = pp.bairro " +
                "WHERE pp.id = i.paradaFinal ) AS 'bairroDestino', " +

                "( SELECT c.nome FROM parada pp INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pp.id = i.paradaInicial ) AS 'cidadePartida', " +

                "( SELECT c.nome FROM parada pp INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
                "WHERE pp.id = i.paradaFinal ) AS 'cidadeDestino' " +

                "FROM itinerario i INNER JOIN empresa e ON e.id = i.empresa WHERE i.id IN (SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
                "WHERE p.id = '"+parada+"' AND pi.ativo = 1 AND p.id <> (SELECT pp.id FROM parada pp WHERE pp.id = i.paradaFinal) AND proximoHorario IS NOT NULL) " +
                "ORDER BY flagDia, proximoHorario");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                PeriodFormatter parser =
                        new PeriodFormatterBuilder()
                                .appendHours().appendLiteral(":")
                                .appendMinutes().appendLiteral(":").appendSeconds().toFormatter();

                PeriodFormatter printer =
                        new PeriodFormatterBuilder()
                                .printZeroAlways().minimumPrintedDigits(2)
                                //.appendDays().appendLiteral(" dia(s) ")
                                .appendHours().appendLiteral(":")
                                .appendMinutes().appendLiteral(":").appendSeconds().toFormatter();

                PeriodFormatter printerFinal =
                        new PeriodFormatterBuilder()
                                .printZeroAlways().minimumPrintedDigits(2)
                                //.appendDays().appendLiteral(" dia(s) ")
                                .appendHours().appendLiteral(":")
                                .appendMinutes().toFormatter();

                List<ItinerarioPartidaDestino> itis = appDatabase.itinerarioDAO()
                        .listarTodosAtivosPorParadaComBairroEHorarioCompletoSync(query);

                for(ItinerarioPartidaDestino i : itis){
                    List<ParadaItinerario> paradas = appDatabase.paradaItinerarioDAO()
                            .listarParadasAtivasPorItinerarioComBairroSync(i.getItinerario().getId(), parada);

                    DateTime tempoTotal = new DateTime();

                    Period period = Period.ZERO;

                    for(ParadaItinerario p : paradas){

                        if(p != null && p.getTempoSeguinte() != null){
                            String tempo = DateTimeFormat.forPattern("HH:mm:ss").print(p.getTempoSeguinte().getMillis());
                            period = period.plus(parser.parsePeriod(tempo));
                        }

                    }

                    tempoTotal = DateTimeFormat.forPattern("HH:mm:ss")
                            .parseDateTime(printer.print(period.normalizedStandard(PeriodType.time())));

                    i.setTempoAcumulado(tempoTotal);

                    String proxHorario = i.getProximoHorario()+":00";

                    Period per = Period.ZERO;
                    per = per.plus(parser.parsePeriod(proxHorario));

                    per = per.plus(parser.parsePeriod(DateTimeFormat.forPattern("HH:mm:ss")
                            .print(tempoTotal)));

                    i.setHorarioEstimado(printerFinal.print(per.normalizedStandard(PeriodType.time())));

                }

                itinerarios.postValue(itis);

            }
        });

        //itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada, DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime()));
        pois = appDatabase.pontoInteresseDAO().listarTodosAtivosProximos(0, 0, 0, 0);
    }

    public LiveData<ParadaBairro> getParada() {
        return parada;
    }

    public DetalhesParadaViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        parada = appDatabase.paradaDAO().carregarComBairro("");
        itinerarios = new MutableLiveData<>();
        localAtual = new MutableLiveData<>();

        isFeriado = new MutableLiveData<>();
        isFeriado.setValue(null);
        retorno = new MutableLiveData<>();
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

    @BindingAdapter("srcCompat")
    public static void setImagemFoto(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        } else{
            imageView.setBackgroundResource(R.mipmap.ic_onibus_azul);
        }

    }

    public void atualizaPontoMapa(){
        midPoint(parada.getValue().getParada().getLatitude(), parada.getValue().getParada().getLongitude(),
                pontoInteresse.getLatitude(), pontoInteresse.getLongitude());
    }

    public void midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        GeoPoint origin = new GeoPoint(lat1,lon1);
        //create destination geopoints from parameters
        GeoPoint destination = new GeoPoint(lat2,lon2);
        //calculate and return center
        GeoPoint point = GeoPoint.fromCenterBetween(origin, destination);

        Location l = new Location(LocationManager.GPS_PROVIDER);

        l.setLatitude(point.getLatitude());
        l.setLongitude(point.getLongitude());

        localAtual.postValue(l);

    }

    public void carregaParadaQRCode(String uf, String cidade, String bairro, String parada){
        this.parada = appDatabase.paradaDAO().carregarComBairroPorUFCidadeEBairro(uf.toUpperCase(), cidade, bairro, parada);
        pois = appDatabase.pontoInteresseDAO().listarTodosAtivosProximos(0, 0, 0, 0);
    }

    public void carregarDadosVinculadosQRCode(String parada){
        itinerarios.postValue(appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada, DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime())).getValue());
        pois = appDatabase.pontoInteresseDAO().listarTodosAtivosProximos(0, 0, 0, 0);
    }

    public void carregaDirections(MapView map, ParadaBairro parada, PontoInteresse pontoInteresse) {

        new directionsAsyncTask(map, parada, pontoInteresse, getApplication().getApplicationContext()).execute();
    }

    private static class directionsAsyncTask extends AsyncTask<String, Void, Void> {

        MapView map;
        ParadaBairro parada;
        PontoInteresse pontoInteresse;
        Polyline rota;
        Context ctx;

        directionsAsyncTask(MapView map, ParadaBairro parada, PontoInteresse pontoInteresse, Context ctx) {
            this.map = map;
            this.parada = parada;
            this.pontoInteresse = pontoInteresse;
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(final String... params) {
            RoadManager roadManager = new OSRMRoadManager(ctx);

            ArrayList<GeoPoint> points = new ArrayList<>();
            points.add(new GeoPoint(parada.getParada().getLatitude(), parada.getParada().getLongitude()));
            points.add(new GeoPoint(pontoInteresse.getLatitude(), pontoInteresse.getLongitude()));

            Road road = roadManager.getRoad(points);
            rota = RoadManager.buildRoadOverlay(road);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            map.getOverlays().add(rota);
            map.invalidate();
        }
    }

//    public void carregarItinerarios(String parada){
//        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada, DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime()));
//    }

    public void buscaPoisProximos(Location local){

        double latitude = local.getLatitude();
        double longitude = local.getLongitude();

//        isRunningNearPlaces = true;

        // Centro - Barra do Pirai
        //double latitude = -22.470612;
        //double longitude = -43.8263613;

        int raioEmMetros = 500;

// 6378000 Size of the Earth (in meters)
        double longitudeD = (Math.asin(raioEmMetros / (6378000 * Math.cos(Math.PI*latitude/180))))*180/Math.PI;
        double latitudeD = (Math.asin((double)raioEmMetros / (double)6378000))*180/Math.PI;

        double latitudeMax = latitude+(latitudeD);
        double latitudeMin = latitude-(latitudeD);
        double longitudeMax = longitude+(longitudeD);
        double longitudeMin = longitude-(longitudeD);

        pois = appDatabase.pontoInteresseDAO().listarTodosAtivosProximos(latitudeMin, latitudeMax, longitudeMin, longitudeMax);

        //new buscaAsyncTask(appDatabase, local, this).execute();
    }

    public void salvarFoto(ImagemParada imagemParada, Bitmap foto) {
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

                    if(imagemParada.getImagem() != null && !imagemParada.getImagem().isEmpty()){
                        File fotoAntiga = new File(getApplication().getFilesDir(), imagemParada.getImagem());

                        if(fotoAntiga.exists() && fotoAntiga.canWrite() && fotoAntiga.getName() != file.getName()){
                            fotoAntiga.delete();
                        }
                    }

                    imagemParada.setImagem(file.getName());
                    imagemParada.setImagemEnviada(false);

                    if(SessionUtils.estaLogado(getApplication().getApplicationContext())){

                        imagemParada.setDescricao(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                        imagemParada.setUsuarioCadastro(PreferenceUtils.carregarUsuarioLogado(getApplication().getApplicationContext()));
                        imagemParada.setUsuarioUltimaAlteracao(PreferenceUtils.carregarUsuarioLogado(getApplication().getApplicationContext()));
                    }

                    add(imagemParada);
                }
            } catch (IOException e) {
                e.printStackTrace();
                retorno.setValue(0);
            }
        }
    }

    // adicionar

    public void add(final ImagemParada imagemParada) {

        imagemParada.setDataCadastro(new DateTime());
        imagemParada.setUltimaAlteracao(new DateTime());
        imagemParada.setEnviado(false);

        imagemParada.setParada(parada.getValue().getParada().getId());

        new addAsyncTask(appDatabase).execute(imagemParada);
    }

    private static class addAsyncTask extends AsyncTask<ImagemParada, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ImagemParada... params) {
            db.imagemParadaDAO().inserir((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(retorno != null){
                retorno.setValue(1);
            }

        }

    }

    // fim adicionar

}
