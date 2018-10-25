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

    @ColumnInfo(name = "idBairroPartida")
    String idBairroPartida;

    @ColumnInfo(name = "idPartida")
    String idPartida;

    @ColumnInfo(name = "nomePartida")
    String nomePartida;

    @ColumnInfo(name = "bairroPartida")
    String nomeBairroPartida;

    @ColumnInfo(name = "cidadePartida")
    String nomeCidadePartida;

    @ColumnInfo(name = "idBairroDestino")
    String idBairroDestino;

    @ColumnInfo(name = "nomeDestino")
    String nomeDestino;

    @ColumnInfo(name = "bairroDestino")
    String nomeBairroDestino;

    @ColumnInfo(name = "cidadeDestino")
    String nomeCidadeDestino;

    @ColumnInfo(name = "nomeEmpresa")
    String nomeEmpresa;

    @ColumnInfo(name = "proximoHorario")
    String proximoHorario;

    @ColumnInfo(name = "idProximoHorario")
    String idProximoHorario;

    @ColumnInfo(name = "observacaoProximoHorario")
    String observacaoProximoHorario;

    @ColumnInfo(name = "horarioAnterior")
    String horarioAnterior;

    @ColumnInfo(name = "idHorarioAnterior")
    String idHorarioAnterior;

    @ColumnInfo(name = "observacaoHorarioAnterior")
    String observacaoHorarioAnterior;

    @ColumnInfo(name = "horarioSeguinte")
    String horarioSeguinte;

    @ColumnInfo(name = "idHorarioSeguinte")
    String idHorarioSeguinte;

    @ColumnInfo(name = "observacaoHorarioSeguinte")
    String observacaoHorarioSeguinte;

    @Ignore
    public boolean selecionado;

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public String getObservacaoProximoHorario() {
        return observacaoProximoHorario;
    }

    public void setObservacaoProximoHorario(String observacaoProximoHorario) {
        this.observacaoProximoHorario = observacaoProximoHorario;
    }

    public String getObservacaoHorarioAnterior() {
        return observacaoHorarioAnterior;
    }

    public void setObservacaoHorarioAnterior(String observacaoHorarioAnterior) {
        this.observacaoHorarioAnterior = observacaoHorarioAnterior;
    }

    public String getObservacaoHorarioSeguinte() {
        return observacaoHorarioSeguinte;
    }

    public void setObservacaoHorarioSeguinte(String observacaoHorarioSeguinte) {
        this.observacaoHorarioSeguinte = observacaoHorarioSeguinte;
    }

    public String getIdProximoHorario() {
        return idProximoHorario;
    }

    public void setIdProximoHorario(String idProximoHorario) {
        this.idProximoHorario = idProximoHorario;
    }

    public String getHorarioAnterior() {
        return horarioAnterior;
    }

    public void setHorarioAnterior(String horarioAnterior) {
        this.horarioAnterior = horarioAnterior;
    }

    public String getIdHorarioAnterior() {
        return idHorarioAnterior;
    }

    public void setIdHorarioAnterior(String idHorarioAnterior) {
        this.idHorarioAnterior = idHorarioAnterior;
    }

    public String getHorarioSeguinte() {
        return horarioSeguinte;
    }

    public void setHorarioSeguinte(String horarioSeguinte) {
        this.horarioSeguinte = horarioSeguinte;
    }

    public String getIdHorarioSeguinte() {
        return idHorarioSeguinte;
    }

    public void setIdHorarioSeguinte(String idHorarioSeguinte) {
        this.idHorarioSeguinte = idHorarioSeguinte;
    }

    public String getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(String idPartida) {
        this.idPartida = idPartida;
    }

    public String getIdBairroPartida() {
        return idBairroPartida;
    }

    public void setIdBairroPartida(String idBairroPartida) {
        this.idBairroPartida = idBairroPartida;
    }

    public String getIdBairroDestino() {
        return idBairroDestino;
    }

    public void setIdBairroDestino(String idBairroDestino) {
        this.idBairroDestino = idBairroDestino;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getProximoHorario() {
        return proximoHorario;
    }

    public void setProximoHorario(String proximoHorario) {
        this.proximoHorario = proximoHorario;
    }

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
