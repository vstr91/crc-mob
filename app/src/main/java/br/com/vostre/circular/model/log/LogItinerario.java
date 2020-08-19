package br.com.vostre.circular.model.log;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "log_itinerario")
public class LogItinerario extends LogConsulta {

    private String partida;

    private String destino;

    private String itinerario;

    private String linha;

    public LogItinerario(){
        this.setAtivo(true);
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getItinerario() {
        return itinerario;
    }

    public void setItinerario(String itinerario) {
        this.itinerario = itinerario;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public boolean valida(LogItinerario log) {

        if(super.valida(log)){
            return true;
        } else{
            return false;
        }

    }

}
