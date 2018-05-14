package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"nome"},
        unique = true)})
public class Pais extends EntidadeSlug {

    @NonNull
    private String sigla;

    public Pais(){
        this.setAtivo(true);
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public boolean valida(Pais pais) {

        if(super.valida(pais) && pais.getSigla() != null){
            return true;
        } else{
            return false;
        }

    }
}
