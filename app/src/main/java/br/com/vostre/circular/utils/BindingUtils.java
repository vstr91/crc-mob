package br.com.vostre.circular.utils;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class BindingUtils {

    @BindingAdapter("app:visibility")
    public static void setVisibility(View view, Boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("app:horario")
    public static void setHorario(TextView textView, String s){
        textView.setText(s);
    }

    @BindingAdapter("app:horario")
    public static void setHorario(TextView textView, Long l){

        if(l != null){
            textView.setText(DateTimeFormat.forPattern("HH:mm")
                    .print(l));
        }

    }

    @BindingAdapter("app:tempo")
    public static void setTempo(TextView textView, DateTime dateTime){
        textView.setText(DateTimeFormat.forPattern("HH:mm")
                .print(dateTime));
    }

    @BindingAdapter("app:distancia")
    public static void setDistancia(TextView textView, Double d){

        if(d != null){

            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1);

            textView.setText(nf.format(d/1000)+" Km");
        }

    }

    @BindingAdapter("app:tarifa")
    public static void setTarifa(TextView textView, Double d){

        if(d != null){

            NumberFormat nf = NumberFormat.getCurrencyInstance();
            nf.setMaximumFractionDigits(2);

            textView.setText(nf.format(d));
        }

    }

    @BindingAdapter("app:textDinheiro")
    public static void setTextDinheiro(TextView view, Double val){

        if(val != null){
            view.setText(NumberFormat.getCurrencyInstance().format(val));
        } else{
            view.setText("-");
        }

    }

    @BindingAdapter("app:textDistancia")
    public static void setTextDistancia(TextView view, Double val){

        if(val != null){
            DecimalFormat format = new DecimalFormat();
            format.setMinimumFractionDigits(1);
            format.setMaximumFractionDigits(1);
            view.setText(format.format(val)+" Km");
        } else{
            view.setText("-");
        }

    }

    @BindingAdapter("app:textData")
    public static void setText(TextView view, DateTime val){

        if(val != null){
            view.setText(DateTimeFormat.forPattern("HH:mm").print(val));
        } else{
            view.setText("-");
        }

    }

}
