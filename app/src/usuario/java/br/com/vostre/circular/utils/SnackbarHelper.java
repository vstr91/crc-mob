package br.com.vostre.circular.utils;

import com.google.android.material.snackbar.Snackbar;
import android.view.View;

public class SnackbarHelper {

    public static void notifica(View rootView, CharSequence texto, int duracao){
        Snackbar.make(rootView, texto, duracao).show();
    }

}