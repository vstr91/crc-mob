package br.com.vostre.circular.model.works;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.dao.AppDatabase;

public class TemporariasWorker extends Worker {

    Context ctx;


    public TemporariasWorker(@NonNull Context context,
                             @NonNull WorkerParameters params){
        super(context, params);

        this.ctx = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Result doWork() {

        AppDatabase db = AppDatabase.getAppDatabase(ctx);

        List<Itinerario> itinerarios = db.itinerarioDAO().listarTodosAtivosSimplificadoNovoSync();

        System.out.println("PROGRESSO: ITI "+itinerarios.size());

        int total = itinerarios.size();
        int cont = 0;

        for(Itinerario iti : itinerarios){
            db.temporariasDAO().invalidaTemporariaPorItinerario(iti.getId());

            db.temporariasDAO().atualizaTemporariaPorItinerario(iti.getId());
            cont++;

            System.out.println("ITINERARIO "+iti.getId());
        }

        // temp 2

        cont = 0;

        for(Itinerario iti : itinerarios){
            db.temporariasDAO().invalidaTemporaria2PorItinerario(iti.getId());

            db.temporariasDAO().atualizaTemporaria2PorItinerario(iti.getId());
            cont++;

            System.out.println("ITINERARIO 2 "+iti.getId());
        }

        System.out.println("ITINERARIO FINALIZOU!");

        return Result.success();
    }

}
