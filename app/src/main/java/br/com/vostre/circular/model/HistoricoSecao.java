package br.com.vostre.circular.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(indices = {@Index(value = {"secao", "tarifa"},
        unique = true)}, tableName = "historico_secao")
public class HistoricoSecao extends EntidadeBase {

    @NonNull
    private String secao;

    @NonNull
    private Double tarifa;

    @NonNull
    public String getSecao() {
        return secao;
    }

    public void setSecao(@NonNull String secao) {
        this.secao = secao;
    }

    @NonNull
    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(@NonNull Double tarifa) {
        this.tarifa = tarifa;
    }

    public boolean valida(HistoricoSecao historicoSecao) {

        if(super.valida(historicoSecao) && historicoSecao.getSecao() != null
                && historicoSecao.getTarifa() != null){
            return true;
        } else{
            return false;
        }

    }
}
