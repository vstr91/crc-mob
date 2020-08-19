package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.databinding.ObservableField;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;

public class HorariosItinerarioViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<HorarioItinerarioNome>> horarios;
    public LiveData<List<HorarioItinerarioNome>> horariosCadastrados;
    public HorarioItinerarioNome horario;

    public LiveData<ItinerarioPartidaDestino> itinerario;
    public ObservableField<ItinerarioPartidaDestino> iti;

    public static MutableLiveData<Integer> retorno;

    public LiveData<List<HorarioItinerarioNome>> getHorariosCadastrados() {
        return horariosCadastrados;
    }

    public void setHorariosCadastrados(LiveData<List<HorarioItinerarioNome>> horariosCadastrados) {
        this.horariosCadastrados = horariosCadastrados;
    }

    public LiveData<List<HorarioItinerarioNome>> getHorarios() {
        return horarios;
    }

    public void setHorarios(LiveData<List<HorarioItinerarioNome>> horarios) {
        this.horarios = horarios;
    }

    public void setItinerario(String itinerario) {
        this.itinerario = appDatabase.itinerarioDAO().carregar(itinerario);
        horarios = appDatabase.horarioItinerarioDAO()
                .listarTodosAtivosPorItinerario(itinerario);
        horariosCadastrados = appDatabase.horarioItinerarioDAO()
                .listarApenasAtivosPorItinerario(itinerario);
    }

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

    public HorariosItinerarioViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        horario = new HorarioItinerarioNome();
        horarios = appDatabase.horarioItinerarioDAO().listarTodosAtivosPorItinerario("");
        horariosCadastrados = appDatabase.horarioItinerarioDAO().listarTodosAtivosPorItinerario("");
        iti = new ObservableField<>(new ItinerarioPartidaDestino());

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void editarHorario(){

        if(horario.getHorarioItinerario().valida(horario.getHorarioItinerario())){
            edit(horario.getHorarioItinerario());
        } else{
            retorno.setValue(0);
        }

    }

    public void invalidarHorarios(String itinerario){

        Itinerario iti = new Itinerario();
        iti.setId(itinerario);

        new invalidaHorariosAsyncTask(appDatabase).execute(iti);
    }

    // adicionar

    public void edit(final HorarioItinerario horario) {

        if(horario.getDataCadastro() == null){
            horario.setDataCadastro(new DateTime());
        }

        horario.setUltimaAlteracao(new DateTime());
        horario.setEnviado(false);

        //new invalidaHorariosAsyncTask(appDatabase).execute(itinerario.getValue().getItinerario());

        new editAsyncTask(appDatabase).execute(horario);
    }

    private static class invalidaHorariosAsyncTask extends AsyncTask<Itinerario, Void, Void> {

        private AppDatabase db;

        invalidaHorariosAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Itinerario... params) {

            db.horarioItinerarioDAO().invalidaTodosPorItinerario(params[0].getId());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(2);
        }

    }

    private static class editAsyncTask extends AsyncTask<HorarioItinerario, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final HorarioItinerario... params) {

            HorarioItinerario horarioItinerario = db.horarioItinerarioDAO()
                    .checaDuplicidade(params[0].getHorario(), params[0].getItinerario());

            if(horarioItinerario != null){
                HorarioItinerario hi = params[0];

                horarioItinerario.setAtivo(true);
                horarioItinerario.setDomingo(hi.getDomingo());
                horarioItinerario.setSegunda(hi.getSegunda());
                horarioItinerario.setTerca(hi.getTerca());
                horarioItinerario.setQuarta(hi.getQuarta());
                horarioItinerario.setQuinta(hi.getQuinta());
                horarioItinerario.setSexta(hi.getSexta());
                horarioItinerario.setSabado(hi.getSabado());
                horarioItinerario.setObservacao(hi.getObservacao());
                horarioItinerario.setUltimaAlteracao(new DateTime());
                horarioItinerario.setProgramadoPara(hi.getProgramadoPara());
                horarioItinerario.setEnviado(false);

                db.horarioItinerarioDAO().editar(horarioItinerario);
            } else{
                db.horarioItinerarioDAO().inserir(params[0]);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim editar

}
