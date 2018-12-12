package br.com.vostre.circular.utils;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Empresa;
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
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.Usuario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.view.BaseActivity;
import br.com.vostre.circular.viewModel.BaseViewModel;

public class DBUtils {

    static String DATABASE_NAME = "circular";

    public static void exportDB(Context ctx) {
        try {
            AppDatabase.getAppDatabase(ctx).close();
            File dbFile = new File(ctx.getDatabasePath(DATABASE_NAME).getAbsolutePath());
            FileInputStream fis = new FileInputStream(dbFile);

            File f = new File(Environment.getExternalStorageDirectory(), DATABASE_NAME+".db");

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(f);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            // Close the streams
            output.flush();
            output.close();
            fis.close();


        } catch (IOException e) {
            Log.e("dbBackup:", e.getMessage());
        }
    }

    public static boolean moveBancoDeDados(Context ctx){

        try {
            String nomeDbInterno = Environment.getDataDirectory() + "/data/"+ctx.getPackageName()+"/databases/circular";
            InputStream dbExterno = ctx.getAssets().open("circular");
            OutputStream dbInterno = new FileOutputStream(nomeDbInterno);

            int tamanho = dbExterno.available();

            byte[] buffer = new byte[tamanho];
            int length;

            while ((length = dbExterno.read(buffer)) > 0){
                dbInterno.write(buffer, 0, length);
            }

            dbInterno.flush();
            dbInterno.close();
            dbExterno.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void populaBancoDeDados(Activity activity){

        try {

            BufferedReader r = new BufferedReader(
                    new InputStreamReader(activity.getAssets().open("dados.txt")));
            StringBuilder dados = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                dados.append(line).append('\n');
            }

            processaJson((BaseActivity) activity, dados);

            //Toast.makeText(ctx, "Finalizou!", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    static void processaJson(BaseActivity activity, StringBuilder dados) throws JSONException {

        BaseViewModel viewModel = ViewModelProviders.of(activity).get(BaseViewModel.class);

        JSONObject arrayObject = new JSONObject(dados.toString());
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

                pais = (Pais) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Pais.class, 0);

                lstPaises.add(pais);

            }

            viewModel.add(lstPaises, "pais");

        }

        // ESTADOS

        if(estados.length() > 0){

            int total = estados.length();
            List<Estado> lstEstados = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Estado estado;
                JSONObject obj = estados.getJSONObject(i);

                estado = (Estado) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Estado.class, 0);

                lstEstados.add(estado);

            }

            viewModel.add(lstEstados, "estado");

        }

        // CIDADES

        if(cidades.length() > 0){

            int total = cidades.length();
            List<Cidade> lstCidades = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Cidade cidade;
                JSONObject obj = cidades.getJSONObject(i);

                cidade = (Cidade) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Cidade.class, 0);

                lstCidades.add(cidade);

            }

            viewModel.add(lstCidades, "cidade");

        }

        // BAIRROS

        if(bairros.length() > 0){

            int total = bairros.length();
            List<Bairro> lstBairros = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Bairro bairro;
                JSONObject obj = bairros.getJSONObject(i);

                bairro = (Bairro) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Bairro.class, 0);

                lstBairros.add(bairro);

            }

            viewModel.add(lstBairros, "bairro");

        }

        // PARADAS

        if(paradas.length() > 0){

            int total = paradas.length();
            List<Parada> lstParadas = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Parada parada;
                JSONObject obj = paradas.getJSONObject(i);

                parada = (Parada) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Parada.class, 0);

                lstParadas.add(parada);

            }

            viewModel.add(lstParadas, "parada");

        }

        // EMPRESAS

        if(empresas.length() > 0){

            int total = empresas.length();
            List<Empresa> lstEmpresas = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Empresa empresa;
                JSONObject obj = empresas.getJSONObject(i);

                empresa = (Empresa) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Empresa.class, 0);

                lstEmpresas.add(empresa);

            }

            viewModel.add(lstEmpresas, "empresa");

        }

        // ONIBUS

        if(onibus.length() > 0){

            int total = onibus.length();
            List<Onibus> lstOnibus = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Onibus umOnibus;
                JSONObject obj = onibus.getJSONObject(i);

                umOnibus = (Onibus) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Onibus.class, 0);

                lstOnibus.add(umOnibus);

            }

            viewModel.add(lstOnibus, "onibus");

        }

        // ITINERARIOS

        if(itinerarios.length() > 0){

            int total = itinerarios.length();
            List<Itinerario> lstItinerarios = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Itinerario itinerario;
                JSONObject obj = itinerarios.getJSONObject(i);

                itinerario = (Itinerario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Itinerario.class, 0);

                lstItinerarios.add(itinerario);

            }

            viewModel.add(lstItinerarios, "itinerario");

        }

        // HORARIOS

        if(horarios.length() > 0){

            int total = horarios.length();
            List<Horario> lstHorarios = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Horario horario;
                JSONObject obj = horarios.getJSONObject(i);

                horario = (Horario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Horario.class, 0);

                lstHorarios.add(horario);

            }

            viewModel.add(lstHorarios, "horario");

        }

        // PARADAS ITINERARIOS

        if(paradasItinerarios.length() > 0){

            int total = paradasItinerarios.length();
            List<ParadaItinerario> lstParadasItinerarios = new ArrayList<>();

            for(int i = 0; i < total; i++){
                ParadaItinerario paradaItinerario;
                JSONObject obj = paradasItinerarios.getJSONObject(i);

                paradaItinerario = (ParadaItinerario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), ParadaItinerario.class, 0);

                lstParadasItinerarios.add(paradaItinerario);

            }

            viewModel.add(lstParadasItinerarios, "parada_itinerario");

        }

        // SECOES ITINERARIOS

        if(secoesItinerarios.length() > 0){

            int total = secoesItinerarios.length();
            List<SecaoItinerario> lstSecoesItinerarios = new ArrayList<>();

            for(int i = 0; i < total; i++){
                SecaoItinerario secaoItinerario;
                JSONObject obj = secoesItinerarios.getJSONObject(i);

                secaoItinerario = (SecaoItinerario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), SecaoItinerario.class, 0);

                lstSecoesItinerarios.add(secaoItinerario);

            }

            viewModel.add(lstSecoesItinerarios, "secao_itinerario");

        }

        // HORARIOS ITINERARIOS

        if(horariosItinerarios.length() > 0){

            int total = horariosItinerarios.length();
            List<HorarioItinerario> lstHorariosItinerarios = new ArrayList<>();

            for(int i = 0; i < total; i++){
                HorarioItinerario horarioItinerario;
                JSONObject obj = horariosItinerarios.getJSONObject(i);

                horarioItinerario = (HorarioItinerario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), HorarioItinerario.class, 0);

                lstHorariosItinerarios.add(horarioItinerario);

            }

            viewModel.add(lstHorariosItinerarios, "horario_itinerario");

        }

        // MENSAGENS

        if(mensagens.length() > 0){

            int total = mensagens.length();
            List<Mensagem> lstMensagens = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Mensagem mensagem;
                JSONObject obj = mensagens.getJSONObject(i);

                mensagem = (Mensagem) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Mensagem.class, 0);

                lstMensagens.add(mensagem);

            }

            viewModel.add(lstMensagens, "mensagem");

        }

        // PARAMETROS

        if(parametros.length() > 0){

            int total = parametros.length();
            List<Parametro> lstParametros = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Parametro parametro;
                JSONObject obj = parametros.getJSONObject(i);

                parametro = (Parametro) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Parametro.class, 0);

                lstParametros.add(parametro);

            }

            viewModel.add(lstParametros, "parametro");

        }

        // PONTOS INTERESSE

        if(pontosInteresse.length() > 0){

            int total = pontosInteresse.length();
            List<PontoInteresse> lstPontosInteresse = new ArrayList<>();

            for(int i = 0; i < total; i++){
                PontoInteresse pontoInteresse;
                JSONObject obj = pontosInteresse.getJSONObject(i);

                pontoInteresse = (PontoInteresse) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), PontoInteresse.class, 0);

                lstPontosInteresse.add(pontoInteresse);

            }

            viewModel.add(lstPontosInteresse, "ponto_interesse");

        }

        // USUARIOS

        if(usuarios.length() > 0){

            int total = usuarios.length();
            List<Usuario> lstUsuarios = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Usuario usuario;
                JSONObject obj = usuarios.getJSONObject(i);

                usuario = (Usuario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Usuario.class, 0);

                lstUsuarios.add(usuario);

            }

            viewModel.add(lstUsuarios, "usuario");

        }

        PreferenceUtils.salvarPreferencia(activity.getApplicationContext(), "init", true);

    }

}
