package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"nome"},
        unique = true)})
public class Parametro extends EntidadeSlug {

    @NonNull
    private String valor;

    @NonNull
    public String getValor() {
        return valor;
    }

    public void setValor(@NonNull String valor) {
        this.valor = valor;
    }

    public boolean valida(Parametro parametro) {

        if(super.valida(parametro) && parametro.getValor() != null){
            return true;
        } else{
            return false;
        }

    }
}
