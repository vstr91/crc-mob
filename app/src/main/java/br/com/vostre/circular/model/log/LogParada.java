package br.com.vostre.circular.model.log;

import androidx.room.Entity;
import androidx.annotation.NonNull;

@Entity(tableName = "log_parada")
public class LogParada extends LogConsulta {

    @NonNull
    private String parada;

    public LogParada(){
        this.setAtivo(true);
    }

    @NonNull
    public String getParada() {
        return parada;
    }

    public void setParada(@NonNull String parada) {
        this.parada = parada;
    }

    public boolean valida(LogParada log) {

        if(super.valida(log)){
            return true;
        } else{
            return false;
        }

    }

}
