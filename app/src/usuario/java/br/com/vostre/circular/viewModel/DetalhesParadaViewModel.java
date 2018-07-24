package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.utils.StringUtils;

public class DetalhesParadaViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<ParadaBairro> parada;
    public ParadaBairro umaParada;

    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;

    public Bitmap foto;

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public void setParada(String parada) {
        //foto = BitmapFactory.decodeFile(parada.getParada().getImagem());
        this.parada = appDatabase.paradaDAO().carregarComBairro(parada);
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada, DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime()));
    }

    public LiveData<ParadaBairro> getParada() {
        return parada;
    }

    public DetalhesParadaViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        parada = appDatabase.paradaDAO().carregarComBairro("");
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario("", "");
    }

    @BindingAdapter("srcCompat")
    public static void setImagemFoto(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        } else{
            imageView.setBackgroundResource(R.mipmap.ic_onibus_azul);
        }

    }

    public void carregarItinerarios(String parada){
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada, DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime()));
    }

}
