package br.com.vostre.circular.utils;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

public class SessionUtils {

    public static boolean estaLogado(Context context){
        return !PreferenceUtils.carregarUsuarioLogado(context).isEmpty();
    }

    public static void logUser(String usuarioLogado) {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(usuarioLogado);
    }


}
