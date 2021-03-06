package br.com.vostre.circular.model.pojo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;

import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Parada;

public class HorarioItinerarioNome {

    @Embedded
    HorarioItinerario horarioItinerario;

    @ColumnInfo(name = "idHorario")
    String idHorario;

    @ColumnInfo(name = "nomeHorario")
    Long nomeHorario;

    public HorarioItinerarioNome(){
        this.setHorarioItinerario(new HorarioItinerario());
    }

    public HorarioItinerario getHorarioItinerario() {
        return horarioItinerario;
    }

    public void setHorarioItinerario(HorarioItinerario horarioItinerario) {
        this.horarioItinerario = horarioItinerario;
    }

    public String getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(String idHorario) {
        this.idHorario = idHorario;
    }

    public Long getNomeHorario() {
        return nomeHorario;
    }

    public void setNomeHorario(Long nomeHorario) {
        this.nomeHorario = nomeHorario;
    }

    public boolean isAtivo(){

        if(this.getHorarioItinerario() != null){
            return (this.getHorarioItinerario().getDomingo() || this.getHorarioItinerario().getSegunda()
                    || this.getHorarioItinerario().getTerca() || this.getHorarioItinerario().getQuarta()
                    || this.getHorarioItinerario().getQuinta() || this.getHorarioItinerario().getSexta()
                    || this.getHorarioItinerario().getSabado());
        } else{
            return false;
        }


    }

    public void reseta(){
        this.getHorarioItinerario().setDomingo(false);
        this.getHorarioItinerario().setSegunda(false);
        this.getHorarioItinerario().setTerca(false);
        this.getHorarioItinerario().setQuarta(false);
        this.getHorarioItinerario().setQuinta(false);
        this.getHorarioItinerario().setSexta(false);
        this.getHorarioItinerario().setSabado(false);
        this.getHorarioItinerario().setObservacao(null);
        this.getHorarioItinerario().setProgramadoPara(null);
        this.getHorarioItinerario().setAtivo(false);
    }

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof HorarioItinerarioNome)){
            return false;
        }

        HorarioItinerarioNome horario = (HorarioItinerarioNome) o;
        return horario.getHorarioItinerario().getHorario().equals(this.getHorarioItinerario().getHorario())
                && horario.getHorarioItinerario().getItinerario().equals(this.getHorarioItinerario().getItinerario());
    }

}
