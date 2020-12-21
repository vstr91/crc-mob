package br.com.vostre.circular.utils;

import android.content.Context;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class SessionUtils {

    public static boolean estaLogado(Context context){
        return !PreferenceUtils.carregarUsuarioLogado(context).isEmpty();
    }

    public static void logUser(String usuarioLogado) {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

        crashlytics.setUserId(usuarioLogado);
    }


}
