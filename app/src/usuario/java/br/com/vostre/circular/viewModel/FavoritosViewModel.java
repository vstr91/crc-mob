package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.PreferenceUtils;

public class FavoritosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public MutableLiveData<List<ParadaBairro>> paradas;
    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;


    public FavoritosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());

        paradas = new MutableLiveData<>();

        carregarParadasFavoritas();
        //mensagensRecebidas = appDatabase.mensagemDAO().listarTodosRecebidos();
    }

    private void carregarParadasFavoritas(){
        final List<ParadaBairro> listParadas = new ArrayList<>();

        final List<String> paradasFavoritas = PreferenceUtils.carregaParadasFavoritas(getApplication());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                for(String id : paradasFavoritas){
                    ParadaBairro paradaBairro = appDatabase.paradaDAO().carregarComBairroSync(id);
                    listParadas.add(paradaBairro);
                }

                Collections.sort(listParadas, new Comparator<ParadaBairro>() {
                    @Override
                    public int compare(ParadaBairro paradaBairro, ParadaBairro t1) {
                        return t1.getParada().getNome().compareTo(paradaBairro.getParada().getNome());
                    }
                });

                paradas.postValue(listParadas);

            }
        });

    }

}
