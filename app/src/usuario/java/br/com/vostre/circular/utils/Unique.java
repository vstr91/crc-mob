package br.com.vostre.circular.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Unique {

    private static Map<Integer, String> letras = new HashMap<>();

    public static String geraIdentificadorUnico(){
        preparaLetras();
        Calendar cal = Calendar.getInstance(new Locale("pt", "BR"));
        DateFormat df = new SimpleDateFormat("ddMMyyHHmmssSSS");
        DateFormat dfDia = new SimpleDateFormat("E");

        String dia = dfDia.format(cal.getTime());
        String diaReduzido = geraSiglaDiaDaSemana(dia);
        String letra1 = geraLetraAleatoria();
        String letra2 = geraLetraAleatoria();

        return diaReduzido+df.format(cal.getTime())+letra1+letra2;
    }

    private static String geraSiglaDiaDaSemana(String dia){
        return dia.substring(0, 1).concat(dia.substring(2,3)).toUpperCase();
    }

    private static void preparaLetras(){
        letras.put(1, "A");
        letras.put(2, "B");
        letras.put(3, "C");
        letras.put(4, "D");
        letras.put(5, "E");
        letras.put(6, "F");
        letras.put(7, "G");
        letras.put(8, "H");
        letras.put(9, "I");
        letras.put(10, "J");
        letras.put(11, "K");
        letras.put(12, "L");
        letras.put(13, "M");
        letras.put(14, "N");
        letras.put(15, "O");
        letras.put(16, "P");
        letras.put(17, "Q");
        letras.put(18, "R");
        letras.put(19, "S");
        letras.put(20, "T");
        letras.put(21, "U");
        letras.put(22, "V");
        letras.put(23, "W");
        letras.put(24, "X");
        letras.put(25, "Y");
        letras.put(26, "Z");
    }

    private static String geraLetraAleatoria(){
        int numero = geraNumeroAleatorio(1, 26);

        return letras.get(numero);

    }

    private static int geraNumeroAleatorio(int min, int max){
        Random r = new Random();
        return r.nextInt((max - min) + 1) + 1;
    }

    public static String getIdentificadorUnico(Context context){
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        return sp.getString(context.getPackageName()+".identificadorUnico", "");
    }

}
