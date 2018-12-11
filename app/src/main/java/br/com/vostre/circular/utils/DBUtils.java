package br.com.vostre.circular.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import br.com.vostre.circular.model.dao.AppDatabase;

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

}
