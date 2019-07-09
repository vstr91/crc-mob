package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;

public class ComparaHorariosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<HorarioItinerarioNome>> horariosProcessados;
    public LiveData<List<HorarioItinerarioNome>> horariosAtuais;
    public HorarioItinerarioNome horario;

    public LiveData<ItinerarioPartidaDestino> itinerario;
    public ObservableField<ItinerarioPartidaDestino> iti;

    public static MutableLiveData<Integer> retorno;

    public void setItinerario(String itinerario) {
        this.itinerario = appDatabase.itinerarioDAO().carregar(itinerario);
        horariosAtuais = appDatabase.horarioItinerarioDAO()
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

    public ComparaHorariosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        horario = new HorarioItinerarioNome();
        iti = new ObservableField<>(new ItinerarioPartidaDestino());

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void atualizaHorarios(List<HorarioItinerarioNome> horarios, Itinerario itinerario){

        new invalidaHorariosAsyncTask(appDatabase).execute(itinerario);

        for(HorarioItinerarioNome h : horarios){
            editarHorario(h);
        }

    }

    public void editarHorario(HorarioItinerarioNome horario){

        if(horario.getHorarioItinerario().valida(horario.getHorarioItinerario())){
            edit(horario.getHorarioItinerario());
        }

    }

    // adicionar

    public void edit(final HorarioItinerario horario) {

        if(horario.getDataCadastro() == null){
            horario.setDataCadastro(new DateTime());
        }

        horario.setUltimaAlteracao(new DateTime());
        horario.setEnviado(false);

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
            retorno.setValue(1);
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
