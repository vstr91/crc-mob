package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import br.com.vostre.circular.model.Feedback;
import br.com.vostre.circular.model.FeedbackItinerario;
import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.SecaoItinerarioParada;
import br.com.vostre.circular.utils.StringUtils;
import br.com.vostre.circular.view.listener.PartidaEDestinoListener;

public class DetalhesItinerarioViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<ItinerarioPartidaDestino> itinerario;

    public LiveData<List<ParadaBairro>> paradas;
    public static MutableLiveData<List<HorarioItinerarioNome>> horarios;
    public LiveData<List<SecaoItinerario>> secoes;
    public LiveData<List<SecaoItinerarioParada>> secoesComNome;
    public MutableLiveData<Location> localAtual;
    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    public static MutableLiveData<Integer> retorno;
    public static List<ItinerarioPartidaDestino> qtdItinerarios;

    String paradaPartida;
    String paradaDestino;

    public LiveData<ParadaBairro> partida;
    public LiveData<ParadaBairro> destino;

    Location l;

    public LiveData<List<ParadaBairro>> ruas;

    public boolean trechoIsolado = false;

    public String partidaConsulta;
    public String destinoConsulta;

    public FeedbackItinerario feedback;
    public Bitmap foto;

    String nomeTemp = "";

    public FeedbackItinerario getFeedback() {
        return feedback;
    }

    public void setFeedback(FeedbackItinerario feedback) {
        this.feedback = feedback;
    }

    public void setItinerario(String itinerario, String paradaPartida, String paradaDestino,
                              String bairroPartida, String bairroDestino) {
        this.itinerario = appDatabase.itinerarioDAO().carregarSimplificado(itinerario);

        if(trechoIsolado){
            this.paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairroTrechoSimplificado(partidaConsulta, destinoConsulta);
        } else{
            this.paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairroSimplificado(itinerario);
        }

        if(paradaPartida == null || paradaDestino == null){
            new carregaHorariosAsyncTask(appDatabase, itinerario, null, trechoIsolado, partidaConsulta, destinoConsulta).execute();
        } else{
            this.paradaPartida = paradaPartida;
            this.paradaDestino = paradaDestino;

            this.partida = appDatabase.paradaDAO().carregarComBairro(paradaPartida);
            this.destino = appDatabase.paradaDAO().carregarComBairro(paradaDestino);

            new carregaHorariosAsyncTask(appDatabase, itinerario, null, bairroPartida, bairroDestino, trechoIsolado, partidaConsulta, destinoConsulta).execute();
        }



        //this.horarios = appDatabase.horarioItinerarioDAO().listarApenasAtivosPorItinerario(itinerario);
        this.secoes = appDatabase.secaoItinerarioDAO().listarTodosPorItinerario(itinerario);
        this.secoesComNome = appDatabase.secaoItinerarioDAO().listarTodosPorItinerarioComParada(itinerario);
    }

    public void setPartidaEDestino(String paradaPartida, String paradaDestino) {
        this.paradaPartida = paradaPartida;
        this.paradaDestino = paradaDestino;

        this.partida = appDatabase.paradaDAO().carregarComBairro(paradaPartida);
        this.destino = appDatabase.paradaDAO().carregarComBairro(paradaDestino);
    }

    public LiveData<List<ParadaBairro>> getParadas() {
        return paradas;
    }

    public void setParadas(LiveData<List<ParadaBairro>> paradas) {
        this.paradas = paradas;
    }

    public DetalhesItinerarioViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
//        itinerario = appDatabase.itinerarioDAO().carregar("");
        paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro("");
        //horarios = appDatabase.horarioItinerarioDAO().listarApenasAtivosPorItinerario("");
        horarios = new MutableLiveData<>();
        secoes = appDatabase.secaoItinerarioDAO().listarTodosPorItinerario("");

        this.partida = appDatabase.paradaDAO().carregarComBairro("");
        this.destino = appDatabase.paradaDAO().carregarComBairro("");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());

        localAtual = new MutableLiveData<>();

        l = new Location(LocationManager.NETWORK_PROVIDER);
        l.setLatitude(0);
        l.setLongitude(0);

        localAtual.setValue(l);

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);

        this.ruas = appDatabase.paradaItinerarioDAO().listarRuasPorItinerario("");
        feedback = new FeedbackItinerario();
    }

    public void salvarFeedback(FeedbackItinerario feedback){
        add(feedback);
        System.out.println("FEEDBACK "+feedback.getItinerario()+" | "+feedback.getDescricao()+" | "+feedback.getImagem());
    }

    public void carregarRuas(String itinerario){
        this.ruas = appDatabase.paradaItinerarioDAO().listarRuasPorItinerario(itinerario);
    }

    public Bitmap preProcessarFoto(Bitmap foto){
        nomeTemp = UUID.randomUUID().toString()+".temp.png";
        File file = new File(getApplication().getFilesDir(),  nomeTemp);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            foto = foto.createScaledBitmap(foto, (int) (foto.getWidth() * 0.1), (int) (foto.getHeight() * 0.1), true);
            foto.compress(Bitmap.CompressFormat.PNG, 10, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();

                    return foto;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    public void salvarFoto(FeedbackItinerario feedback, Bitmap foto) {
        FileOutputStream fos = null;
        File file = new File(getApplication().getFilesDir(),  UUID.randomUUID().toString()+".png");

        try {
            fos = new FileOutputStream(file);

            if(foto.getHeight() > 768 || foto.getWidth() > 1024){
                foto = foto.createScaledBitmap(foto, (int) (foto.getWidth() * 0.3), (int) (foto.getHeight() * 0.3), true);
            }

            foto.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();

                    feedback.setImagem(file.getName());
                    feedback.setImagemEnviada(false);

                    if(nomeTemp != null && !nomeTemp.trim().isEmpty()){

                        File temp = new File(getApplication().getFilesDir(), nomeTemp);

                        if(temp != null && temp.exists() && temp.isFile()){
                            temp.delete();
                        }

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                retorno.setValue(0);
            }
        }
    }

    public void carregarHorariosFiltrados(String itinerario, String itinerarioARemover, String paradaPartida, String paradaDestino, boolean trechoIsolado,
                                          String partidaConsulta, String destinoConsulta){
        new carregaHorariosAsyncTask(appDatabase, itinerario, itinerarioARemover, paradaPartida, paradaDestino, trechoIsolado, partidaConsulta, destinoConsulta).execute();
    }

    private static class carregaHorariosAsyncTask extends AsyncTask<List<ItinerarioPartidaDestino>, Void, Void> {

        private AppDatabase db;
        private String itinerario;
        private String itinerarioARemover = null;

        private String bairroPartida;
        private String bairroDestino;

        boolean trechoIsolado;

        private String partidaConsulta;
        private String destinoConsulta;

        carregaHorariosAsyncTask(AppDatabase appDatabase, String itinerario, String itinerarioARemover, String partida, String destino, boolean trechoIsolado, String partidaConsulta, String destinoConsulta) {
            db = appDatabase;
            this.itinerario = itinerario;
            this.itinerarioARemover = itinerarioARemover;

            this.bairroPartida = partida;
            this.bairroDestino = destino;

            this.trechoIsolado = trechoIsolado;
            this.partidaConsulta = partidaConsulta;
            this.destinoConsulta = destinoConsulta;
        }

        carregaHorariosAsyncTask(AppDatabase appDatabase, String itinerario, String itinerarioARemover, boolean trechoIsolado, String partidaConsulta, String destinoConsulta) {
            db = appDatabase;
            this.itinerario = itinerario;
            this.itinerarioARemover = itinerarioARemover;
            this.trechoIsolado = trechoIsolado;
            this.partidaConsulta = partidaConsulta;
            this.destinoConsulta = destinoConsulta;
        }

        @Override
        protected Void doInBackground(final List<ItinerarioPartidaDestino>... params) {

            Long ini = System.nanoTime();

            System.out.println("TEMPO ========================================================================");

            System.out.println("TEMPO INICIAL: "+ini);
            System.out.println("TEMPO INICIAL PARADAS: "+ini);

            List<ParadaBairro> paradas = db.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairroSimplificadoSync(itinerario);

            Long finPar = System.nanoTime();
            Long totPar = finPar - ini;

            System.out.println("TEMPO FINAL PARADAS: "+finPar);
            System.out.println("TEMPO TOTAL PARADAS: "+TimeUnit.SECONDS.convert(totPar, TimeUnit.NANOSECONDS));

            if(paradas.size() > 0){

                Long iniIti = System.nanoTime();

                System.out.println("TEMPO INICIAL ITI: "+iniIti);
                System.out.println("TEMPO TRECHO ISOLADO: "+trechoIsolado);

                if(trechoIsolado){
                    qtdItinerarios = db.itinerarioDAO()
                            .carregarOpcoesPorPartidaEDestinoTrechoItinerarioSync(partidaConsulta, destinoConsulta);

                    Long finIti = System.nanoTime();
                    Long totIti = finIti - iniIti;

                    System.out.println("TEMPO FINAL ITI TRECHO: "+finIti);
                    System.out.println("TEMPO TOTAL ITI TRECHO: "+TimeUnit.SECONDS.convert(totIti, TimeUnit.NANOSECONDS));

                } else if(bairroPartida != null && bairroDestino != null){
                    qtdItinerarios = db.horarioItinerarioDAO()
                            .contaItinerariosPorPartidaEDestinoSimplificadoSync(bairroPartida, bairroDestino);

                    Long finIti = System.nanoTime();
                    Long totIti = finIti - iniIti;

                    System.out.println("TEMPO FINAL ITI: "+finIti);
                    System.out.println("TEMPO TOTAL ITI: "+TimeUnit.SECONDS.convert(totIti, TimeUnit.NANOSECONDS));

                } else{

                   // if(qtdItinerarios == null || qtdItinerarios.size() == 0){
                        qtdItinerarios = db.itinerarioDAO().listarTodosAtivosPorIdSync(itinerario);

                        Long finIti = System.nanoTime();
                        Long totIti = finIti - iniIti;

                        System.out.println("TEMPO FINAL ITI SOLO: "+finIti);
                        System.out.println("TEMPO TOTAL ITI SOLO: "+TimeUnit.SECONDS.convert(totIti, TimeUnit.NANOSECONDS));

                    //}

                }

                if(itinerarioARemover != null && !itinerarioARemover.isEmpty()){

                    if(bairroPartida != null && bairroDestino != null){

                        Long iniHor = System.nanoTime();

                        System.out.println("TEMPO INICIAL HOR: "+iniHor);

                        horarios.postValue(db.horarioItinerarioDAO().listarApenasAtivosPorPartidaEDestinoFiltradoSync(bairroPartida,
                                bairroDestino, itinerarioARemover));

                        Long finHor = System.nanoTime();
                        Long totHor = finHor - iniHor;

                        System.out.println("TEMPO FINAL HOR: "+finHor);
                        System.out.println("TEMPO TOTAL HOR: "+TimeUnit.SECONDS.convert(totHor, TimeUnit.NANOSECONDS));
                    } else{
                        Long iniHor = System.nanoTime();
                        System.out.println("TEMPO INICIAL HOR: "+iniHor);

                        horarios.postValue(db.horarioItinerarioDAO().listarApenasAtivosPorItinerarioFiltradoSync(itinerario, itinerarioARemover));

                        Long finHor = System.nanoTime();
                        Long totHor = finHor - iniHor;

                        System.out.println("TEMPO FINAL HOR: "+finHor);
                        System.out.println("TEMPO TOTAL HOR: "+TimeUnit.SECONDS.convert(totHor, TimeUnit.NANOSECONDS));
                    }


                } else{

                    if(bairroPartida != null && bairroDestino != null){

                        Long iniHor = System.nanoTime();

                        System.out.println("TEMPO INICIAL HOR: "+iniHor);

                        if(trechoIsolado){

                            List<HorarioItinerarioNome> hrs = db.horarioItinerarioDAO().listarApenasAtivosPorItinerarioTrechoSync(db.itinerarioDAO().carregarOpcoesPorPartidaEDestinoTrechoSync(partidaConsulta, destinoConsulta));

                            horarios.postValue(hrs);
                        } else{
                            Long iniHor2 = System.nanoTime();
                            System.out.println("TEMPO INICIAL HOR: "+iniHor2);

                            horarios.postValue(db.horarioItinerarioDAO().listarApenasAtivosPorPartidaEDestinoSync(bairroPartida, bairroDestino));

                            Long finHor = System.nanoTime();
                            Long totHor = finHor - iniHor2;

                            System.out.println("TEMPO FINAL HOR: "+finHor);
                            System.out.println("TEMPO TOTAL HOR: "+TimeUnit.SECONDS.convert(totHor, TimeUnit.NANOSECONDS));
                        }


                        Long finHor = System.nanoTime();
                        Long totHor = finHor - iniHor;

                        System.out.println("TEMPO FINAL HOR: "+finHor);
                        System.out.println("TEMPO TOTAL HOR: "+TimeUnit.SECONDS.convert(totHor, TimeUnit.NANOSECONDS));
                    } else{

                        Long iniHor = System.nanoTime();
                        System.out.println("TEMPO INICIAL HOR: "+iniHor);

                        horarios.postValue(db.horarioItinerarioDAO().listarApenasAtivosPorItinerarioSync(itinerario));

                        Long finHor = System.nanoTime();
                        Long totHor = finHor - iniHor;

                        System.out.println("TEMPO FINAL HOR: "+finHor);
                        System.out.println("TEMPO TOTAL HOR: "+TimeUnit.SECONDS.convert(totHor, TimeUnit.NANOSECONDS));
                    }


                }


            }

            Long fin = System.nanoTime();

            Long res = fin - ini;

            System.out.println("TEMPO FINAL: "+fin);
            System.out.println("TEMPO TOTAL: "+res);
            System.out.println("TEMPO TOTAL: "+ TimeUnit.SECONDS.convert(res, TimeUnit.NANOSECONDS));
            System.out.println("TEMPO ========================================================================");

            return null;
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
                        localAtual.setValue(location);

                        if(localAtual.getValue() != null){
                            localAtual.getValue().setLatitude(localAtual.getValue().getLatitude());
                            localAtual.getValue().setLongitude(localAtual.getValue().getLongitude());
                        }

                    }

                }
            }
        };
    }

    public void atualizaPontoMapa(){

        if(paradas.getValue().size() > 0){
            List<ParadaBairro> p = paradas.getValue();
            ParadaBairro paradaInicial = p.get(0);
            ParadaBairro paradaFinal = p.get(p.size()-1);

            midPoint(paradaInicial.getParada().getLatitude(), paradaInicial.getParada().getLongitude(), paradaFinal.getParada().getLatitude(), paradaFinal.getParada().getLongitude());
        }

    }

    private void midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        GeoPoint origin = new GeoPoint(lat1,lon1);
        //create destination geopoints from parameters
        GeoPoint destination = new GeoPoint(lat2,lon2);
        //calculate and return center
        GeoPoint point = GeoPoint.fromCenterBetween(origin, destination);

        l.setLatitude(point.getLatitude());
        l.setLongitude(point.getLongitude());

        localAtual.postValue(l);

    }

    public void carregaDirections(MapView map, List<ParadaBairro> paradas) {

        new directionsAsyncTask(map, paradas, getApplication().getApplicationContext()).execute();
    }

    private static class directionsAsyncTask extends AsyncTask<String, Void, Void> {

        MapView map;
        List<ParadaBairro> paradas;
        Polyline rota;
        Context ctx;

        directionsAsyncTask(MapView map, List<ParadaBairro> paradas, Context ctx) {
            this.map = map;
            this.paradas = paradas;
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(final String... params) {
            RoadManager roadManager = new OSRMRoadManager(ctx);

            ArrayList<GeoPoint> points = new ArrayList<>();
            points.add(new GeoPoint(paradas.get(0).getParada().getLatitude(), paradas.get(0).getParada().getLongitude()));
            points.add(new GeoPoint(paradas.get(paradas.size()-1).getParada().getLatitude(), paradas.get(paradas.size()-1).getParada().getLongitude()));

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

    public void carregaSecoesPorItinerario(String itinerario){
        new carregaSecoesAsyncTask(appDatabase, itinerario);
    }

    private static class carregaSecoesAsyncTask extends AsyncTask<List<SecaoItinerarioParada>, Void, Void> {

        private AppDatabase db;
        private String itinerario;

        carregaSecoesAsyncTask(AppDatabase appDatabase, String itinerario) {
            db = appDatabase;
            this.itinerario = itinerario;
        }

        @Override
        protected Void doInBackground(final List<SecaoItinerarioParada>... params) {

            List<SecaoItinerarioParada> secoes = db.secaoItinerarioDAO().listarTodosPorItinerarioComParadaSync(itinerario);

            if(secoes.size() > 0){

            }



            return null;
        }

    }

    // adicionar

    public void add(final FeedbackItinerario feedback) {

        feedback.setDataCadastro(new DateTime());
        feedback.setUltimaAlteracao(new DateTime());
        feedback.setEnviado(false);

        feedback.setItinerario(itinerario.getValue().getItinerario().getId());

        if(feedback.getImagem() != null && !feedback.getImagem().isEmpty()){
            salvarFoto(feedback, foto);
        }

        new addAsyncTask(appDatabase).execute(feedback);
    }

    private static class addAsyncTask extends AsyncTask<FeedbackItinerario, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final FeedbackItinerario... params) {
            db.feedbackItinerarioDAO().inserir((params[0]));
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

    public static void editImagemFeedbackItinerario(final FeedbackItinerario fi, Context context) {

        if(appDatabase == null){
            appDatabase = AppDatabase.getAppDatabase(context.getApplicationContext());
        }

        new DetalhesItinerarioViewModel.editImagemFeedbackItinerarioAsyncTask(appDatabase).execute(fi);
    }

    private static class editImagemFeedbackItinerarioAsyncTask extends AsyncTask<FeedbackItinerario, Void, Void> {

        private AppDatabase db;

        editImagemFeedbackItinerarioAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final FeedbackItinerario... params) {
            db.feedbackItinerarioDAO().editar((params[0]));
            return null;
        }

    }

}
