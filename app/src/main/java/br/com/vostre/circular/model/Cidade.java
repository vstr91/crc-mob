package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"nome", "estado"},
        unique = true)})
public class Cidade extends EntidadeSlug {

    private String brasao;

    @NonNull
    private String estado;

    public String getBrasao() {
        return brasao;
    }

    public void setBrasao(String brasao) {
        this.brasao = brasao;
    }

    @NonNull
    public String getEstado() {
        return estado;
    }

    public void setEstado(@NonNull String estado) {
        this.estado = estado;
    }

    public boolean valida(Cidade cidade) {

        if(super.valida(cidade) && cidade.getEstado() != null){
            return true;
        } else{
            return false;
        }

    }

}
