package br.com.vostre.circular.model;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "parada_sugestao")
public class ParadaSugestao extends Parada {

    private String observacao;

    private String parada;

    private int status = 0;

    public String getParada() {
        return parada;
    }

    public void setParada(String parada) {
        this.parada = parada;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean valida(ParadaSugestao parada) {

        if(super.valida(parada)){
            return true;
        } else{
            return false;
        }

    }
}
