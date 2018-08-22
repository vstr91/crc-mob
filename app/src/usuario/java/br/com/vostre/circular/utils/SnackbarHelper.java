package br.com.vostre.circular.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackbarHelper {

    public static void notifica(View rootView, CharSequence texto, int duracao){
        Snackbar.make(rootView, texto, duracao).show();
    }

}