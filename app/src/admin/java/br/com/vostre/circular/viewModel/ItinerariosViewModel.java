package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
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
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.api.CircularAPI;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.utils.DataHoraUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ItinerariosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;
    public Itinerario itinerario;

    public Double tarifaAntiga;

    public MutableLiveData<List<ParadaItinerarioBairro>> paradasItinerario;
    public ParadaItinerarioBairro parada;

    public LiveData<List<ParadaBairro>> paradas;
    public MutableLiveData<Location> localAtual;
    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    public Empresa empresa;
    public LiveData<List<Empresa>> empresas;

    public LiveData<Empresa> umaEmpresa;

    public LiveData<List<ItinerarioPartidaDestino>> getItinerarios() {
        return itinerarios;
    }

    public static MutableLiveData<Integer> retorno;

    int umMinuto = 60;

    public void setItinerarios(LiveData<List<ItinerarioPartidaDestino>> itinerarios) {
        this.itinerarios = itinerarios;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
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

    public void setEmpresa(String empresa) {
        this.umaEmpresa = appDatabase.empresaDAO().carregar(empresa);
    }

    public ItinerariosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerario = new Itinerario();
        itinerarios = appDatabase.itinerarioDAO().listarTodosComTotalHorariosSimplificado();
        paradas = appDatabase.paradaDAO().listarTodosAtivosComBairro();
        empresas = appDatabase.empresaDAO().listarTodosAtivos();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());

        localAtual = new MutableLiveData<>();
        localAtual.setValue(new Location(LocationManager.GPS_PROVIDER));

        paradasItinerario = new MutableLiveData<>();
        paradasItinerario.setValue(new ArrayList<ParadaItinerarioBairro>());

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);

    }

    public void salvarItinerario(boolean inverter){

        itinerario.setEmpresa(empresa.getId());

        if(itinerario.valida(itinerario)){
            add(itinerario);
            addParadas(this.paradasItinerario.getValue(), itinerario);
        } else{
            retorno.setValue(0);
        }

        // inversao
/*
        if(inverter){

            Itinerario itinerarioInvertido = new Itinerario();
            itinerarioInvertido.setAcessivel(itinerario.getAcessivel());
            itinerarioInvertido.setAtivo(itinerario.getAtivo());
            itinerarioInvertido.setDistancia(itinerario.getDistancia());
            itinerarioInvertido.setEmpresa(itinerario.getEmpresa());
            itinerarioInvertido.setObservacao(itinerario.getObservacao());
            itinerarioInvertido.setSigla(itinerario.getSigla());
            itinerarioInvertido.setTarifa(itinerario.getTarifa());
            itinerarioInvertido.setTempo(itinerario.getTempo());
            itinerarioInvertido.setDataCadastro(itinerario.getDataCadastro());
            itinerarioInvertido.setEnviado(itinerario.getEnviado());
            itinerarioInvertido.setProgramadoPara(itinerario.getProgramadoPara());
            itinerarioInvertido.setUltimaAlteracao(itinerario.getUltimaAlteracao());
            itinerarioInvertido.setUsuarioCadastro(itinerario.getUsuarioCadastro());
            itinerarioInvertido.setUsuarioUltimaAlteracao(itinerario.getUsuarioUltimaAlteracao());

            if(itinerarioInvertido.valida(itinerarioInvertido)){
                add(itinerarioInvertido);

                List<ParadaItinerarioBairro> paradasInvertidas = this.paradasItinerario.getValue();
                Collections.reverse(paradasInvertidas);

                addParadas(paradasInvertidas, itinerarioInvertido);

            } else{
                retorno.setValue(2);
            }

        }
*/


    }

    public void editarItinerario(){

        itinerario.setEmpresa(empresa.getId());

        if(itinerario.valida(itinerario)){
            edit(itinerario);
        } else{
            retorno.setValue(0);
        }

    }

    public void editarItinerario(Itinerario itinerario){

        if(itinerario.valida(itinerario)){
            edit(itinerario);
        } else{
            retorno.setValue(0);
        }

    }

    public void salvarParada(){

    }

    public void editarParada(){
        List<ParadaItinerarioBairro> pibs = this.paradasItinerario.getValue();
//        ParadaItinerarioBairro pib = pibs.get(pibs.indexOf(parada));
//        pib = parada;
        this.paradasItinerario.postValue(pibs);

    }

    public void atualizaTrajetoItinerarios(final String itinerario){

        if(itinerario != null && !itinerario.isEmpty()){
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    ItinerarioPartidaDestino iti = appDatabase.itinerarioDAO().carregarSync(itinerario);

                    if(iti != null){
                        Itinerario i = iti.getItinerario();

                        List<Parada> paradas = appDatabase.paradaItinerarioDAO()
                                .listarParadasAtivasPorItinerarioERuaSync(i.getId());

                        if (paradas != null) {

                            ArrayList<String> points = new ArrayList<>();

                            for(Parada p : paradas){
                                points.add(p.getLongitude()+","+p.getLatitude());
                            }

                            buscaTrajeto(i, points);

                        }

                    }

                }
            });
        } else{
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    List<Itinerario> itinerarios = appDatabase.itinerarioDAO().listarTodosSync();

                    for(Itinerario iti : itinerarios) {

                        if(iti.getTrajeto() == null || iti.getTrajeto().isEmpty()){
                            List<Parada> paradas = appDatabase.paradaItinerarioDAO()
                                    .listarParadasAtivasPorItinerarioERuaSync(iti.getId());

                            if (paradas != null) {

                                ArrayList<String> points = new ArrayList<>();

                                for(Parada p : paradas){
                                    points.add(p.getLongitude()+","+p.getLatitude());
                                }

                                buscaTrajeto(iti, points);

                            }
                        }

                        //pausa para nao sobrecarregar o servico remoto
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        }


    }

    private void buscaTrajeto(final Itinerario iti, final ArrayList<String> points) {
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

                            edit(iti);
                        }

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

    public void atualizaItinerarios() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Itinerario> itinerarios = appDatabase.itinerarioDAO().listarTodosSync();

                for(Itinerario iti : itinerarios){
                    List<ParadaItinerarioBairro> paradas = appDatabase.paradaItinerarioDAO()
                            .listarTodosPorItinerarioComBairroSync(iti.getId());

                    if(paradas != null){

                        int qtd = paradas.size();

                        if(qtd > 1){
                            ParadaItinerarioBairro paradaInicial = paradas.get(0);
                            ParadaItinerarioBairro paradaFinal = paradas.get(qtd-1);

                            iti.setTotalParadas(qtd);
                            iti.setParadaInicial(paradaInicial.getParadaItinerario().getParada());
                            iti.setParadaFinal(paradaFinal.getParadaItinerario().getParada());

                            iti.setEnviado(false);
                            iti.setUltimaAlteracao(DateTime.now());

//                            System.out.println("ITI: "+iti.getId()+" - "+iti.getTotalParadas()+" | "
//                                    +iti.getParadaInicial()+";"+iti.getParadaFinal());

                            appDatabase.itinerarioDAO().editar(iti);
                        }

                    }

                }

            }
        });

    }

    public void atualizarDistancias(Context ctx){
        new atualizaDistanciasAsyncTask(appDatabase, ctx, this).execute();
    }

    private static class atualizaDistanciasAsyncTask extends AsyncTask<List<ParadaItinerario>, Void, Void> {

        private AppDatabase db;
        private Context ctx;
        ItinerariosViewModel thiz;

        atualizaDistanciasAsyncTask(AppDatabase appDatabase, Context context, ItinerariosViewModel thiz) {
            db = appDatabase;
            ctx = context;
            this.thiz = thiz;
        }

        @Override
        protected Void doInBackground(final List<ParadaItinerario>... params) {

            List<ItinerarioPartidaDestino> itinerarios = db.itinerarioDAO().listarTodosAtivosSync();

            for(ItinerarioPartidaDestino itinerario : itinerarios){

                List<ParadaItinerarioBairro> pis = db.paradaItinerarioDAO()
                        .listarTodosPorItinerarioComBairroSync(itinerario.getItinerario().getId());

//                // atualiza distancia itinerario
//                if(itinerario.getDistanciaTrechoMetros() == null || itinerario.getItinerario().getDistanciaMetros() == null){
//                    thiz.calculaDistancia(pis, itinerario.getItinerario());
//                }

                ParadaItinerarioBairro paradaAnterior = null;

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://router.project-osrm.org/")
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
                CircularAPI api = retrofit.create(CircularAPI.class);

                // atualiza distancia paradas
                for(ParadaItinerarioBairro pb : pis){

                    if(paradaAnterior != null && paradaAnterior.getParadaItinerario().getDistanciaSeguinteMetros() == null){

                        try {
                            Thread.sleep(5000);
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
                                                db.paradaItinerarioDAO().editar(finalParadaAnterior.getParadaItinerario());
                                            }
                                        });
                                    } else{
                                        Toast.makeText(ctx, "Erro ao buscar distâncias das paradas! "+response.message(), Toast.LENGTH_SHORT).show();
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

                // atualiza distancia itinerario
                //if(itinerario.getDistanciaTrechoMetros() == null || itinerario.getItinerario().getDistanciaMetros() == null){
                    thiz.calculaDistancia(itinerario.getItinerario(), pis, false);
                //}

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ctx, "Atualização de distâncias finalizada!", Toast.LENGTH_SHORT).show();
        }

    }

    public void calculaDistancia(List<ParadaItinerarioBairro> paradas, final Itinerario itinerario) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://router.project-osrm.org/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        CircularAPI api = retrofit.create(CircularAPI.class);
        Call<String> call = api.carregaDistancia(paradas.get(0).getLongitude()
                +","+paradas.get(0).getLatitude(),paradas.get(paradas.size()-1).getLongitude()
                +","+paradas.get(paradas.size()-1).getLatitude());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {

                    if(response.body() != null){
                        JSONObject obj = new JSONObject(response.body().toString());
                        JSONArray routes = obj.getJSONArray("routes");
                        JSONObject objDados = routes.getJSONObject(0);

                        String distancia = objDados.getString("distance");
                        int tempo = objDados.getInt("duration");

                        if(tempo > umMinuto && tempo <= umMinuto * 10){
                            //tempo += 60;
                        } else if(tempo > umMinuto * 10){
                            tempo += umMinuto * (tempo % 5);
                        }

                        Double distanciaMetros = Double.parseDouble(distancia);
                        String tempoFormatado = DataHoraUtils.segundosParaHoraFormatado(tempo);
                        Double distanciaKm = distanciaMetros/1000;

                        long distMetros = (long) distanciaMetros.doubleValue();
                        long distKm = (long) distanciaKm.doubleValue();

                        // distancia em metros - v2.3
                        if(distMetros > 0){
                            itinerario.setDistanciaMetros(Double.parseDouble(String.valueOf(distMetros)));
                        }

                        // distancia em km - versoes anteriores
                        if(distKm > 0){
                            itinerario.setDistancia(Double.parseDouble(String.valueOf(distKm)));
                        }

                        if(tempoFormatado != null){
                            itinerario.setTempo(DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(tempoFormatado));
                        }

                        editarItinerario(itinerario);

                    } else{
                        Toast.makeText(getApplication().getApplicationContext(), "Erro ao buscar distância do itinerário! "+response.message(), Toast.LENGTH_SHORT).show();
                    }



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
    }

    // adicionar paradas

    public void addParadas(List<ParadaItinerarioBairro> pibs, Itinerario itinerario) {

        int cont = 1;

        for(ParadaItinerarioBairro pib : pibs){
            pib.getParadaItinerario().setItinerario(itinerario.getId());
            pib.getParadaItinerario().setOrdem(cont);

            pib.getParadaItinerario().setDataCadastro(new DateTime());
            pib.getParadaItinerario().setUltimaAlteracao(new DateTime());
            pib.getParadaItinerario().setEnviado(false);

            new addParadaAsyncTask(appDatabase).execute(pib.getParadaItinerario());

            cont++;
        }

        if(pibs != null && pibs.size() > 1){
            itinerario.setTotalParadas(pibs.size());
            itinerario.setParadaInicial(pibs.get(0).getParadaItinerario().getParada());
            itinerario.setParadaFinal(pibs.get(pibs.size()-1).getParadaItinerario().getParada());
            itinerario.setEnviado(false);
            itinerario.setUltimaAlteracao(DateTime.now());

            edit(itinerario);

            atualizaTrajetoItinerarios(itinerario.getId());

        }


    }

    private static class addParadaAsyncTask extends AsyncTask<ParadaItinerario, Void, Void> {

        private AppDatabase db;

        addParadaAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ParadaItinerario... params) {
            db.paradaItinerarioDAO().inserir(params[0]);
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
            ItinerariosViewModel.retorno.setValue(1);
        }
    }

    // fim adicionar

    // editar

    public void edit(final Itinerario itinerario) {

        itinerario.setUltimaAlteracao(new DateTime());
        itinerario.setEnviado(false);

        new editAsyncTask(appDatabase, tarifaAntiga).execute(itinerario);
    }

    private static class editAsyncTask extends AsyncTask<Itinerario, Void, Void> {

        private AppDatabase db;
        private Double tarifaAntiga;

        editAsyncTask(AppDatabase appDatabase, Double tarifaAntiga) {
            db = appDatabase;
            this.tarifaAntiga = tarifaAntiga;
        }

        @Override
        protected Void doInBackground(final Itinerario... params) {

            db.itinerarioDAO().editar((params[0]));

            if(tarifaAntiga != null && params[0].getTarifa().compareTo(tarifaAntiga) != 0){
                HistoricoItinerario historicoItinerario = new HistoricoItinerario();
                historicoItinerario.setItinerario(params[0].getId());
                historicoItinerario.setTarifa(tarifaAntiga);
                historicoItinerario.setAtivo(true);
                historicoItinerario.setEnviado(false);
                historicoItinerario.setDataCadastro(DateTime.now());
                historicoItinerario.setUltimaAlteracao(DateTime.now());

                db.historicoItinerarioDAO().inserir(historicoItinerario);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ItinerariosViewModel.retorno.setValue(1);
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

                itin.setTempo(DateTimeFormat.forPattern("HH:mm:ss")
                        .parseDateTime(printer.print(p.normalizedStandard())));

                itin.setDistanciaMetros(distancia);

                if(!isEdicao){
                    editarItinerario(itin);
                }

            }
        });

    }

}
