package br.com.vostre.circular.utils;

import android.app.Activity;
import androidx.lifecycle.ViewModelProviders;
import androidx.sqlite.db.SimpleSQLiteQuery;

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
import br.com.vostre.circular.model.FeedbackItinerario;
import br.com.vostre.circular.model.Feriado;
import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.HistoricoSecao;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.Parametro;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.Problema;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.Servico;
import br.com.vostre.circular.model.TipoProblema;
import br.com.vostre.circular.model.Tpr;
import br.com.vostre.circular.model.Tpr2;
import br.com.vostre.circular.model.Usuario;
import br.com.vostre.circular.model.ViagemItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.log.LogItinerario;
import br.com.vostre.circular.model.log.LogParada;
import br.com.vostre.circular.view.BaseActivity;
import br.com.vostre.circular.viewModel.BaseViewModel;

public class DBUtils {

    static String DATABASE_NAME = "circular";

    public static void exportDB(Context ctx) {
        try {
            AppDatabase.getAppDatabase(ctx).close();
            File dbFile = new File(ctx.getDatabasePath(DATABASE_NAME).getAbsolutePath());
            FileInputStream fis = new FileInputStream(dbFile);

            File f = new File(Environment.getExternalStorageDirectory(), DATABASE_NAME+"_exp.db");

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

        //v2.3.x
        JSONArray historicosParadas = arrayObject.getJSONArray("historicos_paradas");
        JSONArray historicosItinerarios = arrayObject.getJSONArray("historicos_itinerarios");
        JSONArray tiposProblema = arrayObject.getJSONArray("tipos_problemas");
        JSONArray problemas = arrayObject.getJSONArray("problemas");
        JSONArray servicos = arrayObject.getJSONArray("servicos");
        JSONArray feriados = arrayObject.getJSONArray("feriados");
        JSONArray historicosSecoes = arrayObject.getJSONArray("historicos_secoes");

        //v2.4.x
//        JSONArray logsItinerarios = arrayObject.getJSONArray("logs_itinerarios");
//        JSONArray logsParadas = arrayObject.getJSONArray("logs_paradas");

        JSONArray imagensParadas = arrayObject.getJSONArray("imagens_paradas");
        JSONArray feedbacksItinerarios = arrayObject.getJSONArray("feedbacks_itinerarios");

        JSONArray tprs = arrayObject.getJSONArray("tprs");
        JSONArray tpr2s = arrayObject.getJSONArray("tpr2s");

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

        // HISTORICOS ITINERARIOS

        if(historicosItinerarios != null && historicosItinerarios.length() > 0){

            int total = historicosItinerarios.length();
            List<HistoricoItinerario> lstHistoricos = new ArrayList<>();

            for(int i = 0; i < total; i++){
                HistoricoItinerario historicoItinerario;
                JSONObject obj = historicosItinerarios.getJSONObject(i);

                historicoItinerario = (HistoricoItinerario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), HistoricoItinerario.class, 0);

                lstHistoricos.add(historicoItinerario);

            }

            viewModel.add(lstHistoricos, "historico_itinerario");

        }

        // TIPOS PROBLEMA

        if(tiposProblema != null && tiposProblema.length() > 0){

            int total = tiposProblema.length();
            List<TipoProblema> lstTiposProblema = new ArrayList<>();

            for(int i = 0; i < total; i++){
                TipoProblema tipoProblema;
                JSONObject obj = tiposProblema.getJSONObject(i);

                tipoProblema = (TipoProblema) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), TipoProblema.class, 0);
                tipoProblema.setEnviado(true);

                lstTiposProblema.add(tipoProblema);

            }

            viewModel.add(lstTiposProblema, "tipo_problema");

        }

        // PROBLEMAS

        if(problemas != null && problemas.length() > 0){

            int total = problemas.length();
            List<Problema> lstProblemas = new ArrayList<>();

            //System.out.println("PROBLEMAS: "+problemas.toString());

            for(int i = 0; i < total; i++){
                Problema problema;
                JSONObject obj = problemas.getJSONObject(i);

                problema = (Problema) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Problema.class, 0);

                lstProblemas.add(problema);

//                if(problema.getImagem() != null && !problema.getImagem().isEmpty()){
//                    File imagem = new File(.getApplicationContext().getFilesDir(), problema.getImagem());
//
//                    if(!imagem.exists() || !imagem.canWrite()){
//                        imageDownload(baseUrl, problema.getImagem());
//                    }
//                }

            }

            viewModel.add(lstProblemas, "problema");

        }

        // FERIADOS

        if(feriados.length() > 0){

            int total = feriados.length();
            List<Feriado> lstFeriados = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Feriado feriado;
                JSONObject obj = feriados.getJSONObject(i);

                feriado = (Feriado) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Feriado.class, 0);

                lstFeriados.add(feriado);

            }

            viewModel.add(lstFeriados, "feriado");

        }

        // HISTORICOS SECOES

        if(historicosSecoes != null && historicosSecoes.length() > 0){

            int total = historicosSecoes.length();
            List<HistoricoSecao> lstHistoricosSecoes = new ArrayList<>();

            for(int i = 0; i < total; i++){
                HistoricoSecao historicoSecao;
                JSONObject obj = historicosSecoes.getJSONObject(i);

                historicoSecao = (HistoricoSecao) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), HistoricoSecao.class, 0);

                lstHistoricosSecoes.add(historicoSecao);

            }

            viewModel.add(lstHistoricosSecoes, "historico_secao");

        }

        // SERVICOS

        if(servicos.length() > 0){

            int total = servicos.length();
            List<Servico> lstServicos = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Servico servico;
                JSONObject obj = servicos.getJSONObject(i);

                servico = (Servico) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Servico.class, 0);

                lstServicos.add(servico);

            }

            viewModel.add(lstServicos, "servico");

        }

        // v2.4.x
//        // LOGS ITINERARIOS
//
//        if(logsItinerarios != null && logsItinerarios.length() > 0){
//
//            int total = logsItinerarios.length();
//            List<LogItinerario> lstLogsItinerarios = new ArrayList<>();
//
//            for(int i = 0; i < total; i++){
//                LogItinerario log;
//                JSONObject obj = logsItinerarios.getJSONObject(i);
//
//                log = (LogItinerario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), LogItinerario.class, 1);
//                log.setEnviado(true);
//
//                lstLogsItinerarios.add(log);
//
//            }
//
//            viewModel.add(lstLogsItinerarios, "log_itinerario");
//
//        }
//
//        // LOGS PARADAS
//
//        if(logsParadas != null && logsParadas.length() > 0){
//
//            int total = logsParadas.length();
//            List<LogParada> lstLogsParadas = new ArrayList<>();
//
//            for(int i = 0; i < total; i++){
//                LogParada log;
//                JSONObject obj = logsParadas.getJSONObject(i);
//
//                log = (LogParada) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), LogParada.class, 1);
//                log.setEnviado(true);
//
//                lstLogsParadas.add(log);
//
//            }
//
//            viewModel.add(lstLogsParadas, "log_parada");
//
//        }

        // IMAGENS PARADAS

        if(imagensParadas.length() > 0){

            int total = imagensParadas.length();
            List<ImagemParada> lstImagensParadas = new ArrayList<>();

            for(int i = 0; i < total; i++){
                ImagemParada parada;
                JSONObject obj = imagensParadas.getJSONObject(i);

                parada = (ImagemParada) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), ImagemParada.class, 0);
                parada.setEnviado(true);
                parada.setImagemEnviada(true);

                lstImagensParadas.add(parada);

            }

            viewModel.add(lstImagensParadas, "imagem_parada");

        }

        // FEEDBACKS ITINERARIOS

        if(feedbacksItinerarios.length() > 0){

            int total = feedbacksItinerarios.length();
            List<FeedbackItinerario> lstFeedbacksItinerarios = new ArrayList<>();

            for(int i = 0; i < total; i++){
                FeedbackItinerario fi;
                JSONObject obj = feedbacksItinerarios.getJSONObject(i);

                fi = (FeedbackItinerario) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), FeedbackItinerario.class,  0);
                fi.setEnviado(true);
                fi.setImagemEnviada(true);

                lstFeedbacksItinerarios.add(fi);

            }

            viewModel.add(lstFeedbacksItinerarios, "feedback_itinerario");

        }

        // TPR 1

        if(tprs.length() > 0){

            int total = tprs.length();
            List<Tpr> lstTprs = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Tpr tpr;
                JSONObject obj = tprs.getJSONObject(i);

                tpr = (Tpr) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Tpr.class, 0);
                tpr.setEnviado(true);
                tpr.setTarifa(0d);

                lstTprs.add(tpr);
            }

            viewModel.add(lstTprs, "tpr");

        }

        // TPR 2

        if(tpr2s.length() > 0){

            int total = tpr2s.length();
            List<Tpr2> lstTpr2s = new ArrayList<>();

            for(int i = 0; i < total; i++){
                Tpr2 tpr2;
                JSONObject obj = tpr2s.getJSONObject(i);

                tpr2 = (Tpr2) br.com.vostre.circular.utils.JsonUtils.fromJson(obj.toString(), Tpr2.class, 0);
                tpr2.setEnviado(true);

                lstTpr2s.add(tpr2);
            }

            viewModel.add(lstTpr2s, "tpr2");

        }

        PreferenceUtils.salvarPreferencia(activity.getApplicationContext(), "init", true);

    }

    public static String geraTabelaTemp(){
        return "CREATE TABLE IF NOT EXISTS tpr(" +
                "  distanciaAcumuladaInicial REAL," +
                "  distanciaAcumulada REAL," +
                "  tempo INT," +
                "  id TEXT," +
                "  idBairroPartida TEXT," +
                "  idBairroDestino TEXT," +
                "  distanciaTrechoMetros," +
                "  tempoTrecho," +
                "  tarifaTrecho," +
                "  inicio," +
                "  fim" +
                ")";
    }

    public static String populaTabelaTemp(){
        return "INSERT INTO tpr (id, distanciaAcumuladaInicial, distanciaAcumulada, tempo, idItinerario, idBairroPartida, idBairroDestino, " +
                "             distanciaTrechoMetros, tempoTrecho, tarifaTrecho, inicio, fim, ativo, enviado, data_cadastro, ultima_alteracao) " +
                "                                               SELECT lower(hex( randomblob(4)) || '-' || hex( randomblob(2)) || '-' || '4' || " +
                "                                                substr( hex( randomblob(2)), 2) || '-' || substr('AB89', 1 + (abs(random()) % 4) , 1)  || " +
                "                                                substr(hex(randomblob(2)), 2) || '-' || hex(randomblob(6))), " +
                "                              (   " +
                "                                                SELECT pi2.distanciaAcumulada FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario  " +
                "                                                INNER JOIN bairro b2 ON b2.id = p2.bairro  " +
                "                                                WHERE pi2.ordem =  (  " +
                "                                                SELECT MAX(pi2.ordem) FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario  " +
                "                                                INNER JOIN bairro b2 ON b2.id = p2.bairro   " +
                "                                                WHERE b2.id = ( SELECT b2.id FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario  " +
                "                                                                INNER JOIN bairro b2 ON b2.id = p2.bairro   " +
                "                                                                WHERE pi2.ordem = (SELECT MAX(pi.ordem))  " +
                "                                                                )  " +
                "                                               )  " +
                "                                               ) AS 'distanciaAcumuladaInicial',  " +
                "                               (  " +
                "                                                SELECT pi2.distanciaAcumulada FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario  " +
                "                                                INNER JOIN bairro b2 ON b2.id = p2.bairro   " +
                "                                                WHERE pi2.ordem =  (   " +
                "                                                SELECT MAX(pi2.ordem) FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
                "                                                INNER JOIN bairro b2 ON b2.id = p2.bairro   " +
                "                                                WHERE b2.id = ( SELECT b2.id FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario  " +
                "                                                                INNER JOIN bairro b2 ON b2.id = p2.bairro   " +
                "                                                                WHERE pi2.ordem = (SELECT MAX(pi.ordem)+1)  " +
                "                                                                )  " +
                "                                               )  " +
                "                                               ) - (  " +
                "                                                SELECT pi2.distanciaAcumulada FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario  " +
                "                                                INNER JOIN bairro b2 ON b2.id = p2.bairro   " +
                "                                                WHERE pi2.ordem =  (   " +
                "                                                SELECT MAX(pi2.ordem) FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario  " +
                "                                                INNER JOIN bairro b2 ON b2.id = p2.bairro   " +
                "                                                WHERE b2.id = ( SELECT b2.id FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario  " +
                "                                                                INNER JOIN bairro b2 ON b2.id = p2.bairro   " +
                "                                                                WHERE pi2.ordem = (SELECT MAX(pi.ordem))   " +
                "                                                                )  " +
                "                                               )  " +
                "                                               ) AS 'distanciaAcumulada',   " +

                "                              i.tempo AS 'tempo',  " +
                "                                               i.id,  " +
                "                                               b.id as 'idBairroPartida',  " +
                "                                               (   " +
                "                                                SELECT b2.id FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
                "                                                INNER JOIN bairro b2 ON b2.id = p2.bairro  " +
                "                                                WHERE pi2.ordem = (SELECT MAX(pi.ordem)+1)   " +
                "                                               ) AS 'idBairroDestino',  " +

                "                                                IFNULL(SUM(pi.distanciaSeguinteMetros), 0) AS 'distanciaTrechoMetros',  " +
                "                                                IFNULL(SUM(pi.tempoSeguinte), 0) AS 'tempoTrecho', " +
                "                                               IFNULL(SUM(pi.valorSeguinte), 0) AS 'tarifaTrecho',  " +
                "                                               MIN(pi.ordem) AS 'inicio', MAX(pi.ordem) AS 'fim', 1, 0, datetime('now'), datetime('now')  " +

                "                                                FROM parada_itinerario pi INNER JOIN   " +
                "                                                    parada p ON p.id = pi.parada INNER JOIN  " +
                "                                                    bairro b ON b.id = p.bairro INNER JOIN  " +
                "                                                    itinerario i ON i.id = pi.itinerario  " +

                "                                                WHERE pi.ativo = 1 AND i.ativo = 1  " +
                "                                                GROUP BY i.id, b.id  " +
                "                                                HAVING idBairroDestino != ''  " +
                "                                                ORDER BY pi.itinerario, pi.ordem;";
    }

    public static String geraTabelaTemp2(){
        return " CREATE TABLE IF NOT EXISTS tpr2 (id TEXT, distanciaMetros REAL, idBairroPartida TEXT, " +
                " idBairroDestino TEXT, distanciaTrechoMetros REAL, flagTrecho INT);";
    }

    public static String populaTabelaTemp2(){
        return "INSERT INTO tpr2 (id, idItinerario, distanciaMetros, idBairroPartida, idBairroDestino, distanciaTrechoMetros, flagTrecho, ativo, enviado, data_cadastro, ultima_alteracao) " +
                "                                               SELECT DISTINCT lower(hex( randomblob(4)) || '-' || hex( randomblob(2)) || '-' || '4' ||  " +
                "                                                substr( hex( randomblob(2)), 2) || '-' || substr('AB89', 1 + (abs(random()) % 4) , 1)  ||  " +
                "                                                substr(hex(randomblob(2)), 2) || '-' || hex(randomblob(6))), t1.id, 0,   " +
                "                                                      t1.idBairroPartida,   " +
                "                                                      t2.idBairroDestino,  " +

                "                                                      t2.distanciaAcumuladaInicial + t2.distanciaAcumulada - t1.distanciaAcumuladaInicial,  " +
                "                                                      CASE WHEN (t1.inicio = 1 AND (   " +
                "                                                                 t2.idBairroDestino = (   " +
                "                                                                                          SELECT p3.bairro  " +
                "                                                                                            FROM parada_itinerario pi3  " +
                "                                                                                                 INNER JOIN  " +
                "                                                                                                 parada p3 ON p3.id = pi3.parada  " +
                "                                                                                           WHERE pi3.itinerario = t1.id  " +
                "                                                                                           ORDER BY pi3.ordem DESC  " +
                "                                                                                           LIMIT 1  " +
                "                                                                                      ) )  " +
                "                                                          ) THEN 0 ELSE 1 END, 1, 0, datetime('now'), datetime('now') " +
                "                                                 FROM tpr t1 INNER JOIN  " +
                "                                                      tpr t2 ON t1.id = t2.id AND   " +
                "                                                                t1.fim <= t2.fim WHERE t1.ativo = 1 AND t2.ativo = 1  " +
                "                                                ORDER BY t1.id,   " +
                "                                                         t1.inicio,  " +
                "                                                         t2.inicio";
    }

    public static void criaTabelasTemporarias(AppDatabase db){

        // deleta tabelas (estrutura mudou)
        SimpleSQLiteQuery queryDelTemp = new SimpleSQLiteQuery("DROP TABLE IF EXISTS tpr");
        db.itinerarioDAO().geraTabelaTemp(queryDelTemp);

        SimpleSQLiteQuery queryDelTemp2 = new SimpleSQLiteQuery("DROP TABLE IF EXISTS tpr2");
        db.itinerarioDAO().geraTabelaTemp(queryDelTemp2);
        // fim deleta tabela

        SimpleSQLiteQuery queryTemp = new SimpleSQLiteQuery(geraTabelaTemp());
        db.itinerarioDAO().geraTabelaTemp(queryTemp);

        SimpleSQLiteQuery queryTemp2 = new SimpleSQLiteQuery(geraTabelaTemp2());
        db.itinerarioDAO().geraTabelaTemp(queryTemp2);
    }

    public static void iniciaTabelasTemporarias(AppDatabase appDatabase, boolean criaTabelas) {

        if(criaTabelas){
            criaTabelasTemporarias(appDatabase);
        }

        appDatabase.itinerarioDAO().deletaTabelaTemp(new SimpleSQLiteQuery("DELETE FROM tpr"));

        SimpleSQLiteQuery queryPopula = new SimpleSQLiteQuery(populaTabelaTemp());
        appDatabase.itinerarioDAO().populaTabelaTemp(queryPopula);

        // temp 2
        appDatabase.itinerarioDAO().deletaTabelaTemp(new SimpleSQLiteQuery("DELETE FROM tpr2"));

        SimpleSQLiteQuery queryPopula2 = new SimpleSQLiteQuery(populaTabelaTemp2());
        appDatabase.itinerarioDAO().populaTabelaTemp(queryPopula2);
    }

}
