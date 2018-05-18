package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"nome", "cidade"},
        unique = true)})
public class Bairro extends EntidadeSlug {

    @NonNull
    private String cidade;

    @NonNull
    public String getCidade() {
        return cidade;
    }

    public void setCidade(@NonNull String cidade) {
        this.cidade = cidade;
    }

    public boolean valida(Bairro bairro) {

        if(super.valida(bairro) && bairro.getCidade() != null){
            return true;
        } else{
            return false;
        }

    }
}
