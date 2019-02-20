package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

@Entity
public class Acesso {

    @NonNull
    private String identificadorUnico;

    @NonNull
    private DateTime dataCriacao;

    @NonNull
    private DateTime dataValidacao;

    @NonNull
    public String getIdentificadorUnico() {
        return identificadorUnico;
    }

    public void setIdentificadorUnico(@NonNull String identificadorUnico) {
        this.identificadorUnico = identificadorUnico;
    }

    @NonNull
    public DateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(@NonNull DateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @NonNull
    public DateTime getDataValidacao() {
        return dataValidacao;
    }

    public void setDataValidacao(@NonNull DateTime dataValidacao) {
        this.dataValidacao = dataValidacao;
    }

    public boolean valida(Acesso acesso) {

        if(acesso.getIdentificadorUnico() != null && !acesso.getIdentificadorUnico().isEmpty()){
            return true;
        } else{
            return false;
        }

    }

}
