package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;

@Entity(tableName = "ponto_interesse_sugestao")
public class PontoInteresseSugestao extends PontoInteresse {

    private String observacao;

    private String pontoInteresse;

    private int status = 0;

    public String getPontoInteresse() {
        return pontoInteresse;
    }

    public void setPontoInteresse(String pontoInteresse) {
        this.pontoInteresse = pontoInteresse;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean valida(PontoInteresseSugestao poi) {

        if(super.valida(poi)){
            return true;
        } else{
            return false;
        }

    }
}
