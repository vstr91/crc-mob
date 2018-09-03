package br.com.vostre.circular.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;

public class DataHoraUtils {

    public static String getDiaAtual(){
        DateTime dateTime = new DateTime();
        String dia = "";

        switch(dateTime.get(DateTimeFieldType.dayOfWeek())){
            case 7:
                dia = "domingo";
                break;
            case 1:
                dia = "segunda";
                break;
            case 2:
                dia = "terca";
                break;
            case 3:
                dia = "quarta";
                break;
            case 4:
                dia = "quinta";
                break;
            case 5:
                dia = "sexta";
                break;
            case 6:
                dia = "sabado";
                break;
        }

        return dia;
    }

    public static String getDiaSelecionado(Calendar data){
        DateTime dateTime = new DateTime(data);
        String dia = "";

        switch(dateTime.get(DateTimeFieldType.dayOfWeek())){
            case 7:
                dia = "domingo";
                break;
            case 1:
                dia = "segunda";
                break;
            case 2:
                dia = "terca";
                break;
            case 3:
                dia = "quarta";
                break;
            case 4:
                dia = "quinta";
                break;
            case 5:
                dia = "sexta";
                break;
            case 6:
                dia = "sabado";
                break;
        }

        return dia;
    }

    public static String getDiaSeguinte(){
        DateTime dateTime = new DateTime();
        String diaSeguinte = "";

        switch(dateTime.get(DateTimeFieldType.dayOfWeek())){
            case 7:
                diaSeguinte = "segunda";
                break;
            case 1:
                diaSeguinte = "terca";
                break;
            case 2:
                diaSeguinte = "quarta";
                break;
            case 3:
                diaSeguinte = "quinta";
                break;
            case 4:
                diaSeguinte = "sexta";
                break;
            case 5:
                diaSeguinte = "sabado";
                break;
            case 6:
                diaSeguinte = "domingo";
                break;
        }

        return diaSeguinte;
    }

    public static String getDiaSeguinteSelecionado(Calendar data){
        DateTime dateTime = new DateTime(data);
        String diaSeguinte = "";

        switch(dateTime.get(DateTimeFieldType.dayOfWeek())){
            case 7:
                diaSeguinte = "segunda";
                break;
            case 1:
                diaSeguinte = "terca";
                break;
            case 2:
                diaSeguinte = "quarta";
                break;
            case 3:
                diaSeguinte = "quinta";
                break;
            case 4:
                diaSeguinte = "sexta";
                break;
            case 5:
                diaSeguinte = "sabado";
                break;
            case 6:
                diaSeguinte = "domingo";
                break;
        }

        return diaSeguinte;
    }

    public static String getDiaAtualFormatado(){
        DateTime dateTime = new DateTime();
        String dia = "";
        int a = dateTime.get(DateTimeFieldType.dayOfWeek());

        switch(dateTime.get(DateTimeFieldType.dayOfWeek())){
            case 7:
                dia = "Domingo";
                break;
            case 1:
                dia = "Segunda-Feira";
                break;
            case 2:
                dia = "Terça-Feira";
                break;
            case 3:
                dia = "Quarta-Feira";
                break;
            case 4:
                dia = "Quinta-Feira";
                break;
            case 5:
                dia = "Sexta-Feira";
                break;
            case 6:
                dia = "Sábado";
                break;
        }

        return dia;
    }

    public static String getDiaSelecionadoFormatado(Calendar data){
        DateTime dateTime = new DateTime(data);
        String dia = "";

        switch(dateTime.get(DateTimeFieldType.dayOfWeek())){
            case 7:
                dia = "Domingo";
                break;
            case 1:
                dia = "Segunda-Feira";
                break;
            case 2:
                dia = "Terça-Feira";
                break;
            case 3:
                dia = "Quarta-Feira";
                break;
            case 4:
                dia = "Quinta-Feira";
                break;
            case 5:
                dia = "Sexta-Feira";
                break;
            case 6:
                dia = "Sábado";
                break;
        }

        return dia;
    }

    public static String getHoraAtual(){
        return DateTimeFormat.forPattern("HH:mm").print(DateTime.now());
    }

}
