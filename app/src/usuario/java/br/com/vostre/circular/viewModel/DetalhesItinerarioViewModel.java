package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.SecaoItinerarioParada;

public class DetalhesItinerarioViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

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

    public void setItinerario(String itinerario, String paradaPartida, String paradaDestino,
                              String bairroPartida, String bairroDestino) {
        this.itinerario = appDatabase.itinerarioDAO().carregar(itinerario);

        if(trechoIsolado){
            this.paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairroTrecho(partidaConsulta, destinoConsulta);
        } else{
            this.paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro(itinerario);
        }


        this.ruas = appDatabase.paradaItinerarioDAO().listarRuasPorItinerario(itinerario);

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
        itinerario = appDatabase.itinerarioDAO().carregar("");
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

            List<ParadaBairro> paradas = db.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairroSync(itinerario);

            Long finPar = System.nanoTime();
            Long totPar = finPar - ini;

            System.out.println("TEMPO FINAL PARADAS: "+finPar);
            System.out.println("TEMPO TOTAL PARADAS: "+TimeUnit.SECONDS.convert(totPar, TimeUnit.NANOSECONDS));

            if(paradas.size() > 0){

                Long iniIti = System.nanoTime();

                System.out.println("TEMPO INICIAL ITI: "+iniIti);

                if(trechoIsolado){
                    qtdItinerarios = db.itinerarioDAO()
                            .carregarOpcoesPorPartidaEDestinoTrechoItinerarioSync(partidaConsulta, destinoConsulta);
                } else{
                    qtdItinerarios = db.horarioItinerarioDAO()
                            .contaItinerariosPorPartidaEDestinoSimplificadoSync(bairroPartida, bairroDestino);
                }



                Long finIti = System.nanoTime();
                Long totIti = finIti - iniIti;

                System.out.println("TEMPO FINAL ITI: "+finIti);
                System.out.println("TEMPO TOTAL ITI: "+TimeUnit.SECONDS.convert(totIti, TimeUnit.NANOSECONDS));

//                SimpleSQLiteQuery queryOpcoes = new SimpleSQLiteQuery(
//                        ItinerariosViewModel.geraQueryItinerarios(bairroPartida, bairroDestino));
//
//                qtdItinerarios = db.itinerarioDAO()
//                        .carregarOpcoesPorPartidaEDestinoSync(queryOpcoes);

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
                        horarios.postValue(db.horarioItinerarioDAO().listarApenasAtivosPorItinerarioFiltradoSync(itinerario, itinerarioARemover));
                    }


                } else{

                    if(bairroPartida != null && bairroDestino != null){

                        Long iniHor = System.nanoTime();

                        System.out.println("TEMPO INICIAL HOR: "+iniHor);

                        if(trechoIsolado){

                            List<HorarioItinerarioNome> hrs = db.horarioItinerarioDAO().listarApenasAtivosPorItinerarioTrechoSync(db.itinerarioDAO().carregarOpcoesPorPartidaEDestinoTrechoSync(partidaConsulta, destinoConsulta));

                            horarios.postValue(hrs);
                        } else{
                            horarios.postValue(db.horarioItinerarioDAO().listarApenasAtivosPorPartidaEDestinoSync(bairroPartida, bairroDestino));
                        }


                        Long finHor = System.nanoTime();
                        Long totHor = finHor - iniHor;

                        System.out.println("TEMPO FINAL HOR: "+finHor);
                        System.out.println("TEMPO TOTAL HOR: "+TimeUnit.SECONDS.convert(totHor, TimeUnit.NANOSECONDS));
                    } else{
                        horarios.postValue(db.horarioItinerarioDAO().listarApenasAtivosPorItinerarioSync(itinerario));
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
        List<ParadaBairro> p = paradas.getValue();
        ParadaBairro paradaInicial = p.get(0);
        ParadaBairro paradaFinal = p.get(p.size()-1);

        midPoint(paradaInicial.getParada().getLatitude(), paradaInicial.getParada().getLongitude(), paradaFinal.getParada().getLatitude(), paradaFinal.getParada().getLongitude());

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

}
