package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.dao.AppDatabase;

public class DashboardViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    public LiveData<Integer> itinerarios;
    public LiveData<Integer> paradas;
    public LiveData<Integer> horarios;
    public LiveData<Integer> cidades;

    public DashboardViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        itinerarios = appDatabase.itinerarioDAO().contarTodosAtivos();
        paradas = appDatabase.paradaDAO().contarTodosAtivos();
        cidades = appDatabase.cidadeDAO().contarTodosAtivos();
        horarios = appDatabase.horarioItinerarioDAO().contarTodosAtivos();
    }

}
