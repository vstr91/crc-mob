package br.com.vostre.circular.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

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

    public static String getDiaSeguinte(String dia){
        DateTime dateTime = new DateTime();
        String diaSeguinte = "";

        switch(dia){
            case "domingo":
                diaSeguinte = "segunda";
                break;
            case "segunda":
                diaSeguinte = "terca";
                break;
            case "terca":
                diaSeguinte = "quarta";
                break;
            case "quarta":
                diaSeguinte = "quinta";
                break;
            case "quinta":
                diaSeguinte = "sexta";
                break;
            case "sexta":
                diaSeguinte = "sabado";
                break;
            case "sabado":
                diaSeguinte = "domingo";
                break;
        }

        return diaSeguinte;
    }

    public static String getDiaAnterior(){
        DateTime dateTime = new DateTime();
        String diaSeguinte = "";

        switch(dateTime.get(DateTimeFieldType.dayOfWeek())){
            case 7:
                diaSeguinte = "sabado";
                break;
            case 1:
                diaSeguinte = "domingo";
                break;
            case 2:
                diaSeguinte = "segunda";
                break;
            case 3:
                diaSeguinte = "terca";
                break;
            case 4:
                diaSeguinte = "quarta";
                break;
            case 5:
                diaSeguinte = "quinta";
                break;
            case 6:
                diaSeguinte = "sexta";
                break;
        }

        return diaSeguinte;
    }

    public static String getDiaAnteriorSelecionado(Calendar data){
        DateTime dateTime = new DateTime(data);
        String diaSeguinte = "";

        switch(dateTime.get(DateTimeFieldType.dayOfWeek())){
            case 7:
                diaSeguinte = "sabado";
                break;
            case 1:
                diaSeguinte = "domingo";
                break;
            case 2:
                diaSeguinte = "segunda";
                break;
            case 3:
                diaSeguinte = "terca";
                break;
            case 4:
                diaSeguinte = "quarta";
                break;
            case 5:
                diaSeguinte = "quinta";
                break;
            case 6:
                diaSeguinte = "sexta";
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

    public static String getDiaFormatado(String d){
        DateTime dateTime = new DateTime();
        String dia = "";
        int a = dateTime.get(DateTimeFieldType.dayOfWeek());

        switch(d){
            case "domingo":
                dia = "Domingo";
                break;
            case "segunda":
                dia = "Segunda-Feira";
                break;
            case "terca":
                dia = "Terça-Feira";
                break;
            case "quarta":
                dia = "Quarta-Feira";
                break;
            case "quinta":
                dia = "Quinta-Feira";
                break;
            case "sexta":
                dia = "Sexta-Feira";
                break;
            case "sabado":
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

    public static String getHoraFormatada(String hora){
        return DateTimeFormat.forPattern("HH:mm").print(DateTimeFormat.forPattern("HH:mm:ss").parseLocalTime(hora));
    }

    public static String segundosParaHoraFormatado(Integer segundos){
        int hours = segundos / 3600;
        int minutes = (segundos % 3600) / 60;

        String h = hours < 10 ? "0"+hours : String.valueOf(hours);
        String m = minutes < 10 ? "0"+minutes : String.valueOf(minutes);

        Duration duration = new Duration(segundos * 1000);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSuffix(":").printZeroAlways()
                .appendMinutes().printZeroAlways()
                .appendSuffix(":")
                .appendSeconds().printZeroAlways()
                .toFormatter();

        Period period = duration.toPeriod();
        Period dayTimePeriod = period.normalizedStandard(PeriodType.dayTime());
        String formattedString = formatter.print(dayTimePeriod);

        if(period.getHours() == 0){
            return "00:"+formattedString;
        } else{
            return formattedString;
        }


    }

}
