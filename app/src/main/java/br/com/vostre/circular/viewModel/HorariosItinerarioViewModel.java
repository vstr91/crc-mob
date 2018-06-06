package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.PaisDAO;
import br.com.vostre.circular.utils.StringUtils;

public class HorariosItinerarioViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<HorarioItinerario>> horarios;
    public HorarioItinerario horario;

    public LiveData<List<HorarioItinerario>> getHorarios() {
        return horarios;
    }

    public void setHorarios(LiveData<List<HorarioItinerario>> horarios) {
        this.horarios = horarios;
    }

    public HorarioItinerario getHorario() {
        return horario;
    }

    public void setHorario(HorarioItinerario horario) {
        this.horario = horario;
    }

    public HorariosItinerarioViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        horario = new HorarioItinerario();
        horarios = appDatabase.horarioItinerarioDAO().listarTodos();
    }

    public void salvarHorario(){

        if(horario.valida(horario)){
            add(horario);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarHorario(){

        if(horario.valida(horario)){
            edit(horario);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    // adicionar

    public void add(final HorarioItinerario horario) {

        horario.setDataCadastro(new DateTime());
        horario.setUltimaAlteracao(new DateTime());
        horario.setEnviado(false);

        new addAsyncTask(appDatabase).execute(horario);
    }

    private static class addAsyncTask extends AsyncTask<HorarioItinerario, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final HorarioItinerario... params) {
            db.horarioItinerarioDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    // editar

    public void edit(final HorarioItinerario horario) {

        horario.setUltimaAlteracao(new DateTime());
        horario.setEnviado(false);

        new editAsyncTask(appDatabase).execute(horario);
    }

    private static class editAsyncTask extends AsyncTask<HorarioItinerario, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final HorarioItinerario... params) {
            db.horarioItinerarioDAO().editar((params[0]));
            return null;
        }

    }

    // fim editar

}
