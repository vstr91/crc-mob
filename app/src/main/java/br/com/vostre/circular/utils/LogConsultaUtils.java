package br.com.vostre.circular.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

    public static LogConsulta iniciaLog(Context ctx){
        LogConsulta log = new LogConsulta();
        log.setDataInicio(DateTime.now());
        log.setUid(PreferenceUtils.carregarUsuarioLogado(ctx));
        log.setVersao(BuildConfig.VERSION_CODE+";"+BuildConfig.VERSION_NAME);

        return log;
    }

    public static void finalizaLog(Context ctx, LogConsulta log, String local){
        log.setDataFim(DateTime.now());
        log.setLocal(local);

        logaConsulta(log, ctx);
    }

}