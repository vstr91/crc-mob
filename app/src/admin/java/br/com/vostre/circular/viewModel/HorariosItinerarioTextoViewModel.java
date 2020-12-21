package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;

public class HorariosItinerarioTextoViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<HorarioItinerarioNome>> horarios;
    public HorarioItinerarioNome horario;

    public LiveData<ItinerarioPartidaDestino> itinerario;
    public ObservableField<ItinerarioPartidaDestino> iti;

    public static MutableLiveData<Integer> retorno;

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

    public HorariosItinerarioTextoViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        horario = new HorarioItinerarioNome();
        horarios = appDatabase.horarioItinerarioDAO().listarTodosAtivosPorItinerario("");
        iti = new ObservableField<>(new ItinerarioPartidaDestino());

        retorno = new MutableLiveData<>();
        retorno.setValue(-2);
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

    public void processaHorarios(String[] segSex, String[] sab, String[] dom, String itinerario) {
        new processaAsyncTask(appDatabase, segSex, sab, dom, itinerario).execute();
    }

    private static class processaAsyncTask extends AsyncTask<HorarioItinerario, Void, Void> {

        private AppDatabase db;

        String[] segSex, sab, dom;
        String itinerario;

        processaAsyncTask(AppDatabase appDatabase, final String[] segSex, final String[] sab, final String dom[], String itinerario) {
            db = appDatabase;

            this.segSex = segSex;
            this.sab = sab;
            this.dom = dom;
            this.itinerario = itinerario;
        }

        @Override
        protected Void doInBackground(HorarioItinerario... params) {

            List<HorarioItinerarioNome> horarios = new ArrayList<>();

            //processando dias uteis

            if(segSex != null && segSex.length > 0){

                for(String h : segSex){

                    HorarioItinerarioNome hi = new HorarioItinerarioNome();
                    hi.getHorarioItinerario().setHorario(h);
                    hi.getHorarioItinerario().setItinerario(itinerario);
                    hi.getHorarioItinerario().setSegunda(true);
                    hi.getHorarioItinerario().setTerca(true);
                    hi.getHorarioItinerario().setQuarta(true);
                    hi.getHorarioItinerario().setQuinta(true);
                    hi.getHorarioItinerario().setSexta(true);

                    horarios.add(hi);
                }

            }

            if(sab != null && sab.length > 0){

                for(String h : sab){
                    HorarioItinerarioNome hi = new HorarioItinerarioNome();
                    hi.getHorarioItinerario().setHorario(h);
                    hi.getHorarioItinerario().setItinerario(itinerario);

                    int index = horarios.indexOf(hi);

                    if(index >= 0){
                        hi = horarios.get(index);
                        hi.getHorarioItinerario().setSabado(true);
                    } else{
                        hi.getHorarioItinerario().setSabado(true);
                        horarios.add(hi);
                    }

                }

            }

            if(dom != null && dom.length > 0){

                for(String h : dom){
                    HorarioItinerarioNome hi = new HorarioItinerarioNome();
                    hi.getHorarioItinerario().setHorario(h);
                    hi.getHorarioItinerario().setItinerario(itinerario);

                    int index = horarios.indexOf(hi);

                    if(index >= 0){
                        hi = horarios.get(index);
                        hi.getHorarioItinerario().setDomingo(true);
                    } else{
                        hi.getHorarioItinerario().setDomingo(true);
                        horarios.add(hi);
                    }

                }

            }

            if(horarios.size() > 0){
                db.horarioItinerarioDAO().invalidaTodosPorItinerario(itinerario);
            }

            for(HorarioItinerarioNome h : horarios){

                System.out.println("HORARIO>> "+h.getHorarioItinerario().getHorario()+":00");

                Horario hor = db.horarioDAO().encontrarPorNome(h.getHorarioItinerario().getHorario()+":00");

                if(hor != null){
                    HorarioItinerario horarioItinerario = db.horarioItinerarioDAO()
                            .checaDuplicidade(hor.getId(), itinerario);

                    if(horarioItinerario != null){
                        HorarioItinerario hi = h.getHorarioItinerario();

                        horarioItinerario.setAtivo(true);
                        horarioItinerario.setDomingo(hi.getDomingo());
                        horarioItinerario.setSegunda(hi.getSegunda());
                        horarioItinerario.setTerca(hi.getTerca());
                        horarioItinerario.setQuarta(hi.getQuarta());
                        horarioItinerario.setQuinta(hi.getQuinta());
                        horarioItinerario.setSexta(hi.getSexta());
                        horarioItinerario.setSabado(hi.getSabado());
                        horarioItinerario.setUltimaAlteracao(new DateTime());
                        horarioItinerario.setEnviado(false);

                        db.horarioItinerarioDAO().editar(horarioItinerario);
                    } else{
                        h.getHorarioItinerario().setHorario(hor.getId());
                        h.getHorarioItinerario().setItinerario(itinerario);
                        h.getHorarioItinerario().setAtivo(true);
                        h.getHorarioItinerario().setDataCadastro(new DateTime());
                        h.getHorarioItinerario().setUltimaAlteracao(new DateTime());
                        h.getHorarioItinerario().setEnviado(false);

                        db.horarioItinerarioDAO().inserir(h.getHorarioItinerario());
                    }

                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

}
