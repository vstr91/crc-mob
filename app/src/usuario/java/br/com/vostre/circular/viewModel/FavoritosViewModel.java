package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    public MutableLiveData<List<ItinerarioPartidaDestino>> itinerarios;


    public FavoritosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());

        paradas = new MutableLiveData<>();
        itinerarios = new MutableLiveData<>();

        carregarParadasFavoritas();
        carregarItinerariosFavoritos();
    }

    private void carregarParadasFavoritas(){
        final List<ParadaBairro> listParadas = new ArrayList<>();

        final List<String> paradasFavoritas = PreferenceUtils.carregaParadasFavoritas(getApplication());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                for(String id : paradasFavoritas){
                    ParadaBairro paradaBairro = appDatabase.paradaDAO().carregarComBairroSync(id);

                    if(paradaBairro != null && paradaBairro.getParada() != null &&
                            !paradaBairro.getNomeBairro().isEmpty() &&
                            !paradaBairro.getNomeCidade().isEmpty()){
                        listParadas.add(paradaBairro);
                    }


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

    private void carregarItinerariosFavoritos(){
        final List<ItinerarioPartidaDestino> listItinerarios = new ArrayList<>();

        final List<String> itinerariosFavoritos = PreferenceUtils.carregaItinerariosFavoritos(getApplication());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                for(String id : itinerariosFavoritos){

                    String[] dados = id.split("\\|");

                    ItinerarioPartidaDestino itinerario = appDatabase.itinerarioDAO()
                            .carregarPorPartidaEDestinoSync(dados[0], dados[1]);

                    if(itinerario != null && itinerario.getItinerario() != null &&
                            !itinerario.getNomeBairroPartida().isEmpty() &&
                            !itinerario.getNomeBairroDestino().isEmpty()){
                        listItinerarios.add(itinerario);
                    }

                }

                itinerarios.postValue(listItinerarios);

            }
        });

    }

}
