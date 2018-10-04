package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

@Entity
public class Itinerario extends EntidadeBase {

    private String sigla;

    @NonNull
    private Double tarifa;

    private Double distancia;

    private DateTime tempo;

    @NonNull
    private Boolean acessivel;

    @NonNull
    private String empresa;

    private String observacao;

    public Itinerario(){
        this.setAcessivel(false);
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    @NonNull
    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(@NonNull Double tarifa) {
        this.tarifa = tarifa;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public DateTime getTempo() {
        return tempo;
    }

    public void setTempo(DateTime tempo) {
        this.tempo = tempo;
    }

    @NonNull
    public Boolean getAcessivel() {
        return acessivel;
    }

    public void setAcessivel(@NonNull Boolean acessivel) {
        this.acessivel = acessivel;
    }

    @NonNull
    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(@NonNull String empresa) {
        this.empresa = empresa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean valida(Itinerario itinerario) {

        if(super.valida(itinerario) && itinerario.getAcessivel() != null && itinerario.getTarifa() != null && itinerario.getTarifa() > 0
                && itinerario.getEmpresa() != null){
            return true;
        } else{
            return false;
        }

    }
}
