package br.com.vostre.circular.model.pojo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;

import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.SecaoItinerario;

public class SecaoItinerarioParada {

    @Embedded
    SecaoItinerario secaoItinerario;

    @ColumnInfo(name = "idPartida")
    String idPartida;

    @ColumnInfo(name = "nomePartida")
    String nomePartida;

    @ColumnInfo(name = "nomeBairroPartida")
    String nomeBairroPartida;

    @ColumnInfo(name = "nomeCidadePartida")
    String nomeCidadePartida;

    @ColumnInfo(name = "idDestino")
    String idDestino;

    @ColumnInfo(name = "nomeDestino")
    String nomeDestino;

    @ColumnInfo(name = "nomeBairroDestino")
    String nomeBairroDestino;

    @ColumnInfo(name = "nomeCidadeDestino")
    String nomeCidadeDestino;

    @Ignore
    String bairroComCidade;

    public SecaoItinerarioParada(){
        SecaoItinerario si = new SecaoItinerario();
        setSecaoItinerario(si);
    }

    public SecaoItinerario getSecaoItinerario() {
        return secaoItinerario;
    }

    public void setSecaoItinerario(SecaoItinerario secaoItinerario) {
        this.secaoItinerario = secaoItinerario;
    }

    public String getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(String idPartida) {
        this.idPartida = idPartida;
    }

    public String getNomePartida() {
        return nomePartida;
    }

    public void setNomePartida(String nomePartida) {
        this.nomePartida = nomePartida;
    }

    public String getNomeBairroPartida() {
        return nomeBairroPartida;
    }

    public void setNomeBairroPartida(String nomeBairroPartida) {
        this.nomeBairroPartida = nomeBairroPartida;
    }

    public String getNomeCidadePartida() {
        return nomeCidadePartida;
    }

    public void setNomeCidadePartida(String nomeCidadePartida) {
        this.nomeCidadePartida = nomeCidadePartida;
    }

    public String getIdDestino() {
        return idDestino;
    }

    public void setIdDestino(String idDestino) {
        this.idDestino = idDestino;
    }

    public String getNomeDestino() {
        return nomeDestino;
    }

    public void setNomeDestino(String nomeDestino) {
        this.nomeDestino = nomeDestino;
    }

    public String getNomeBairroDestino() {
        return nomeBairroDestino;
    }

    public void setNomeBairroDestino(String nomeBairroDestino) {
        this.nomeBairroDestino = nomeBairroDestino;
    }

    public String getNomeCidadeDestino() {
        return nomeCidadeDestino;
    }

    public void setNomeCidadeDestino(String nomeCidadeDestino) {
        this.nomeCidadeDestino = nomeCidadeDestino;
    }

    public String getBairroComCidade() {
        return bairroComCidade;
    }

    public void setBairroComCidade(String bairroComCidade) {
        this.bairroComCidade = bairroComCidade;
    }

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof SecaoItinerarioParada)){
            return false;
        }

        SecaoItinerarioParada secao = (SecaoItinerarioParada) o;
        return secao.getSecaoItinerario().getId().equals(this.getSecaoItinerario().getId());
    }

}
