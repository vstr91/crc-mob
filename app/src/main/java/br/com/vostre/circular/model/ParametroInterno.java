package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

@Entity(tableName = "parametro_interno")
public class ParametroInterno extends EntidadeBase {

    @NonNull
    private DateTime dataUltimoAcesso;

    @NonNull
    private String identificadorUnico;

    @NonNull
    public DateTime getDataUltimoAcesso() {
        return dataUltimoAcesso;
    }

    public void setDataUltimoAcesso(@NonNull DateTime dataUltimoAcesso) {
        this.dataUltimoAcesso = dataUltimoAcesso;
    }

    @NonNull
    public String getIdentificadorUnico() {
        return identificadorUnico;
    }

    public void setIdentificadorUnico(@NonNull String identificadorUnico) {
        this.identificadorUnico = identificadorUnico;
    }

    public boolean valida(ParametroInterno parametro) {

        if(super.valida(parametro) && parametro.getDataUltimoAcesso() != null && parametro.getIdentificadorUnico() != null){
            return true;
        } else{
            return false;
        }

    }
}
