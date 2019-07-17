package br.com.vostre.circular.utils;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.location.Location;

import java.util.List;

import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ParadaBairro;

public class LocationUtils {

    public static LiveData<List<ParadaBairro>> buscaParadasProximas(Context context, Location local, int raioEmMetros){

        //double latitude = local.getLatitude();
        //double longitude = local.getLongitude();

        // Centro - Barra do Pirai
        double latitude = -22.470612;
        double longitude = -43.8263613;

// 6378000 Size of the Earth (in meters)
        double longitudeD = (Math.asin(raioEmMetros / (6378000 * Math.cos(Math.PI*latitude/180))))*180/Math.PI;
        double latitudeD = (Math.asin((double)raioEmMetros / (double)6378000))*180/Math.PI;

        double latitudeMax = latitude+(latitudeD);
        double latitudeMin = latitude-(latitudeD);
        double longitudeMax = longitude+(longitudeD);
        double longitudeMin = longitude-(longitudeD);

        AppDatabase appDatabase = AppDatabase.getAppDatabase(context);

        return appDatabase.paradaDAO().listarTodosAtivosProximos(latitudeMin, latitudeMax, longitudeMin, longitudeMax);

        //new buscaAsyncTask(appDatabase, local, this).execute();
    }

}
