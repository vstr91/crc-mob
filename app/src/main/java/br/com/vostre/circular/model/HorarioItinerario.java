package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"horario", "itinerario"},
        unique = true)}, tableName = "horario_itinerario")
public class HorarioItinerario extends EntidadeBase {

    @NonNull
    private String horario;

    @NonNull
    private String itinerario;

    @NonNull
    private Boolean domingo;

    @NonNull
    private Boolean segunda;

    @NonNull
    private Boolean terca;

    @NonNull
    private Boolean quarta;

    @NonNull
    private Boolean quinta;

    @NonNull
    private Boolean sexta;

    @NonNull
    private Boolean sabado;

    private String observacao;

    @NonNull
    public String getHorario() {
        return horario;
    }

    public void setHorario(@NonNull String horario) {
        this.horario = horario;
    }

    @NonNull
    public String getItinerario() {
        return itinerario;
    }

    public void setItinerario(@NonNull String itinerario) {
        this.itinerario = itinerario;
    }

    @NonNull
    public Boolean getDomingo() {
        return domingo;
    }

    public void setDomingo(@NonNull Boolean domingo) {
        this.domingo = domingo;
    }

    @NonNull
    public Boolean getSegunda() {
        return segunda;
    }

    public void setSegunda(@NonNull Boolean segunda) {
        this.segunda = segunda;
    }

    @NonNull
    public Boolean getTerca() {
        return terca;
    }

    public void setTerca(@NonNull Boolean terca) {
        this.terca = terca;
    }

    @NonNull
    public Boolean getQuarta() {
        return quarta;
    }

    public void setQuarta(@NonNull Boolean quarta) {
        this.quarta = quarta;
    }

    @NonNull
    public Boolean getQuinta() {
        return quinta;
    }

    public void setQuinta(@NonNull Boolean quinta) {
        this.quinta = quinta;
    }

    @NonNull
    public Boolean getSexta() {
        return sexta;
    }

    public void setSexta(@NonNull Boolean sexta) {
        this.sexta = sexta;
    }

    @NonNull
    public Boolean getSabado() {
        return sabado;
    }

    public void setSabado(@NonNull Boolean sabado) {
        this.sabado = sabado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean valida(HorarioItinerario horarioItinerario) {

        if(super.valida(horarioItinerario) && horarioItinerario.getHorario() != null
                && horarioItinerario.getItinerario() != null
                && horarioItinerario.getDomingo() != null
                && horarioItinerario.getSegunda() != null
                && horarioItinerario.getTerca() != null
                && horarioItinerario.getQuarta() != null
                && horarioItinerario.getQuinta() != null
                && horarioItinerario.getSexta() != null
                && horarioItinerario.getSabado() != null){
            return true;
        } else{
            return false;
        }

    }
}
