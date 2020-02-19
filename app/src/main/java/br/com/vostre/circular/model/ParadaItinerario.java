package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;

@Entity(indices = {@Index(value = {"parada", "itinerario"},
        unique = true)}, tableName = "parada_itinerario")
public class ParadaItinerario extends EntidadeBase {

    @NonNull
    private String parada;

    @NonNull
    private String itinerario;

    @NonNull
    private Integer ordem;

    @NonNull
    private Boolean destaque;

    private Double valorAnterior;

    private Double valorSeguinte;

    private DateTime tempoSeguinte;

    private Double distanciaSeguinte;

    private Double distanciaSeguinteMetros;

    public Double getDistanciaSeguinteMetros() {
        return distanciaSeguinteMetros;
    }

    public void setDistanciaSeguinteMetros(Double distanciaSeguinteMetros) {
        this.distanciaSeguinteMetros = distanciaSeguinteMetros;
    }

    public DateTime getTempoSeguinte() {
        return tempoSeguinte;
    }

    public void setTempoSeguinte(DateTime tempoSeguinte) {
        this.tempoSeguinte = tempoSeguinte;
    }

    public Double getDistanciaSeguinte() {
        return distanciaSeguinte;
    }

    public void setDistanciaSeguinte(Double distanciaSeguinte) {
        this.distanciaSeguinte = distanciaSeguinte;
    }

    @NonNull
    public String getParada() {
        return parada;
    }

    public void setParada(@NonNull String parada) {
        this.parada = parada;
    }

    @NonNull
    public String getItinerario() {
        return itinerario;
    }

    public void setItinerario(@NonNull String itinerario) {
        this.itinerario = itinerario;
    }

    @NonNull
    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(@NonNull Integer ordem) {
        this.ordem = ordem;
    }

    @NonNull
    public Boolean getDestaque() {
        return destaque;
    }

    public void setDestaque(@NonNull Boolean destaque) {
        this.destaque = destaque;
    }

    public Double getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(Double valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public Double getValorSeguinte() {
        return valorSeguinte;
    }

    public void setValorSeguinte(Double valorSeguinte) {
        this.valorSeguinte = valorSeguinte;
    }

    public boolean valida(ParadaItinerario paradaItinerario) {

        if(super.valida(paradaItinerario) && paradaItinerario.getParada() != null
                && paradaItinerario.getItinerario() != null
                && paradaItinerario.getOrdem() != null && paradaItinerario.getDestaque() != null){
            return true;
        } else{
            return false;
        }

    }
}
