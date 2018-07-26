package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.util.List;

import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;

public class InfoWindowParadaViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    ParadaBairro parada;
    public Bitmap foto;
    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;

    public ParadaBairro getParada() {
        return parada;
    }

    public void setParada(ParadaBairro parada) {
        this.parada = parada;
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada.getParada().getId(),
                DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime()));

        if(parada.getParada().getImagem() != null){
            File foto = new File(getApplication().getFilesDir(), parada.getParada().getImagem());

            if(foto.exists() && foto.canRead()){
                this.foto = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.foto = null;
        }

    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public InfoWindowParadaViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario("", "");
    }

}
