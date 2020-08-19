package br.com.vostre.circular.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.annotation.NonNull;

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

        if(super.valida(pais) && pais.getSigla() != null && !pais.getSigla().isEmpty()){
            return true;
        } else{
            return false;
        }

    }

}
