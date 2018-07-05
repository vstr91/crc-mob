package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.File;

import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ParadaBairro;

public class InfoWindowViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    ParadaBairro parada;
    public Bitmap foto;

    public ParadaBairro getParada() {
        return parada;
    }

    public void setParada(ParadaBairro parada) {
        this.parada = parada;

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

    public InfoWindowViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
    }

}
