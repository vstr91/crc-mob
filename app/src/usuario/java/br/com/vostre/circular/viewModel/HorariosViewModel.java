package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.PaisDAO;
import br.com.vostre.circular.utils.StringUtils;

public class HorariosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<Horario>> horarios;
    public Horario horario;

    public LiveData<List<Horario>> getHorarios() {
        return horarios;
    }

    public void setHorarios(LiveData<List<Horario>> horarios) {
        this.horarios = horarios;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public HorariosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        horario = new Horario();
        horarios = appDatabase.horarioDAO().listarTodos();
    }

    public void popularHorarios(){

        int cont = 12 * 24;

        DateTime dateTime = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);

        for(int i = 0; i < cont; i++){

            Horario horario = new Horario();
            horario.setNome(dateTime);

            add(horario);

            System.out.println("HORA: "+ DateTimeFormat.forPattern("HH:mm-Z").print(dateTime));
            dateTime = dateTime.plusMinutes(5);
        }

    }

    // adicionar

    public void add(final Horario horario) {

        horario.setDataCadastro(new DateTime());
        horario.setUltimaAlteracao(new DateTime());
        horario.setEnviado(false);

        new addAsyncTask(appDatabase).execute(horario);
    }

    private static class addAsyncTask extends AsyncTask<Horario, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Horario... params) {
            db.horarioDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

}
