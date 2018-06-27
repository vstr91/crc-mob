package br.com.vostre.circular.model.adapter;

import android.accounts.Account;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.WorkerThread;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.EntidadeBase;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.Parametro;
import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.Usuario;
import br.com.vostre.circular.model.api.CircularAPI;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.utils.Constants;
import br.com.vostre.circular.utils.JsonUtils;
import br.com.vostre.circular.utils.Unique;
import br.com.vostre.circular.view.BaseActivity;
import br.com.vostre.circular.viewModel.BaseViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Header;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter implements Callback<String> {

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    Context ctx;
    AppDatabase appDatabase;
    String baseUrl;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        ctx = context.getApplicationContext();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

        appDatabase = AppDatabase.getAppDatabase(this.getContext());

        List<? extends EntidadeBase> paises = appDatabase.paisDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> empresas = appDatabase.empresaDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> onibus = appDatabase.onibusDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> estados = appDatabase.estadoDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> cidades = appDatabase.cidadeDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> bairros = appDatabase.bairroDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> paradas = appDatabase.paradaDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> itinerarios = appDatabase.itinerarioDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> horarios = appDatabase.horarioDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> paradasItinerario = appDatabase.paradaItinerarioDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> secoesItinerarios = appDatabase.secaoItinerarioDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> horariosItinerarios = appDatabase.horarioItinerarioDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> mensagens = appDatabase.mensagemDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> parametros = appDatabase.parametroDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> pontosInteresse = appDatabase.pontoInteresseDAO().listarTodosAEnviar();
        List<? extends EntidadeBase> usuarios = appDatabase.usuarioDAO().listarTodosAEnviar();

        String strPaises = "\"paises\": "+JsonUtils.toJson((List<EntidadeBase>) paises);
        String strEmpresas = "\"empresas\": "+JsonUtils.toJson((List<EntidadeBase>) empresas);
        String strOnibus = "\"onibus\": "+JsonUtils.toJson((List<EntidadeBase>) onibus);
        String strEstados = "\"estados\": "+JsonUtils.toJson((List<EntidadeBase>) estados);
        String strCidades = "\"cidades\": "+JsonUtils.toJson((List<EntidadeBase>) cidades);
        String strBairros = "\"bairros\": "+JsonUtils.toJson((List<EntidadeBase>) bairros);
        String strParadas = "\"paradas\": "+JsonUtils.toJson((List<EntidadeBase>) paradas);
        String strItinerarios = "\"itinerarios\": "+JsonUtils.toJson((List<EntidadeBase>) itinerarios);
        String strHorarios = "\"horarios\": "+JsonUtils.toJson((List<EntidadeBase>) horarios);
        String strParadasItinerarios = "\"paradas_itinerarios\": "+JsonUtils.toJson((List<EntidadeBase>) paradasItinerario);
        String strSecoesItinerarios = "\"secoes_itinerarios\": "+JsonUtils.toJson((List<EntidadeBase>) secoesItinerarios);
        String strHorariosItinerarios = "\"horarios_itinerarios\": "+JsonUtils.toJson((List<EntidadeBase>) horariosItinerarios);
        String strMensagens = "\"mensagens\": "+JsonUtils.toJson((List<EntidadeBase>) mensagens);
        String strParametros = "\"parametros\": "+JsonUtils.toJson((List<EntidadeBase>) parametros);
        String strPontosInteresse = "\"pontos_interesse\": "+JsonUtils.toJson((List<EntidadeBase>) pontosInteresse);
        String strUsuarios = "\"usuarios\": "+JsonUtils.toJson((List<EntidadeBase>) usuarios);

        String json = "{"+strPaises+","+strEmpresas+","+strOnibus+","+strEstados+","+strCidades+","
                +strBairros+","+strParadas+","+strItinerarios+","+strHorarios+","+strParadasItinerarios+","
                +strSecoesItinerarios+","+strHorariosItinerarios+","+strMensagens+","+strParametros+","
                +strPontosInteresse+","+strUsuarios+"}";

        // EXPORTA ARQUIVO DE DADOS
        ///*
        File caminho = Environment.getExternalStorageDirectory();

        File arquivo = new File(caminho, "data.txt");

        FileOutputStream stream = null;

        try {
            stream = new FileOutputStream(arquivo);
            stream.write(json.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //*/

        baseUrl = appDatabase.parametroDAO().carregarPorSlug("servidor");

        if(baseUrl == null){
            baseUrl = Constants.BASE_URL;
        }

        int registros = paises.size()+empresas.size()+onibus.size()+estados.size()+cidades.size()
                +bairros.size()+paradas.size()+itinerarios.size()+horarios.size()+paradasItinerario.size()
                +secoesItinerarios.size()+horariosItinerarios.size()+mensagens.size()+parametros.size()+pontosInteresse.size()+usuarios.size();

        if(registros > 0){
            chamaAPI(registros, json, 0, baseUrl);
        } else{
            chamaAPI(0, null, 1, baseUrl);
        }


    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {

        if(response.code() == 200){
            Toast.makeText(ctx, "Envio de dados efetuado com sucesso!", Toast.LENGTH_SHORT).show();
            Toast.makeText(ctx, "Iniciando recebimento de dados...", Toast.LENGTH_SHORT).show();

            chamaAPI(0, null, 1, baseUrl);

        } else{
            Toast.makeText(ctx, "Código de resposta: "+response.code()+" | Mensagem: "+response.message(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        Toast.makeText(ctx, "Problema ao acessar: "+t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void chamaAPI(Integer registros, String json, Integer tipo, String baseUrl) {

        Gson gson;

        if(tipo == 0){
            gson = new GsonBuilder()
                    .registerTypeAdapter(DateTime.class, JsonUtils.serDateTime)
                    .registerTypeAdapter(DateTime.class, JsonUtils.deserDateTime)
                    .create();
        } else{
            gson = new GsonBuilder()
                    .registerTypeAdapter(DateTime.class, JsonUtils.serDateTime)
                    .registerTypeAdapter(DateTime.class, JsonUtils.deserDateTimeAPI)
                    .create();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        if(tipo == 0){
            JSONObject paramObject = new JSONObject();
            try {
                paramObject.put("dados", json);
                paramObject.put("qtd", registros);

                CircularAPI api = retrofit.create(CircularAPI.class);
                Call<String> call = api.enviaDados(paramObject.toString());
                call.enqueue(this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            CircularAPI api = retrofit.create(CircularAPI.class);

            ParametroInterno parametroInterno = appDatabase.parametroInternoDAO().carregar();
            String data = "-";
            System.out.println("IU ::::::: "+parametroInterno.getIdentificadorUnico());

            if(parametroInterno != null){
                data = DateTimeFormat.forPattern("yyyy-MM-dd-HH-mm-ss").print(parametroInterno.getDataUltimoAcesso());
            }

            Call<String> call = api.recebeDados(data);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Toast.makeText(ctx, "Recebimento de dados efetuado com sucesso! Iniciando processamento...", Toast.LENGTH_SHORT).show();

                    try {
                        processaJson(response);
                    } catch (JSONException e) {
                        Toast.makeText(ctx, "Problema ao processar dados: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(ctx, "Problema ao receber dados: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void processaJson(Response<String> response) throws JSONException {

        String dados = response.body();

        if(dados != null){

            JSONObject arrayObject = new JSONObject(dados);

            JSONArray paises = arrayObject.getJSONArray("paises");
            JSONArray empresas = arrayObject.getJSONArray("empresas");
            JSONArray onibus = arrayObject.getJSONArray("onibus");
            JSONArray estados = arrayObject.getJSONArray("estados");
            JSONArray cidades = arrayObject.getJSONArray("cidades");
            JSONArray bairros = arrayObject.getJSONArray("bairros");
            JSONArray paradas = arrayObject.getJSONArray("paradas");
            JSONArray itinerarios = arrayObject.getJSONArray("itinerarios");
            JSONArray horarios = arrayObject.getJSONArray("horarios");
            JSONArray paradasItinerarios = arrayObject.getJSONArray("paradas_itinerarios");
            JSONArray secoesItinerarios = arrayObject.getJSONArray("secoes_itinerarios");
            JSONArray horariosItinerarios = arrayObject.getJSONArray("horarios_itinerarios");
            JSONArray pontosInteresse = arrayObject.getJSONArray("pontos_interesse");
            JSONArray mensagens = arrayObject.getJSONArray("mensagens");
            JSONArray parametros = arrayObject.getJSONArray("parametros");
            JSONArray usuarios = arrayObject.getJSONArray("usuarios");

            // PAISES

            if(paises.length() > 0){

                int total = paises.length();
                List<Pais> lstPaises = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Pais pais;
                    JSONObject obj = paises.getJSONObject(i);

                    pais = (Pais) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Pais.class, 1);
                    pais.setEnviado(true);

                    lstPaises.add(pais);

                }

                add(lstPaises, "pais");

            }

            // ESTADOS

            if(estados.length() > 0){

                int total = estados.length();
                List<Estado> lstEstados = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Estado estado;
                    JSONObject obj = estados.getJSONObject(i);

                    estado = (Estado) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Estado.class, 1);
                    estado.setEnviado(true);

                    lstEstados.add(estado);

                }

                add(lstEstados, "estado");

            }

            // CIDADES

            if(cidades.length() > 0){

                int total = cidades.length();
                List<Cidade> lstCidades = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Cidade cidade;
                    JSONObject obj = cidades.getJSONObject(i);

                    cidade = (Cidade) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Cidade.class, 1);
                    cidade.setEnviado(true);

                    lstCidades.add(cidade);

                }

                add(lstCidades, "cidade");

            }

            // BAIRROS

            if(bairros.length() > 0){

                int total = bairros.length();
                List<Bairro> lstBairros = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Bairro bairro;
                    JSONObject obj = bairros.getJSONObject(i);

                    bairro = (Bairro) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Bairro.class, 1);
                    bairro.setEnviado(true);

                    lstBairros.add(bairro);

                }

                add(lstBairros, "bairro");

            }

            // PARADAS

            if(paradas.length() > 0){

                int total = paradas.length();
                List<Parada> lstParadas = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Parada parada;
                    JSONObject obj = paradas.getJSONObject(i);

                    parada = (Parada) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Parada.class, 1);
                    parada.setEnviado(true);

                    lstParadas.add(parada);

                }

                add(lstParadas, "parada");

            }

            // EMPRESAS

            if(empresas.length() > 0){

                int total = empresas.length();
                List<Empresa> lstEmpresas = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Empresa empresa;
                    JSONObject obj = empresas.getJSONObject(i);

                    empresa = (Empresa) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Empresa.class, 1);
                    empresa.setEnviado(true);

                    lstEmpresas.add(empresa);

                }

                add(lstEmpresas, "empresa");

            }

            // ONIBUS

            if(onibus.length() > 0){

                int total = onibus.length();
                List<Onibus> lstOnibus = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Onibus umOnibus;
                    JSONObject obj = onibus.getJSONObject(i);

                    umOnibus = (Onibus) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Onibus.class, 1);
                    umOnibus.setEnviado(true);

                    lstOnibus.add(umOnibus);

                }

                add(lstOnibus, "onibus");

            }

            // ITINERARIOS

            if(itinerarios.length() > 0){

                int total = itinerarios.length();
                List<Itinerario> lstItinerarios = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Itinerario itinerario;
                    JSONObject obj = itinerarios.getJSONObject(i);

                    itinerario = (Itinerario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Itinerario.class, 1);
                    itinerario.setEnviado(true);

                    lstItinerarios.add(itinerario);

                }

                add(lstItinerarios, "itinerario");

            }

            // HORARIOS

            if(horarios.length() > 0){

                int total = horarios.length();
                List<Horario> lstHorarios = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Horario horario;
                    JSONObject obj = horarios.getJSONObject(i);

                    horario = (Horario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Horario.class, 1);
                    horario.setEnviado(true);

                    lstHorarios.add(horario);

                }

                add(lstHorarios, "horario");

            }

            // PARADAS ITINERARIOS

            if(paradasItinerarios.length() > 0){

                int total = paradasItinerarios.length();
                List<ParadaItinerario> lstParadasItinerarios = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    ParadaItinerario paradaItinerario;
                    JSONObject obj = paradasItinerarios.getJSONObject(i);

                    paradaItinerario = (ParadaItinerario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), ParadaItinerario.class, 1);
                    paradaItinerario.setEnviado(true);

                    lstParadasItinerarios.add(paradaItinerario);

                }

                add(lstParadasItinerarios, "parada_itinerario");

            }

            // SECOES ITINERARIOS

            if(secoesItinerarios.length() > 0){

                int total = secoesItinerarios.length();
                List<SecaoItinerario> lstSecoesItinerarios = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    SecaoItinerario secaoItinerario;
                    JSONObject obj = secoesItinerarios.getJSONObject(i);

                    secaoItinerario = (SecaoItinerario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), SecaoItinerario.class, 1);
                    secaoItinerario.setEnviado(true);

                    lstSecoesItinerarios.add(secaoItinerario);

                }

                add(lstSecoesItinerarios, "secao_itinerario");

            }

            // HORARIOS ITINERARIOS

            if(horariosItinerarios.length() > 0){

                int total = horariosItinerarios.length();
                List<HorarioItinerario> lstHorariosItinerarios = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    HorarioItinerario horarioItinerario;
                    JSONObject obj = horariosItinerarios.getJSONObject(i);

                    horarioItinerario = (HorarioItinerario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), HorarioItinerario.class, 1);
                    horarioItinerario.setEnviado(true);

                    lstHorariosItinerarios.add(horarioItinerario);

                }

                add(lstHorariosItinerarios, "horario_itinerario");

            }

            // MENSAGENS

            if(mensagens.length() > 0){

                int total = mensagens.length();
                List<Mensagem> lstMensagens = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Mensagem mensagem;
                    JSONObject obj = mensagens.getJSONObject(i);

                    mensagem = (Mensagem) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Mensagem.class, 1);
                    mensagem.setEnviado(true);

                    lstMensagens.add(mensagem);

                }

                add(lstMensagens, "mensagem");

            }

            // PARAMETROS

            if(parametros.length() > 0){

                int total = parametros.length();
                List<Parametro> lstParametros = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Parametro parametro;
                    JSONObject obj = parametros.getJSONObject(i);

                    parametro = (Parametro) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Parametro.class, 1);
                    parametro.setEnviado(true);

                    lstParametros.add(parametro);

                }

                add(lstParametros, "parametro");

            }

            // PONTOS INTERESSE

            if(pontosInteresse.length() > 0){

                int total = pontosInteresse.length();
                List<PontoInteresse> lstPontosInteresse = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    PontoInteresse pontoInteresse;
                    JSONObject obj = pontosInteresse.getJSONObject(i);

                    pontoInteresse = (PontoInteresse) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), PontoInteresse.class, 1);
                    pontoInteresse.setEnviado(true);

                    lstPontosInteresse.add(pontoInteresse);

                }

                add(lstPontosInteresse, "ponto_interesse");

            }

            // USUARIOS

            if(usuarios.length() > 0){

                int total = usuarios.length();
                List<Usuario> lstUsuarios = new ArrayList<>();

                for(int i = 0; i < total; i++){
                    Usuario usuario;
                    JSONObject obj = usuarios.getJSONObject(i);

                    usuario = (Usuario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Usuario.class, 1);
                    usuario.setEnviado(true);

                    lstUsuarios.add(usuario);

                }

                add(lstUsuarios, "usuario");

            }

        }

        if(response.code() == 200){
            atualizaDataAcesso(response);
        }

    }

    private void atualizaDataAcesso(Response response){

        String date = response.headers().get("date");

        System.out.println("DATE >>>>>>>>>>>>> "+date);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            calendar.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        String dataUltimoAcesso = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm").print(calendar.getTimeInMillis());
        new addParamAsyncTask(appDatabase, calendar).execute();

    }

    // adicionar

    public void add(final List<? extends EntidadeBase> entidadeBase, String entidade) {

        new addAsyncTask(appDatabase, entidade).execute(entidadeBase);
    }

    private class addParamAsyncTask extends AsyncTask<ParametroInterno, Void, Void> {

        private AppDatabase db;
        private ParametroInterno parametro;
        private DateTime ultimoAcesso;

        addParamAsyncTask(AppDatabase appDatabase, Calendar ultimoAcesso) {
            db = appDatabase;
            this.ultimoAcesso = new DateTime(ultimoAcesso);
        }

        @Override
        protected Void doInBackground(final ParametroInterno... params) {

            parametro = appDatabase.parametroInternoDAO().carregar();

            if(parametro == null){
                parametro = new ParametroInterno();
                parametro.setId("1");
                parametro.setDataCadastro(DateTime.now());
                parametro.setAtivo(true);
                parametro.setEnviado(true);

                String identificadorUnico = Unique.geraIdentificadorUnico();

                parametro.setIdentificadorUnico(identificadorUnico);
            }

            parametro.setDataUltimoAcesso(ultimoAcesso);
            parametro.setUltimaAlteracao(DateTime.now());

            db.parametroInternoDAO().inserir(parametro);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class addAsyncTask extends AsyncTask<List<? extends EntidadeBase>, Void, Void> {

        private AppDatabase db;
        private String entidade;

        addAsyncTask(AppDatabase appDatabase, String entidade) {
            db = appDatabase;
            this.entidade = entidade;
        }

        @Override
        protected Void doInBackground(final List<? extends EntidadeBase>... params) {

            switch(entidade){
                case "pais":
                    db.paisDAO().deletarTodos();
                    db.paisDAO().inserirTodos((List<Pais>) params[0]);
                    break;
                case "empresa":
                    db.empresaDAO().deletarTodos();
                    db.empresaDAO().inserirTodos((List<Empresa>) params[0]);
                    break;
                case "onibus":
                    db.onibusDAO().deletarTodos();
                    db.onibusDAO().inserirTodos((List<Onibus>) params[0]);
                    break;
                case "estado":
                    db.estadoDAO().deletarTodos();
                    db.estadoDAO().inserirTodos((List<Estado>) params[0]);
                    break;
                case "cidade":
                    db.cidadeDAO().deletarTodos();
                    db.cidadeDAO().inserirTodos((List<Cidade>) params[0]);
                    break;
                case "bairro":
                    db.bairroDAO().deletarTodos();
                    db.bairroDAO().inserirTodos((List<Bairro>) params[0]);
                    break;
                case "parada":
                    db.paradaDAO().deletarTodos();
                    db.paradaDAO().inserirTodos((List<Parada>) params[0]);
                    break;
                case "itinerario":
                    db.itinerarioDAO().deletarTodos();
                    db.itinerarioDAO().inserirTodos((List<Itinerario>) params[0]);
                    break;
                case "horario":
                    db.horarioDAO().deletarTodos();
                    db.horarioDAO().inserirTodos((List<Horario>) params[0]);
                    break;
                case "parada_itinerario":
                    db.paradaItinerarioDAO().deletarTodos();
                    db.paradaItinerarioDAO().inserirTodos((List<ParadaItinerario>) params[0]);
                    break;
                case "secao_itinerario":
                    db.secaoItinerarioDAO().deletarTodos();
                    db.secaoItinerarioDAO().inserirTodos((List<SecaoItinerario>) params[0]);
                    break;
                case "horario_itinerario":
                    db.horarioItinerarioDAO().deletarTodos();
                    db.horarioItinerarioDAO().inserirTodos((List<HorarioItinerario>) params[0]);
                    break;
                case "mensagem":
                    db.mensagemDAO().deletarTodos();
                    db.mensagemDAO().inserirTodos((List<Mensagem>) params[0]);
                    break;
                case "parametro":
                    db.parametroDAO().deletarTodos();
                    db.parametroDAO().inserirTodos((List<Parametro>) params[0]);
                    break;
                case "ponto_interesse":
                    System.out.println("RESPONSE USUARIO");
                    db.pontoInteresseDAO().deletarTodos();
                    db.pontoInteresseDAO().inserirTodos((List<PontoInteresse>) params[0]);
                    break;
                case "usuario":
                    db.usuarioDAO().deletarTodos();
                    System.out.println("RESPONSE USUARIO");
                    db.usuarioDAO().inserirTodos((List<Usuario>) params[0]);
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    // fim adicionar

}