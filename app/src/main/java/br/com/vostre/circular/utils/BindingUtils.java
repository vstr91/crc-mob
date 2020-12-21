package br.com.vostre.circular.utils;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import br.com.vostre.circleview.CircleView;
import br.com.vostre.circular.R;

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

    @BindingAdapter("app:textUltimaAlteracao")
    public static void setTextUltimaAlteracao(TextView view, DateTime val){

        if(val != null){
            view.setText("Última alteração: "+DateTimeFormat.forPattern("dd/MM/yyyy HH:mm").print(val));
        } else{
            view.setText("-");
        }

    }

    @BindingAdapter("app:textClima")
    public static void setTextClima(EditText view, Integer val){

        if(val != null){
            view.setText(String.valueOf(val));
        } else{
            view.setText("");
        }

    }

    @InverseBindingAdapter(attribute = "app:textClima", event = "android:textAttrChanged")
    public static int getTextClima(EditText view){


        if(!view.getText().toString().isEmpty()){
            return Integer.parseInt(view.getText().toString());
        } else{
            return 0;
        }

    }

    @BindingAdapter("app:imagem")
    public static void setimagem(ImageView view, String imagem){

        if(imagem != null){
            final File brasao = new File(view.getContext().getApplicationContext().getFilesDir(),  imagem);

            if(brasao.exists() && brasao.canRead()){
                final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
                view.setImageDrawable(drawable);
            }
        } else{

            view.setImageDrawable(view.getContext().getApplicationContext().getResources().getDrawable(R.drawable.imagem_nao_disponivel_quadrada));
        }

    }

    @BindingAdapter("app:imagem")
    public static void setimagem(CircleView view, String imagem){

        if(imagem != null){
            final File brasao = new File(view.getContext().getApplicationContext().getFilesDir(),  imagem);

            if(brasao.exists() && brasao.canRead()){
                final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
                view.setImagem(null);
                view.setImagem(drawable);
                view.refreshDrawableState();
            }
        }

    }

}
