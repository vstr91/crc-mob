package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.LocationUtils;

public class InfoWindowPOIViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    PontoInteresse pontoInteresse;
    public Bitmap foto;

    public LiveData<List<ParadaBairro>> paradas;
    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;

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

    public void buscarParadasProximas(Context ctx, Location location){

        paradas = LocationUtils.buscaParadasProximas(ctx, location, 100);

    }

    public void listarTodosAtivosProximosPoi(List<String> paradas){

        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosProximosPoi(paradas);

    }

}
