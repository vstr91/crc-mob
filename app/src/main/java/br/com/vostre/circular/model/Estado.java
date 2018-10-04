package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"nome", "pais"},
        unique = true)})
public class Estado extends EntidadeSlug {

    @NonNull
    private String sigla;

    @NonNull
    private String pais;

    public Estado(){
        this.setAtivo(true);
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    @NonNull
    public String getPais() {
        return pais;
    }

    public void setPais(@NonNull String pais) {
        this.pais = pais;
    }

    public boolean valida(Estado estado) {

        if(super.valida(estado) && estado.getSigla() != null && !estado.getSigla().isEmpty() && estado.getPais() != null){
            return true;
        } else{
            return false;
        }

    }
}
