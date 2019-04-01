package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.File;

import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;

public class InfoWindowSugestaoPoiViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    PontoInteresseSugestaoBairro parada;
    public Bitmap foto;

    public PontoInteresseSugestaoBairro getParada() {
        return parada;
    }

    public void setParada(PontoInteresseSugestaoBairro parada) {
        this.parada = parada;

        if(parada.getPontoInteresse().getImagem() != null){
            File foto = new File(getApplication().getFilesDir(), parada.getPontoInteresse().getImagem());

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

    public InfoWindowSugestaoPoiViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
    }

}
