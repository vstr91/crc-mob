package br.com.vostre.circular.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import org.joda.time.DateTime;

@Entity(tableName = "tpr2")
public class Tpr2 extends EntidadeBase {

    @NonNull
    private String idItinerario;

    @NonNull
    private Double distanciaMetros;

    @NonNull
    private String idBairroPartida;

    @NonNull
    private String idBairroDestino;

    @NonNull
    private Double distanciaTrechoMetros;

    @NonNull
    private Integer flagTrecho;

    public Tpr2(){
        this.setAtivo(true);
    }

    @NonNull
    public String getIdItinerario() {
        return idItinerario;
    }

    public void setIdItinerario(@NonNull String idItinerario) {
        this.idItinerario = idItinerario;
    }

    @NonNull
    public Double getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(@NonNull Double distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }

    @NonNull
    public String getIdBairroPartida() {
        return idBairroPartida;
    }

    public void setIdBairroPartida(@NonNull String idBairroPartida) {
        this.idBairroPartida = idBairroPartida;
    }

    @NonNull
    public String getIdBairroDestino() {
        return idBairroDestino;
    }

    public void setIdBairroDestino(@NonNull String idBairroDestino) {
        this.idBairroDestino = idBairroDestino;
    }

    @NonNull
    public Double getDistanciaTrechoMetros() {
        return distanciaTrechoMetros;
    }

    public void setDistanciaTrechoMetros(@NonNull Double distanciaTrechoMetros) {
        this.distanciaTrechoMetros = distanciaTrechoMetros;
    }

    @NonNull
    public Integer getFlagTrecho() {
        return flagTrecho;
    }

    public void setFlagTrecho(@NonNull Integer flagTrecho) {
        this.flagTrecho = flagTrecho;
    }
}
