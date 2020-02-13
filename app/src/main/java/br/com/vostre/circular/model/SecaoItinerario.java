package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(tableName = "secao_itinerario")
public class SecaoItinerario extends EntidadeBase {

    @NonNull
    private String itinerario;

    @NonNull
    private String nome;

    private String paradaInicial;

    private String paradaFinal;

    @NonNull
    private Double tarifa;

    @Ignore
    private Double novaTarifa;

    public Double getNovaTarifa() {
        return novaTarifa;
    }

    public void setNovaTarifa(Double novaTarifa) {
        this.novaTarifa = novaTarifa;
    }

    @NonNull
    public String getItinerario() {
        return itinerario;
    }

    public void setItinerario(@NonNull String itinerario) {
        this.itinerario = itinerario;
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    public void setNome(@NonNull String nome) {
        this.nome = nome;
    }

    public String getParadaInicial() {
        return paradaInicial;
    }

    public void setParadaInicial(String paradaInicial) {
        this.paradaInicial = paradaInicial;
    }

    public String getParadaFinal() {
        return paradaFinal;
    }

    public void setParadaFinal(String paradaFinal) {
        this.paradaFinal = paradaFinal;
    }

    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(Double tarifa) {
        this.tarifa = tarifa;
    }

    public boolean valida(SecaoItinerario secaoItinerario) {

        if(super.valida(secaoItinerario) && secaoItinerario.getItinerario() != null
                && secaoItinerario.getNome() != null && secaoItinerario.getTarifa() != null){
            return true;
        } else{
            return false;
        }

    }
}
