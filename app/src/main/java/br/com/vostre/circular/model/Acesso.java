package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.UUID;

@Entity
public class Acesso {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String identificadorUnico;

    @NonNull
    private DateTime dataCriacao;

    @NonNull
    private DateTime dataValidacao;

    public Acesso(){
        this.setId(UUID.randomUUID().toString());
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

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
