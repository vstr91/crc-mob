package br.com.vostre.circular.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.JsonObject;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.vostre.circular.model.UsuarioPreferencia;
import br.com.vostre.circular.model.dao.AppDatabase;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceUtils {

    public static void salvarPreferencia(Context ctx, String chave, String valor){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(chave, valor);
        editor.apply();
    }

    public static void salvarPreferencia(Context ctx, String chave, int valor){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(chave, valor);
        editor.apply();
    }

    public static void salvarPreferencia(Context ctx, String chave, boolean valor){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(chave, valor);
        editor.apply();
    }

    public static void gravaMostraToast(Context ctx, boolean valor){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("mostra_toast", valor);
        editor.apply();
    }

    public static boolean carregarMostraToast(Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        return sharedPreferences.getBoolean("mostra_toast", false);
    }

    public static String carregarPreferencia(Context ctx, String chave){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        return sharedPreferences.getString(chave, "");
    }

    public static int carregarPreferenciaInt(Context ctx, String chave){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        return sharedPreferences.getInt(chave, -1);
    }

    public static boolean carregarPreferenciaBoolean(Context ctx, String chave){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        return sharedPreferences.getBoolean(chave, false);
    }

    public static void salvarUsuarioLogado(Context ctx, String id){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usuario", id);
        editor.apply();
    }

    public static String carregarUsuarioLogado(Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), MODE_PRIVATE);
        return sharedPreferences.getString("usuario", "");
    }

    public static List<String> carregaParadasFavoritas(Context context){
        List<String> paradasFavoritas = null;

        String favParadas = (String) PreferenceUtils.carregarPreferencia(context, context.getPackageName()+".paradas_favoritas");
        String[] arrParadas;

        if(favParadas.isEmpty()){
            paradasFavoritas = new ArrayList<>();
        } else{
            arrParadas = favParadas.split(";");
            paradasFavoritas = new LinkedList<>(Arrays.asList(arrParadas));
        }

        return paradasFavoritas;

    }

    public static void gravaParadasFavoritas(List<String> lstParadas, Context context){
        String favParadas = Arrays.toString(lstParadas.toArray())
                .replace("[", "").replace("]", "")
                .replace(" ", "").replace(",", ";");

        PreferenceUtils.salvarPreferencia(context, context.getPackageName()+".paradas_favoritas", favParadas);

        if(SessionUtils.estaLogado(context)){
            salvaPreferenciasNoBanco(context);
        }


    }

    public static List<String> carregaItinerariosFavoritos(Context context){
        List<String> itinerariosFavoritos = null;

        String favItinerarios = (String) PreferenceUtils.carregarPreferencia(context, context.getPackageName()+".itinerarios_favoritos");
        String[] arrItinerarios;

        if(favItinerarios.isEmpty()){
            itinerariosFavoritos = new ArrayList<>();
        } else{
            arrItinerarios = favItinerarios.split(";");
            itinerariosFavoritos = new LinkedList<>(Arrays.asList(arrItinerarios));
        }

        return itinerariosFavoritos;

    }

    public static void gravaItinerariosFavoritos(List<String> lstItinerarios, Context context){
        String favItinerarios = Arrays.toString(lstItinerarios.toArray())
                .replace("[", "").replace("]", "")
                .replace(" ", "").replace(",", ";");

        PreferenceUtils.salvarPreferencia(context, context.getPackageName()+".itinerarios_favoritos", favItinerarios);

        if(SessionUtils.estaLogado(context)){
            salvaPreferenciasNoBanco(context);
        }

    }

    public static String carregaPreferenciasUsuario(Context context){
        Map<String, ?> map = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE).getAll();

        JsonObject jsonObject = new JsonObject();

        for(Map.Entry<String,?> entry : map.entrySet()){

            if(!entry.getKey().equalsIgnoreCase("init") && !entry.getKey().startsWith("param_") && !entry.getKey().equalsIgnoreCase("usuario")){

                jsonObject.addProperty(entry.getKey(), entry.getValue().toString());

            }

        }

        return jsonObject.toString();

    }

    private static void salvaPreferenciasNoBanco(final Context context){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String jsonPreferencias = carregaPreferenciasUsuario(context);
                String usuarioLogado = PreferenceUtils.carregarUsuarioLogado(context);
                AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
                boolean jaExiste = false;


                UsuarioPreferencia usuarioPreferencia = appDatabase.usuarioPreferenciaDAO().carregarPorUsuario(usuarioLogado);

                if(usuarioPreferencia == null){
                    usuarioPreferencia = new UsuarioPreferencia();
                    usuarioPreferencia.setDataCadastro(DateTime.now());
                } else{
                    jaExiste = true;
                }

                usuarioPreferencia.setPreferencia(jsonPreferencias);
                usuarioPreferencia.setUsuario(usuarioLogado);

                usuarioPreferencia.setUltimaAlteracao(DateTime.now());
                usuarioPreferencia.setEnviado(false);

                if(jaExiste){
                    appDatabase.usuarioPreferenciaDAO().editar(usuarioPreferencia);
                } else{
                    appDatabase.usuarioPreferenciaDAO().inserir(usuarioPreferencia);
                }



            }
        });

    }

    public static void mesclaItinerariosFavoritos(List<String> itinerarios, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        String itinerariosAtuais = sharedPreferences.getString(context.getPackageName()+".itinerarios_favoritos", "");

        List<String> l = new ArrayList<>(Arrays.asList(itinerariosAtuais.split(";")));

        if(l.get(0).isEmpty()){
            l = new ArrayList<>();
        }

        for(String i : itinerarios){
            if(!l.contains(i)){
                l.add(i);
            }
        }

        gravaItinerariosFavoritos(l, context);

    }

    public static void mesclaParadasFavoritas(List<String> paradas, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        String itinerariosAtuais = sharedPreferences.getString(context.getPackageName()+".paradas_favoritas", "");

        List<String> l = new ArrayList<>(Arrays.asList(itinerariosAtuais.split(";")));

        if(l.get(0).isEmpty()){
            l = new ArrayList<>();
        }

        for(String i : paradas){
            if(!l.contains(i)){
                l.add(i);
            }
        }

        gravaParadasFavoritas(l, context);

    }

    public static void atualizaItinerariosFavoritosNoBanco(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        String itinerariosAtuais = sharedPreferences.getString(context.getPackageName()+".itinerarios_favoritos", "");

        List<String> l = new ArrayList<>(Arrays.asList(itinerariosAtuais.split(";")));

        gravaItinerariosFavoritos(l, context);
    }

    public static void atualizaParadasFavoritasNoBanco(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        String paradasAtuais = sharedPreferences.getString(context.getPackageName()+".paradas_favoritas", "");

        List<String> l = new ArrayList<>(Arrays.asList(paradasAtuais.split(";")));

        gravaParadasFavoritas(l, context);
    }

}
