package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(tableName = "parada_sugestao", indices = {@Index(value = {"nome", "bairro"},
        unique = true)})
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
