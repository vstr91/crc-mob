package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;

import androidx.databinding.ObservableField;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimePrinter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.FeedbackItinerario;
import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.api.CircularAPI;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.model.pojo.TrechoPartidaDestino;
import br.com.vostre.circular.utils.DataHoraUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DetalhesItinerarioViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<ItinerarioPartidaDestino> itinerario;

    public MutableLiveData<List<ParadaItinerarioBairro>> paradasItinerario;
    public LiveData<List<ParadaItinerarioBairro>> pits;
    public ParadaItinerarioBairro parada;

    public LiveData<List<ParadaBairro>> paradas;
    public MutableLiveData<Location> localAtual;
    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    public Empresa empresa;
    public LiveData<List<Empresa>> empresas;

    public static MutableLiveData<Integer> retorno;

    public ObservableField<ItinerarioPartidaDestino> iti;

    public LiveData<List<HistoricoItinerario>> historicoItinerario;

    int umMinuto = 60;

    public LiveData<List<TrechoPartidaDestino>> trechos;

    public ObservableField<ItinerarioPartidaDestino> getIti() {
        return iti;
    }

    public void setIti(ObservableField<ItinerarioPartidaDestino> iti) {
        this.iti = iti;
    }

    public LiveData<ItinerarioPartidaDestino> getItinerario() {
        return itinerario;
    }

    public void setItinerario(String itinerario) {
        this.itinerario = appDatabase.itinerarioDAO().carregar(itinerario);
        this.pits = appDatabase.paradaItinerarioDAO().listarTodosAtivosPorItinerarioComBairro(itinerario);
        this.historicoItinerario = appDatabase.historicoItinerarioDAO().carregarPorItinerario(itinerario);
        this.trechos = appDatabase.temporariasDAO().listarTodosTemp2PorItinerario(itinerario);
//        paradasItinerario.setValue(appDatabase.paradaItinerarioDAO()
//                .listarTodosAtivosPorItinerarioComBairro(itinerario).getValue());
    }

    public LiveData<List<ParadaItinerarioBairro>> getParadasItinerario() {
        return paradasItinerario;
    }

    public void setParadasItinerario(MutableLiveData<List<ParadaItinerarioBairro>> paradasItinerario) {
        this.paradasItinerario = paradasItinerario;
    }

    public ParadaItinerarioBairro getParada() {
        return parada;
    }

    public void setParada(ParadaItinerarioBairro parada) {
        this.parada = parada;
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
        paradas = appDatabase.paradaDAO().listarTodosAtivosComBairro();
        empresas = appDatabase.empresaDAO().listarTodosAtivos();
        trechos = appDatabase.temporariasDAO().listarTodosTemp2PorItinerario("");

        iti = new ObservableField<>(new ItinerarioPartidaDestino());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());

        localAtual = new MutableLiveData<>();
        localAtual.setValue(new Location(LocationManager.GPS_PROVIDER));

        paradasItinerario = new MutableLiveData<>();
        paradasItinerario.setValue(new ArrayList<ParadaItinerarioBairro>());

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public static void editImagemFeedbackItinerario(FeedbackItinerario fi, Context ctx){

    }

    public void atualizaParadasItinerario(final String itinerario){
        List<ParadaItinerarioBairro> pis = appDatabase.paradaItinerarioDAO()
                .listarTodosPorItinerarioComBairroSync(itinerario);

        ParadaItinerarioBairro paradaAnterior = null;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://router.project-osrm.org/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        CircularAPI api = retrofit.create(CircularAPI.class);

        for(ParadaItinerarioBairro pb : pis){

            if(paradaAnterior != null){

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Call<String> call = api.carregaDistancia(paradaAnterior.getLongitude()+","
                        +paradaAnterior.getLatitude(),pb.getLongitude()+","+pb.getLatitude());

                final ParadaItinerarioBairro finalParadaAnterior = paradaAnterior;
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        System.out.println(response);
                        try {

                            if(response.body() != null){
                                JSONObject obj = new JSONObject(response.body().toString());
                                JSONArray routes = obj.getJSONArray("routes");
                                JSONObject objDados = routes.getJSONObject(0);

                                String distancia = objDados.getString("distance");
                                int tempo = objDados.getInt("duration");

                                //somando 45% para simular tempo de onibus. Padrao da api eh carro
                                tempo = tempo + ((Double)(tempo * 0.45d)).intValue();

                                Double distanciaMetros = Double.parseDouble(distancia);
                                String tempoFormatado = DataHoraUtils.segundosParaHoraFormatado(tempo);
                                Double distanciaKm = distanciaMetros/1000;

                                long distMetros = (long) distanciaMetros.doubleValue();
                                long distKm = (long) distanciaKm.doubleValue();

                                if(distMetros > 0){
                                    finalParadaAnterior.getParadaItinerario()
                                            .setDistanciaSeguinteMetros(Double.parseDouble(String.valueOf(distMetros)));
                                }

                                if(distKm > 0){
                                    finalParadaAnterior.getParadaItinerario()
                                            .setDistanciaSeguinte(Double.parseDouble(String.valueOf(distKm)));
                                }

                                if(tempoFormatado != null){
                                    finalParadaAnterior.getParadaItinerario()
                                            .setTempoSeguinte(DateTimeFormat.forPattern("HH:mm:ss")
                                                    .parseDateTime(tempoFormatado));
                                }

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        finalParadaAnterior.getParadaItinerario().setEnviado(false);
                                        finalParadaAnterior.getParadaItinerario().setUltimaAlteracao(DateTime.now());
                                        appDatabase.paradaItinerarioDAO().editar(finalParadaAnterior.getParadaItinerario());
                                    }
                                });
                            } else{
                                Toast.makeText(getApplication().getApplicationContext(), "Erro ao buscar distâncias das paradas! "+response.message(), Toast.LENGTH_SHORT).show();
                            }

//                            viewModel.editarItinerario();

                            //System.out.println(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        System.out.println(call.request().body());
                    }
                });

//                System.out.println("PARADAS ITINERARIO::: "
//                        +paradaAnterior.getNomeParada()+" - "+paradaAnterior.getNomeBairro()
//                        +", "+paradaAnterior.getNomeCidade()
//                        +", ("+paradaAnterior.getLatitude()+";"+paradaAnterior.getLongitude()+") || "+pb.getNomeParada()+" - "+pb.getNomeBairro()
//                        +", "+pb.getNomeCidade()+", ("+pb.getLatitude()+";"+pb.getLongitude()+")");
            }

            paradaAnterior = pb;

        }

    }

    public void salvarItinerario(){

        Itinerario umItinerario = itinerario.getValue().getItinerario();
        umItinerario.setEmpresa(empresa.getId());

        if(umItinerario.valida(umItinerario)){
            add(umItinerario);
            addParadas(this.paradasItinerario.getValue());

            calculaDistancia(umItinerario, this.paradasItinerario.getValue(), false);

        } else{
            retorno.setValue(0);
        }

    }

    public void editarItinerario(DateTime dt){

        Itinerario umItinerario = itinerario.getValue().getItinerario();

        umItinerario.setTempo(dt);

        if(empresa != null){
            umItinerario.setEmpresa(empresa.getId());
        }


        if(umItinerario.valida(umItinerario)){
            iti.get().setItinerario(umItinerario);
            edit(umItinerario);
        } else{
            retorno.setValue(0);
        }

    }

    public void salvarParadas(){
        addParadas(this.paradasItinerario.getValue());
    }

    public void editarParada(){
        List<ParadaItinerarioBairro> pibs = this.paradasItinerario.getValue();
//        ParadaItinerarioBairro pib = pibs.get(pibs.indexOf(parada));
//        pib = parada;
        this.paradasItinerario.postValue(pibs);
        this.parada = null;

    }

    // adicionar paradas

    public void addParadas(List<ParadaItinerarioBairro> pibs) {

        int cont = 1;

        new invalidaParadasAsyncTask(appDatabase).execute(itinerario.getValue().getItinerario());

        for(ParadaItinerarioBairro pib : pibs){
            pib.getParadaItinerario().setItinerario(itinerario.getValue().getItinerario().getId());
            pib.getParadaItinerario().setOrdem(cont);
            pib.getParadaItinerario().setAtivo(true);

            pib.getParadaItinerario().setDataCadastro(new DateTime());
            pib.getParadaItinerario().setUltimaAlteracao(new DateTime());
            pib.getParadaItinerario().setEnviado(false);

            new addParadaAsyncTask(appDatabase).execute(pib.getParadaItinerario());

            cont++;
        }

        if(pibs != null && pibs.size() > 1){
            // atualiza paradas inicial e final do itinerario, assim como numero de paradas
            final Itinerario iti = itinerario.getValue().getItinerario();

            iti.setTotalParadas(pibs.size());
            iti.setParadaInicial(pibs.get(0).getParadaItinerario().getParada());
            iti.setParadaFinal(pibs.get(pibs.size()-1).getParadaItinerario().getParada());

            iti.setUltimaAlteracao(DateTime.now());
            iti.setEnviado(false);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    appDatabase.itinerarioDAO().editar(iti);
                    atualizaTrajetoItinerarios(iti.getId());

                    atualizaDistanciaAcumulada(iti.getId());
                    atualizaTemporarias(iti.getId());
                }
            });
        }

        paradasItinerario.postValue(appDatabase.paradaItinerarioDAO().listarTodosAtivosPorItinerarioComBairro(itinerario.getValue().getItinerario().getId()).getValue());

    }

    private static class invalidaParadasAsyncTask extends AsyncTask<Itinerario, Void, Void> {

        private AppDatabase db;

        invalidaParadasAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Itinerario... params) {

            db.paradaItinerarioDAO().invalidaTodosPorItinerario(params[0].getId());

            return null;
        }

    }

    private static class addParadaAsyncTask extends AsyncTask<ParadaItinerario, Void, Void> {

        private AppDatabase db;

        addParadaAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ParadaItinerario... params) {

            ParadaItinerario paradaItinerario = db.paradaItinerarioDAO().checaDuplicidade(params[0].getParada(), params[0].getItinerario());

            if(paradaItinerario != null){
                ParadaItinerario pi = params[0];

                paradaItinerario.setAtivo(true);
                paradaItinerario.setOrdem(pi.getOrdem());
                paradaItinerario.setDestaque(pi.getDestaque());
                paradaItinerario.setValorAnterior(pi.getValorAnterior());
                paradaItinerario.setValorSeguinte(pi.getValorSeguinte());
                paradaItinerario.setEnviado(false);
                paradaItinerario.setDistanciaSeguinte(pi.getDistanciaSeguinte());
                paradaItinerario.setDistanciaSeguinteMetros(pi.getDistanciaSeguinteMetros());
                paradaItinerario.setTempoSeguinte(pi.getTempoSeguinte());
                paradaItinerario.setUltimaAlteracao(DateTime.now());

                db.paradaItinerarioDAO().editar(paradaItinerario);
            } else{
                db.paradaItinerarioDAO().inserir(params[0]);
            }


            return null;
        }

    }

    // fim adicionar paradas

    // adicionar

    public void add(final Itinerario itinerario) {

        itinerario.setDataCadastro(new DateTime());
        itinerario.setUltimaAlteracao(new DateTime());
        itinerario.setEnviado(false);

        new addAsyncTask(appDatabase).execute(itinerario);
    }

    private static class addAsyncTask extends AsyncTask<Itinerario, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Itinerario... params) {
            db.itinerarioDAO().inserir((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DetalhesItinerarioViewModel.retorno.setValue(1);
        }
    }

    // fim adicionar

    // editar

    public void edit(final Itinerario itinerario) {

        itinerario.setUltimaAlteracao(new DateTime());
        itinerario.setEnviado(false);

        new editAsyncTask(appDatabase).execute(itinerario);
    }

    private static class editAsyncTask extends AsyncTask<Itinerario, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Itinerario... params) {
            db.itinerarioDAO().editar((params[0]));
            return null;
        }

    }

    // fim editar

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

    public void calculaDistancia(final Itinerario itin, List<ParadaItinerarioBairro> paradas, final boolean isEdicao) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplication());

                List<ParadaItinerarioBairro> pis = db.paradaItinerarioDAO()
                        .listarTodosPorItinerarioComBairroSync(itin.getId());

                Double distancia = 0d;
                DateTime tempo = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime("00:00:00");

                Period p = Period.ZERO;
                PeriodFormatter parser = new PeriodFormatterBuilder().appendHours()
                        .appendLiteral(":").appendMinutes()
                        .appendLiteral(":").appendSeconds().toFormatter();

                PeriodFormatter printer =
                        new PeriodFormatterBuilder()
                                .printZeroAlways().minimumPrintedDigits(2)
                                //.appendDays().appendLiteral(":") // remove original code
                                .appendHours().appendLiteral(":")
                                .appendMinutes().appendLiteral(":")
                                .appendSeconds().toFormatter();

                for(ParadaItinerarioBairro pi : pis){

                    if(pi.getParadaItinerario().getDistanciaSeguinteMetros() != null){
                        distancia += pi.getParadaItinerario().getDistanciaSeguinteMetros();
                    }

                    if(pi.getParadaItinerario().getTempoSeguinte() != null){
                        p = p.plus(parser.parsePeriod(DateTimeFormat.forPattern("HH:mm:ss").print(pi.getParadaItinerario().getTempoSeguinte())));
                    }

                }

                String a = printer.print(p.normalizedStandard());

                DateTime dt = DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(a);

                itinerario.getValue().getItinerario().setTempo(dt);

                itinerario.getValue().getItinerario().setDistanciaMetros(distancia);

                if(!isEdicao){
                    editarItinerario(dt);
                }

            }
        });

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://router.project-osrm.org/")
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//        CircularAPI api = retrofit.create(CircularAPI.class);
//        Call<String> call = api.carregaDistancia(paradas.get(0).getLongitude()
//                +","+paradas.get(0).getLatitude(),paradas.get(paradas.size()-1).getLongitude()
//                +","+paradas.get(paradas.size()-1).getLatitude());
//
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//
//                try {
//
//                    if(response.body() != null){
//                        JSONObject obj = new JSONObject(response.body().toString());
//                        JSONArray routes = obj.getJSONArray("routes");
//                        JSONObject objDados = routes.getJSONObject(0);
//
//                        String distancia = objDados.getString("distance");
//                        int tempo = objDados.getInt("duration");
//
//                        //somando 1/3 para simular tempo de onibus. Padrao da api eh carro
//                        tempo = tempo + tempo/3;
//
////                        if(tempo > umMinuto && tempo <= umMinuto * 10){
////                            //tempo += 60;
////                        } else if(tempo > umMinuto * 10){
////                            tempo += umMinuto * (tempo % 5);
////                        }
//
//                        Double distanciaMetros = Double.parseDouble(distancia);
//                        String tempoFormatado = DataHoraUtils.segundosParaHoraFormatado(tempo);
//                        Double distanciaKm = distanciaMetros/1000;
//
//                        long distMetros = (long) distanciaMetros.doubleValue();
//                        long distKm = (long) distanciaKm.doubleValue();
//
//                        // distancia em metros - v2.3
//                        if(distMetros > 0){
//                            itinerario.getValue().getItinerario()
//                                    .setDistanciaMetros(Double.parseDouble(String.valueOf(distMetros)));
//                        }
//
//                        // distancia em km - versoes anteriores
//                        if(distKm > 0){
//                            itinerario.getValue().getItinerario()
//                                    .setDistancia(Double.parseDouble(String.valueOf(distKm)));
//                        }
//
//                        if(tempoFormatado != null){
//                            itinerario.getValue().getItinerario()
//                                    .setTempo(DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(tempoFormatado));
//                        }
//
//                        if(!isEdicao){
//                            editarItinerario();
//                        }
//                    } else{
//                        Toast.makeText(getApplication().getApplicationContext(), "Erro ao buscar distância do itinerário! "+response.message(), Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                System.out.println(call.request().body());
//            }
//        });
    }

    public void carregaDirections(MapView map, Itinerario itinerario) {

        //new directionsAsyncTask(map, itinerario, appDatabase, getApplication().getApplicationContext()).execute();
    }

    private static class directionsAsyncTask extends AsyncTask<String, Void, Void> {

        MapView map;
        Itinerario itinerario;
        Polyline rota;
        Context ctx;
        AppDatabase db;

        directionsAsyncTask(MapView map, Itinerario itinerario, AppDatabase db, Context ctx) {
            this.map = map;
            this.itinerario = itinerario;
            this.ctx = ctx;
            this.db = db;
        }

        @Override
        protected Void doInBackground(final String... params) {
            List<Parada> paradas = db.paradaItinerarioDAO().listarParadasAtivasPorItinerarioERuaSync(itinerario.getId());

            ArrayList<String> points = new ArrayList<>();

            for(Parada p : paradas){
                points.add(p.getLongitude()+","+p.getLatitude());
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://router.project-osrm.org/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            CircularAPI api = retrofit.create(CircularAPI.class);
            Call<String> call = api.carregaCaminhoItinerario(StringUtils.join(points, ";"));

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {

                        if(response.body() != null && response.code() == 200){
                            JSONObject obj = new JSONObject(response.body().toString());
                            JSONArray routes = obj.getJSONArray("routes");
                            String objDados = routes.getJSONObject(0).getString("geometry");

                            List<GeoPoint> pontos = PolylineEncoder.decode(objDados, 10, false);

                            Polyline line = new Polyline();
                            line.setPoints(pontos);
                            line.getOutlinePaint().setStrokeWidth(20);
                            line.getOutlinePaint().setColor(Color.RED);

                            line.getOutlinePaint().setStrokeJoin(Paint.Join.ROUND);
                            line.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);

                            if(map != null){
                                map.getOverlayManager().add(line);
                                map.invalidate();
                            }

                            System.out.printf(objDados);

//                            if(!isEdicao){
//                                editarItinerario();
//                            }
                        } else{
                            Toast.makeText(ctx.getApplicationContext(), "Erro ao buscar o caminho do itinerário! "+response.message(), Toast.LENGTH_SHORT).show();
                        }



                        //System.out.println(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });

            //PolylineEncoder.decode("", 3, false);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

//            if(map != null){
//                map.getOverlays().add(rota);
//                map.invalidate();
//            }

        }
    }

    public void atualizaTrajetoItinerarios(final String itinerario){

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    ItinerarioPartidaDestino iti = appDatabase.itinerarioDAO().carregarSync(itinerario);

                    if(iti != null){
                        Itinerario i = iti.getItinerario();

//                        List<Parada> paradas = appDatabase.paradaItinerarioDAO()
//                                .listarParadasAtivasPorItinerarioERuaSync(i.getId());

                        List<Parada> paradas = appDatabase.paradaItinerarioDAO()
                                .listarParadasAtivasPorItinerarioSync(i.getId());

                        Parada paradaInicial = appDatabase.paradaDAO().carregarSync(iti.getItinerario().getParadaInicial());
                        Parada paradaFinal = appDatabase.paradaDAO().carregarSync(iti.getItinerario().getParadaFinal());

                        if (paradas != null) {

                            ArrayList<String> points = new ArrayList<>();

//                            if(paradaInicial != null && !paradaInicial.equals(paradas.get(0))){
//                                points.add(paradaInicial.getLongitude()+","+paradaInicial.getLatitude());
//                            }

                            for(Parada p : paradas){
                                points.add(p.getLongitude()+","+p.getLatitude());
                            }

//                            if(paradaFinal != null && !paradaFinal.equals(paradas.get(paradas.size()-1))){
//                                points.add(paradaFinal.getLongitude()+","+paradaFinal.getLatitude());
//                            }

                            buscaTrajeto(i, points);

                        }

                    }

                }
            });

    }

    public void atualizaDistanciaAcumulada(final String itinerario){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplication().getApplicationContext());
                db.paradaItinerarioDAO().calculaDistanciaAcumuladaPorItinerario(itinerario);

                db.paradaItinerarioDAO().atualizaDistanciaItinerario(itinerario);
            }
        });
    }

    public void atualizaTemporarias(final String itinerario){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplication().getApplicationContext());

                // invalidando dados do itinerario nas views
                db.temporariasDAO().invalidaTemporariaPorItinerario(itinerario);
                db.temporariasDAO().invalidaTemporaria2PorItinerario(itinerario);

                // salvando dados novos na view
                db.temporariasDAO().atualizaTemporariaPorItinerario(itinerario);
                db.temporariasDAO().atualizaTemporaria2PorItinerario(itinerario);

            }
        });
    }

    private void buscaTrajeto(final Itinerario iti, ArrayList<String> points) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://router.project-osrm.org/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        CircularAPI api = retrofit.create(CircularAPI.class);
        Call<String> call = api.carregaCaminhoItinerario(StringUtils.join(points, ";"));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

                    if(response.body() != null && response.code() == 200){
                        JSONObject obj = new JSONObject(response.body().toString());
                        JSONArray routes = obj.getJSONArray("routes");
                        String polyline = routes.getJSONObject(0).getString("geometry");

                        if(polyline != null && !polyline.isEmpty()){
                            iti.setTrajeto(polyline);
                        } else{
                            iti.setTrajeto(null);
                        }

                        ItinerarioPartidaDestino itin = itinerario.getValue();

                        if(itin != null){
                            iti.setDistanciaMetros(itin.getItinerario().getDistanciaMetros());
                            iti.setTempo(itin.getItinerario().getTempo());
                        }

                        edit(iti);

//                            if(!isEdicao){
//                                editarItinerario();
//                            }
                    } else{
                        Toast.makeText(getApplication().getApplicationContext(), "Erro ao buscar o caminho do itinerário! "+response.message(), Toast.LENGTH_SHORT).show();
                    }



                    //System.out.println(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void atualizaPontoMapa(){

        if(paradasItinerario.getValue().size() > 0){
            List<ParadaItinerarioBairro> p = paradasItinerario.getValue();
            ParadaItinerarioBairro paradaInicial = p.get(0);
            ParadaItinerarioBairro paradaFinal = p.get(p.size()-1);

            midPoint(Double.parseDouble(paradaInicial.getLatitude()), Double.parseDouble(paradaInicial.getLongitude()),
                    Double.parseDouble(paradaFinal.getLatitude()), Double.parseDouble(paradaFinal.getLongitude()));
        }

    }

    private void midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        GeoPoint origin = new GeoPoint(lat1,lon1);
        //create destination geopoints from parameters
        GeoPoint destination = new GeoPoint(lat2,lon2);
        //calculate and return center
        GeoPoint point = GeoPoint.fromCenterBetween(origin, destination);

        Location l = new Location(LocationManager.NETWORK_PROVIDER);
        l.setLatitude(point.getLatitude());
        l.setLongitude(point.getLongitude());

        localAtual.postValue(l);

    }


}
