package br.com.vostre.circular.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(indices = {@Index(value = {"parada", "sugestao"},
        unique = true)}, tableName = "historico_parada")
public class HistoricoParada extends EntidadeBase {

    @NonNull
    private String parada;

    @NonNull
    private String sugestao;

    @NonNull
    public String getParada() {
        return parada;
    }

    public void setParada(@NonNull String parada) {
        this.parada = parada;
    }

    @NonNull
    public String getSugestao() {
        return sugestao;
    }

    public void setSugestao(@NonNull String sugestao) {
        this.sugestao = sugestao;
    }

    public boolean valida(HistoricoParada paradaItinerario) {

        if(super.valida(paradaItinerario) && paradaItinerario.getParada() != null
                && paradaItinerario.getSugestao() != null){
            return true;
        } else{
            return false;
        }

    }
}
