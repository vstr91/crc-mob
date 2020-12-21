package br.com.vostre.circular.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import br.com.vostre.circular.BuildConfig;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.log.LogConsulta;
import br.com.vostre.circular.model.log.LogItinerario;
import br.com.vostre.circular.model.log.LogParada;

public class LogConsultaUtils {

    public static void logaConsulta(final LogConsulta log, final Context ctx){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(ctx);

                if(log.getDataInicio() != null && log.getDataFim() != null){

                    if(log instanceof LogItinerario){

                        db.logConsultaDAO().inserirItinerario((LogItinerario) log);
                    } else{
                        db.logConsultaDAO().inserirParada((LogParada) log);
                    }

                }




            }
        });

    }

    public static LogConsulta iniciaLog(Context ctx, LogConsulta c){
        LogConsulta log;

        if(c instanceof LogItinerario){
            log = new LogItinerario();
        } else{
            log = new LogParada();
        }

        log.setDataInicio(DateTime.now());
        log.setUid(PreferenceUtils.carregarUsuarioLogado(ctx));

        if(log.getUid().isEmpty()){
            log.setUid(PreferenceUtils.carregarPreferencia(ctx, ctx.getPackageName()+".id_unico"));
        }

        if(log.getUid().isEmpty()){
            log.setUid("-1");
        }

        log.setVersao(BuildConfig.VERSION_CODE+";"+BuildConfig.VERSION_NAME);

        return log;
    }

    public static void finalizaLog(Context ctx, LogConsulta log, String local){
        log.setDataFim(DateTime.now());
        log.setLocal(local);

        log.setAtivo(true);
        log.setEnviado(false);
        log.setDataCadastro(DateTime.now());
        log.setUltimaAlteracao(DateTime.now());

        logaConsulta(log, ctx);
    }

    public static LogItinerario iniciaLogItinerario(String tipo, Context ctx){
        LogItinerario log = new LogItinerario();
        log = (LogItinerario) LogConsultaUtils.iniciaLog(ctx, log);
        log.setTipo(tipo);
        return log;
    }

    public static LogParada iniciaLogParada(String tipo, Context ctx){
        LogParada log = new LogParada();
        log = (LogParada) LogConsultaUtils.iniciaLog(ctx, log);
        log.setTipo(tipo);
        return log;
    }

    public static void finalizaLog(Activity ctx, LogConsulta log) {
        LogConsultaUtils.finalizaLog(ctx, log, ctx.getLocalClassName());
    }

}
