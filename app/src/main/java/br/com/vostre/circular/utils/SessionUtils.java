package br.com.vostre.circular.utils;

import android.content.Context;

public class SessionUtils {

    public static boolean estaLogado(Context context){
        return !PreferenceUtils.carregarUsuarioLogado(context).isEmpty();
    }

}
