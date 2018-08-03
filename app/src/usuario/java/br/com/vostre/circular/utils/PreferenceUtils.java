package br.com.vostre.circular.utils;

import android.content.Context;
import android.content.SharedPreferences;

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

}
