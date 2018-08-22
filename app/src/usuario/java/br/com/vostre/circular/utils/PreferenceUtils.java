package br.com.vostre.circular.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PreferenceUtils {

    public static void salvarPreferencia(Context ctx, String chave, String valor){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(chave, valor);
        editor.apply();
    }

    public static String carregarPreferencia(Context ctx, String chave){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(chave, "");
    }

    public static void salvarUsuarioLogado(Context ctx, String id){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usuario", id);
        editor.apply();
    }

    public static String carregarUsuarioLogado(Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
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
    }

}
