package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;

import java.io.File;

import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.dao.AppDatabase;

public class InfoWindowPOIViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    PontoInteresse pontoInteresse;
    public Bitmap foto;

    public PontoInteresse getPontoInteresse() {
        return pontoInteresse;
    }

    public void setPontoInteresse(PontoInteresse pontoInteresse) {
        this.pontoInteresse = pontoInteresse;

        if(pontoInteresse.getImagem() != null){
            File foto = new File(getApplication().getFilesDir(), pontoInteresse.getImagem());

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

    public InfoWindowPOIViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
    }

}
