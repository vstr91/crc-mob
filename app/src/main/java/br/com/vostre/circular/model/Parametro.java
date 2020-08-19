package br.com.vostre.circular.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.annotation.NonNull;

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
