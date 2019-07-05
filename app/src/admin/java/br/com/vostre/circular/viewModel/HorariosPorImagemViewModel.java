package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;

public class HorariosPorImagemViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<Horario>> horarios;
    public HorarioItinerarioNome horario;

    public LiveData<ItinerarioPartidaDestino> itinerario;
    public ObservableField<ItinerarioPartidaDestino> iti;

    public static MutableLiveData<Integer> retorno;

    public HorarioItinerarioNome getHorario() {
        return horario;
    }

    public void setHorario(HorarioItinerarioNome horario) {
        this.horario = horario;

        if(horario.getHorarioItinerario() == null){
            this.horario.setHorarioItinerario(new HorarioItinerario());
        }

    }

    public ObservableField<ItinerarioPartidaDestino> getIti() {
        return iti;
    }

    public void setIti(ObservableField<ItinerarioPartidaDestino> iti) {
        this.iti = iti;
    }

    public HorariosPorImagemViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        horario = new HorarioItinerarioNome();
        iti = new ObservableField<>(new ItinerarioPartidaDestino());


        horarios = appDatabase.horarioDAO().listarTodosAtivos();

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public List<HorarioItinerarioNome> vincularHorarios(List<HorarioItinerarioNome> horarios){

        List<HorarioItinerarioNome> hors = horarios;

        for(HorarioItinerarioNome h : horarios){
            Horario ho = carregarPorNome(h);
            h.getHorarioItinerario().setHorario(ho.getId());
            h.setNomeHorario(ho.getNome().getMillis());
        }

        return hors;

    }

    public Horario carregarPorNome(HorarioItinerarioNome h){
        String hora = h.getHorarioItinerario().getHorario()+":00";
        return appDatabase.horarioDAO().encontrarPorNome(hora);
    }

}
