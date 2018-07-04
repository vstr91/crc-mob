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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import br.com.vostre.circular.utils.Crypt;
import br.com.vostre.circular.utils.JsonUtils;
import br.com.vostre.circular.utils.Unique;
import br.com.vostre.circular.view.BaseActivity;
import br.com.vostre.circular.viewModel.BaseViewModel;
import br.com.vostre.circular.viewModel.CidadesViewModel;
import br.com.vostre.circular.viewModel.EmpresasViewModel;
import br.com.vostre.circular.viewModel.ParadasViewModel;
import br.com.vostre.circular.viewModel.PontosInteresseViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

    static Context ctx;
    AppDatabase appDatabase;
    String baseUrl;

    ParametroInterno parametroInterno;
    String token;
    String tokenImagem;

    List<? extends EntidadeBase> paises;
    List<? extends EntidadeBase> empresas;
    List<? extends EntidadeBase> onibus;
    List<? extends EntidadeBase> estados;
    List<? extends EntidadeBase> cidades;
    List<? extends EntidadeBase> bairros;
    List<? extends EntidadeBase> paradas;
    List<? extends EntidadeBase> itinerarios;
    List<? extends EntidadeBase> horarios;
    List<? extends EntidadeBase> paradasItinerario;
    List<? extends EntidadeBase> secoesItinerarios;
    List<? extends EntidadeBase> horariosItinerarios;
    List<? extends EntidadeBase> mensagens;
    List<? extends EntidadeBase> parametros;
    List<? extends EntidadeBase> pontosInteresse;
    List<? extends EntidadeBase> usuarios;

    Crypt crypt;

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

        baseUrl = appDatabase.parametroDAO().carregarPorSlug("servidor");
        crypt = new Crypt();

        if(baseUrl == null){
            baseUrl = Constants.BASE_URL;
        }

        parametroInterno = appDatabase.parametroInternoDAO().carregar();

        if(parametroInterno == null){
            parametroInterno = new ParametroInterno();
            parametroInterno.setId("1");
            parametroInterno.setDataCadastro(DateTime.now());
            parametroInterno.setAtivo(true);
            parametroInterno.setEnviado(true);

            String identificadorUnico = Unique.geraIdentificadorUnico();

            parametroInterno.setIdentificadorUnico(identificadorUnico);

            parametroInterno.setDataUltimoAcesso(DateTimeFormat.forPattern("dd/mm/yyyy").parseDateTime("01/01/2000"));
            parametroInterno.setUltimaAlteracao(DateTime.now());

            appDatabase.parametroInternoDAO().inserir(parametroInterno);

        }

        System.out.println("ID UNICO >>>>>>>>>>>> "+parametroInterno.getIdentificadorUnico());

        try {
            requisitaToken(parametroInterno.getIdentificadorUnico(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {

        if(response.code() == 200){
            Toast.makeText(ctx, "Envio de dados efetuado com sucesso! Iniciando recebimento de dados...",
                    Toast.LENGTH_SHORT).show();

            chamaAPI(0, null, 1, baseUrl, token);

        } else{
            Toast.makeText(ctx, "Erro ao enviar dados. Código de resposta: "+response.code()+" | Mensagem: "+response.message(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        Toast.makeText(ctx, "Problema ao acessar para enviar dados: "+t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void requisitaToken(String id, int tipo) throws Exception{
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(baseUrl)
                .build();

        CircularAPI api = retrofit.create(CircularAPI.class);

        id = crypt.bytesToHex(crypt.encrypt(id));

        if(tipo == 0){
            Call<String> call = api.requisitaToken(id);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code() == 200){

                        token = response.body();

                        System.out.println("TOKEN "+token);

                        try {
                            token = crypt.bytesToHex(crypt.encrypt(token));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new listaDadosAsyncTask(appDatabase).execute();

                    } else{
                        Toast.makeText(getContext().getApplicationContext(),
                                "Erro "+response.code()+" ("+response.message()+") ao requisitar token.",
                                Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext().getApplicationContext(),
                            "Erro "+t.getMessage()+" ao requisitar token.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            Call<String> call = api.requisitaToken(id);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.code() == 200){

                        tokenImagem = response.body();

                        System.out.println("TOKEN IMAGEM "+tokenImagem);

                        try {
                            tokenImagem = crypt.bytesToHex(crypt.encrypt(tokenImagem));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new enviaImagemAsyncTask(appDatabase).execute();

                    } else{
                        Toast.makeText(getContext().getApplicationContext(),
                                "Erro "+response.code()+" ("+response.message()+") ao requisitar token de imagem.",
                                Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }



    }

    private void chamaAPI(Integer registros, String json, Integer tipo, String baseUrl, String token) {

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
                Call<String> call = api.enviaDados(paramObject.toString(), token);
                call.enqueue(this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            CircularAPI api = retrofit.create(CircularAPI.class);

            String data = "-";

            if(parametroInterno != null){
                data = DateTimeFormat.forPattern("yyyy-MM-dd-HH-mm-ss").print(parametroInterno.getDataUltimoAcesso());
            }

            Call<String> call = api.recebeDados(token, data);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Toast.makeText(ctx, "Recebimento de dados efetuado com sucesso! Iniciando processamento...", Toast.LENGTH_SHORT).show();
                    try {
                        requisitaToken(parametroInterno.getIdentificadorUnico(), 1);
                        processaJson(response);
                    } catch (JSONException e) {
                        Toast.makeText(ctx, "Problema ao processar dados: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
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

        System.out.println("DADOS >>>>> "+dados);

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
                    cidade.setImagemEnviada(true);

                    lstCidades.add(cidade);

                    System.out.println("CIDADE "+cidade.getBrasao());

                    if(cidade.getBrasao() != null && !cidade.getBrasao().isEmpty()){
                        File brasao = new File(getContext().getApplicationContext().getFilesDir(), cidade.getBrasao());

                        if(!brasao.exists() || !brasao.canWrite()){
                            System.out.println("BAIXANDO BRASAO");
                            imageDownload(baseUrl, cidade.getBrasao());
                        } else{
                            System.out.println("RESP BRASAO >>>>>>> "+brasao.exists());
                        }
                    }

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
                    empresa.setImagemEnviada(false);

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
                    pontoInteresse.setImagemEnviada(false);

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

    private class listaDadosAsyncTask extends AsyncTask<Void, Void, Void> {

        private AppDatabase db;

        listaDadosAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Void... params) {

            paises = appDatabase.paisDAO().listarTodosAEnviar();
            empresas = appDatabase.empresaDAO().listarTodosAEnviar();
            onibus = appDatabase.onibusDAO().listarTodosAEnviar();
            estados = appDatabase.estadoDAO().listarTodosAEnviar();
            cidades = appDatabase.cidadeDAO().listarTodosAEnviar();
            bairros = appDatabase.bairroDAO().listarTodosAEnviar();
            paradas = appDatabase.paradaDAO().listarTodosAEnviar();
            itinerarios = appDatabase.itinerarioDAO().listarTodosAEnviar();
            horarios = appDatabase.horarioDAO().listarTodosAEnviar();
            paradasItinerario = appDatabase.paradaItinerarioDAO().listarTodosAEnviar();
            secoesItinerarios = appDatabase.secaoItinerarioDAO().listarTodosAEnviar();
            horariosItinerarios = appDatabase.horarioItinerarioDAO().listarTodosAEnviar();
            mensagens = appDatabase.mensagemDAO().listarTodosAEnviar();
            parametros = appDatabase.parametroDAO().listarTodosAEnviar();
            pontosInteresse = appDatabase.pontoInteresseDAO().listarTodosAEnviar();
            usuarios = appDatabase.usuarioDAO().listarTodosAEnviar();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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

            int registros = paises.size()+empresas.size()+onibus.size()+estados.size()+cidades.size()
                    +bairros.size()+paradas.size()+itinerarios.size()+horarios.size()+paradasItinerario.size()
                    +secoesItinerarios.size()+horariosItinerarios.size()+mensagens.size()+parametros.size()+pontosInteresse.size()+usuarios.size();

            if(registros > 0){
                chamaAPI(registros, json, 0, baseUrl, token);
            } else{
                chamaAPI(0, null, 1, baseUrl, token);
            }
        }
    }

    private class enviaImagemAsyncTask extends AsyncTask<Void, Void, Void> {

        private AppDatabase db;

        enviaImagemAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        List<Empresa> empresas;
        List<Cidade> cidades;
        List<Parada> paradas;
        List<PontoInteresse> pontosInteresse;

        @Override
        protected Void doInBackground(final Void... params) {

            empresas = appDatabase.empresaDAO().listarTodosImagemAEnviar();
            cidades = appDatabase.cidadeDAO().listarTodosImagemAEnviar();
            paradas = appDatabase.paradaDAO().listarTodosImagemAEnviar();
            pontosInteresse = appDatabase.pontoInteresseDAO().listarTodosImagemAEnviar();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

            for(final Cidade cidade : cidades){

                File imagem = new File(getContext().getApplicationContext().getFilesDir(),  cidade.getBrasao());

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imagem);
                MultipartBody.Part body = MultipartBody.Part.createFormData("upload", imagem.getName(), reqFile);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

                CircularAPI api = retrofit.create(CircularAPI.class);
                Call<String> call = api.enviaImagem(body, name, tokenImagem);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.code() == 200){
                            cidade.setImagemEnviada(true);
                            CidadesViewModel.edit(cidade, getContext().getApplicationContext());
                        } else{
                            Toast.makeText(getContext().getApplicationContext(),
                                    "Erro "+response.code()+" ("+response.message()+") ao enviar imagem de "+cidade.getNome()+" para o servidor",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getContext().getApplicationContext(),
                                "Erro "+t.getMessage()+" ("+call.request().headers()+") ao enviar imagem de "+cidade.getNome()+" para o servidor",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }

            for(final Empresa empresa : empresas){

                File imagem = new File(getContext().getApplicationContext().getFilesDir(),  empresa.getLogo());

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imagem);
                MultipartBody.Part body = MultipartBody.Part.createFormData("upload", imagem.getName(), reqFile);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

                CircularAPI api = retrofit.create(CircularAPI.class);
                Call<String> call = api.enviaImagem(body, name, tokenImagem);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.code() == 200){
                            empresa.setImagemEnviada(true);
                            EmpresasViewModel.editEmpresa(empresa, getContext().getApplicationContext());
                        } else{
                            Toast.makeText(getContext().getApplicationContext(),
                                    "Erro ao enviar imagem de "+empresa.getNome()+" para o servidor",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }

            for(final Parada parada : paradas){

                File imagem = new File(getContext().getApplicationContext().getFilesDir(),  parada.getImagem());

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imagem);
                MultipartBody.Part body = MultipartBody.Part.createFormData("upload", imagem.getName(), reqFile);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

                CircularAPI api = retrofit.create(CircularAPI.class);
                Call<String> call = api.enviaImagem(body, name, tokenImagem);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.code() == 200){
                            parada.setImagemEnviada(true);
                            ParadasViewModel.edit(parada, getContext().getApplicationContext());
                        } else{
                            Toast.makeText(getContext().getApplicationContext(),
                                    "Erro ao enviar imagem de "+parada.getNome()+" para o servidor",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }

            for(final PontoInteresse pontoInteresse : pontosInteresse){

                File imagem = new File(getContext().getApplicationContext().getFilesDir(),  pontoInteresse.getImagem());

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imagem);
                MultipartBody.Part body = MultipartBody.Part.createFormData("upload", imagem.getName(), reqFile);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

                CircularAPI api = retrofit.create(CircularAPI.class);
                Call<String> call = api.enviaImagem(body, name, tokenImagem);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.code() == 200){
                            pontoInteresse.setImagemEnviada(true);
                            PontosInteresseViewModel.edit(pontoInteresse, getContext().getApplicationContext());
                        } else{
                            System.out.println("RESP IMAGEM "+response.body()+" | "+response.message()+" | "
                                    +response.headers()+" | "+call.request().url());
                            Toast.makeText(getContext().getApplicationContext(),
                                    "Erro "+response.code()+" ("+response.message()+") ao enviar imagem de "+pontoInteresse.getNome()+" para o servidor",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }

        }
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
                    //db.paisDAO().deletarTodos();
                    db.paisDAO().inserirTodos((List<Pais>) params[0]);
                    break;
                case "empresa":
                    //db.empresaDAO().deletarTodos();
                    db.empresaDAO().inserirTodos((List<Empresa>) params[0]);
                    break;
                case "onibus":
                    //db.onibusDAO().deletarTodos();
                    db.onibusDAO().inserirTodos((List<Onibus>) params[0]);
                    break;
                case "estado":
                    //db.estadoDAO().deletarTodos();
                    db.estadoDAO().inserirTodos((List<Estado>) params[0]);
                    break;
                case "cidade":
                    //db.cidadeDAO().deletarTodos();
                    db.cidadeDAO().inserirTodos((List<Cidade>) params[0]);
                    break;
                case "bairro":
                    //db.bairroDAO().deletarTodos();
                    db.bairroDAO().inserirTodos((List<Bairro>) params[0]);
                    break;
                case "parada":
                    //db.paradaDAO().deletarTodos();
                    db.paradaDAO().inserirTodos((List<Parada>) params[0]);
                    break;
                case "itinerario":
                    //db.itinerarioDAO().deletarTodos();
                    db.itinerarioDAO().inserirTodos((List<Itinerario>) params[0]);
                    break;
                case "horario":
                    //db.horarioDAO().deletarTodos();
                    db.horarioDAO().inserirTodos((List<Horario>) params[0]);
                    break;
                case "parada_itinerario":
                    //db.paradaItinerarioDAO().deletarTodos();
                    db.paradaItinerarioDAO().inserirTodos((List<ParadaItinerario>) params[0]);
                    break;
                case "secao_itinerario":
                    //db.secaoItinerarioDAO().deletarTodos();
                    db.secaoItinerarioDAO().inserirTodos((List<SecaoItinerario>) params[0]);
                    break;
                case "horario_itinerario":
                    //db.horarioItinerarioDAO().deletarTodos();
                    db.horarioItinerarioDAO().inserirTodos((List<HorarioItinerario>) params[0]);
                    break;
                case "mensagem":
                    //db.mensagemDAO().deletarTodos();
                    db.mensagemDAO().inserirTodos((List<Mensagem>) params[0]);
                    break;
                case "parametro":
                    //db.parametroDAO().deletarTodos();
                    db.parametroDAO().inserirTodos((List<Parametro>) params[0]);
                    break;
                case "ponto_interesse":
                    //db.pontoInteresseDAO().deletarTodos();
                    db.pontoInteresseDAO().inserirTodos((List<PontoInteresse>) params[0]);
                    break;
                case "usuario":
                    //db.usuarioDAO().deletarTodos();
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

    public static void imageDownload(String baseUrl, final String imagem){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(baseUrl)
                .build();

        CircularAPI api = retrofit.create(CircularAPI.class);
        Call<ResponseBody> call = api.recebeImagem(imagem.replace(".png", ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println("RESP IMAGEM >>>>>>>> "+response.message()+" | "+response.code());

                if(response.code() == 200){
                    FileOutputStream fos = null;
                    File file = new File(ctx.getApplicationContext().getFilesDir(), imagem);

                    try {
                        fos = new FileOutputStream(file);
                        Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fos != null) {
                                fos.close();
                                Toast.makeText(ctx, "Imagem "+imagem+" recebida.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("RESP IMAGEM ERRO >>>>>>>>>>> "+t.getMessage());
            }
        });
    }

}