package br.com.vostre.circular.model.pojo;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Ignore;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.Tpr2;

public class TrechoPartidaDestino {

    @Embedded
    Tpr2 tpr2;

    @ColumnInfo(name = "bairroPartida")
    String nomeBairroPartida;

    @ColumnInfo(name = "cidadePartida")
    String nomeCidadePartida;

    @ColumnInfo(name = "bairroDestino")
    String nomeBairroDestino;

    @ColumnInfo(name = "cidadeDestino")
    String nomeCidadeDestino;

    public Tpr2 getTpr2() {
        return tpr2;
    }

    public void setTpr2(Tpr2 tpr2) {
        this.tpr2 = tpr2;
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

    public String getPartidaEDestinoResumido(){

        if(getNomeCidadePartida() != null && getNomeCidadePartida().equals(getNomeCidadeDestino())){
            return getNomeBairroPartida()+" x "+getNomeBairroDestino();
        } else{
            return getNomeCidadePartida()+" x "+getNomeCidadeDestino();
        }


    }

}
