package br.com.vostre.circular.model.pojo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import java.util.List;

import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ParadaItinerario;

public class ItinerarioPartidaDestino {

    @Embedded
    Itinerario itinerario;

    @Ignore
    public List<ParadaItinerarioBairro> paradasItinerario;

    @ColumnInfo(name = "nomePartida")
    String nomePartida;

    @ColumnInfo(name = "bairroPartida")
    String nomeBairroPartida;

    @ColumnInfo(name = "cidadePartida")
    String nomeCidadePartida;

    @ColumnInfo(name = "nomeDestino")
    String nomeDestino;

    @ColumnInfo(name = "bairroDestino")
    String nomeBairroDestino;

    @ColumnInfo(name = "cidadeDestino")
    String nomeCidadeDestino;

    public ItinerarioPartidaDestino(){
        Itinerario itinerario = new Itinerario();
        this.itinerario = itinerario;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public List<ParadaItinerarioBairro> getParadasItinerario() {
        return paradasItinerario;
    }

    public void setParadasItinerario(List<ParadaItinerarioBairro> paradasItinerario) {
        this.paradasItinerario = paradasItinerario;
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

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof ItinerarioPartidaDestino)){
            return false;
        }

        ItinerarioPartidaDestino paradaItinerario = (ItinerarioPartidaDestino) o;
        return paradaItinerario.getItinerario().getId().equals(this.getItinerario().getId());
    }

}
