package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"nome"},
        unique = true)}, tableName = "tipo_problema")
public class TipoProblema extends EntidadeSlug {

    private String descricao;

    public TipoProblema(){
        this.setAtivo(true);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean valida(TipoProblema pais) {

        if(super.valida(pais) && pais.getDescricao() != null && !pais.getDescricao().isEmpty()){
            return true;
        } else{
            return false;
        }

    }

}