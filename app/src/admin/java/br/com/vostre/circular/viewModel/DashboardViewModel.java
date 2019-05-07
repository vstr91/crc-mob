package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.AcessoDia;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;

public class DashboardViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    public LiveData<List<AcessoDia>> acessos;
    public LiveData<Integer> itinerarios;
    public LiveData<Integer> paradas;
    public LiveData<Integer> horarios;
    public LiveData<Integer> cidades;
    public LiveData<Integer> empresas;

    public LiveData<List<ItinerarioPartidaDestino>> itinerariosMunicipais;
    public LiveData<List<ItinerarioPartidaDestino>> itinerariosIntermunicipais;

    public DashboardViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        acessos = appDatabase.acessoDAO().listarAcessosUnicosPorDia(7);
        itinerarios = appDatabase.itinerarioDAO().contarTodosAtivos();
        paradas = appDatabase.paradaDAO().contarTodosAtivos();
        cidades = appDatabase.cidadeDAO().contarTodosAtivos();
        horarios = appDatabase.horarioItinerarioDAO().contarTodosAtivos();
        empresas = appDatabase.empresaDAO().contarTodosAtivos();

        itinerariosMunicipais = appDatabase.itinerarioDAO().listarTodosMunicipaisAtivosComTotalHorarios();
        itinerariosIntermunicipais = appDatabase.itinerarioDAO().listarTodosIntermunicipaisAtivosComTotalHorarios();
    }

}
