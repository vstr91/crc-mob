package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"itinerario", "tarifa"},
        unique = true)}, tableName = "historico_itinerario")
public class HistoricoItinerario extends EntidadeBase {

    @NonNull
    private String itinerario;

    @NonNull
    private Double tarifa;

    @NonNull
    public String getItinerario() {
        return itinerario;
    }

    public void setItinerario(@NonNull String itinerario) {
        this.itinerario = itinerario;
    }

    @NonNull
    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(@NonNull Double tarifa) {
        this.tarifa = tarifa;
    }

    public boolean valida(HistoricoItinerario historicoItinerario) {

        if(super.valida(historicoItinerario) && historicoItinerario.getItinerario() != null
                && historicoItinerario.getTarifa() != null){
            return true;
        } else{
            return false;
        }

    }
}
