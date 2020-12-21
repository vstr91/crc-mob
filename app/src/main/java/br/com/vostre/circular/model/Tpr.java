package br.com.vostre.circular.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import org.joda.time.DateTime;

@Entity(tableName = "tpr")
public class Tpr extends EntidadeBase {

    private String sigla;

    @NonNull
    private Double tarifa;

    @NonNull
    private Double distanciaAcumuladaInicial;

    @NonNull
    private Double distanciaAcumulada;

    @NonNull
    private DateTime tempo;

    @NonNull
    private boolean acessivel;

    @NonNull
    private String empresa;

    private String observacao;

    @NonNull
    private boolean mostraRua;

    @NonNull
    private String idItinerario;

    @NonNull
    private String idBairroPartida;

    @NonNull
    private String bairroPartida;

    @NonNull
    private String cidadePartida;

    @NonNull
    private String idBairroDestino;

    @NonNull
    private String bairroDestino;

    @NonNull
    private String cidadeDestino;

    private Double distanciaTrechoMetros;

    private DateTime tempoTrecho;

    private Double tarifaTrecho;

    @NonNull
    private Integer inicio;

    @NonNull
    private Integer fim;

    private String aliasBairroPartida;

    private String aliasCidadePartida;

    private String aliasBairroDestino;

    private String aliasCidadeDestino;

    public Tpr(){
        this.setAtivo(true);
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

    public boolean isAcessivel() {
        return acessivel;
    }

    public void setAcessivel(boolean acessivel) {
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

    public boolean isMostraRua() {
        return mostraRua;
    }

    public void setMostraRua(boolean mostraRua) {
        this.mostraRua = mostraRua;
    }

    @NonNull
    public String getBairroPartida() {
        return bairroPartida;
    }

    public void setBairroPartida(@NonNull String bairroPartida) {
        this.bairroPartida = bairroPartida;
    }

    @NonNull
    public String getCidadePartida() {
        return cidadePartida;
    }

    public void setCidadePartida(@NonNull String cidadePartida) {
        this.cidadePartida = cidadePartida;
    }

    @NonNull
    public String getBairroDestino() {
        return bairroDestino;
    }

    public void setBairroDestino(@NonNull String bairroDestino) {
        this.bairroDestino = bairroDestino;
    }

    @NonNull
    public String getCidadeDestino() {
        return cidadeDestino;
    }

    public void setCidadeDestino(@NonNull String cidadeDestino) {
        this.cidadeDestino = cidadeDestino;
    }

    public String getAliasBairroPartida() {
        return aliasBairroPartida;
    }

    public void setAliasBairroPartida(String aliasBairroPartida) {
        this.aliasBairroPartida = aliasBairroPartida;
    }

    public String getAliasCidadePartida() {
        return aliasCidadePartida;
    }

    public void setAliasCidadePartida(String aliasCidadePartida) {
        this.aliasCidadePartida = aliasCidadePartida;
    }

    public String getAliasBairroDestino() {
        return aliasBairroDestino;
    }

    public void setAliasBairroDestino(String aliasBairroDestino) {
        this.aliasBairroDestino = aliasBairroDestino;
    }

    public String getAliasCidadeDestino() {
        return aliasCidadeDestino;
    }

    public void setAliasCidadeDestino(String aliasCidadeDestino) {
        this.aliasCidadeDestino = aliasCidadeDestino;
    }

    @NonNull
    public Double getDistanciaAcumuladaInicial() {
        return distanciaAcumuladaInicial;
    }

    public void setDistanciaAcumuladaInicial(@NonNull Double distanciaAcumuladaInicial) {
        this.distanciaAcumuladaInicial = distanciaAcumuladaInicial;
    }

    @NonNull
    public Double getDistanciaAcumulada() {
        return distanciaAcumulada;
    }

    public void setDistanciaAcumulada(@NonNull Double distanciaAcumulada) {
        this.distanciaAcumulada = distanciaAcumulada;
    }

    @NonNull
    public DateTime getTempo() {
        return tempo;
    }

    public void setTempo(@NonNull DateTime tempo) {
        this.tempo = tempo;
    }

    @NonNull
    public String getIdItinerario() {
        return idItinerario;
    }

    public void setIdItinerario(@NonNull String idItinerario) {
        this.idItinerario = idItinerario;
    }

    @NonNull
    public String getIdBairroPartida() {
        return idBairroPartida;
    }

    public void setIdBairroPartida(@NonNull String idBairroPartida) {
        this.idBairroPartida = idBairroPartida;
    }

    @NonNull
    public String getIdBairroDestino() {
        return idBairroDestino;
    }

    public void setIdBairroDestino(@NonNull String idBairroDestino) {
        this.idBairroDestino = idBairroDestino;
    }

    @NonNull
    public Double getDistanciaTrechoMetros() {
        return distanciaTrechoMetros;
    }

    public void setDistanciaTrechoMetros(@NonNull Double distanciaTrechoMetros) {
        this.distanciaTrechoMetros = distanciaTrechoMetros;
    }

    @NonNull
    public DateTime getTempoTrecho() {
        return tempoTrecho;
    }

    public void setTempoTrecho(@NonNull DateTime tempoTrecho) {
        this.tempoTrecho = tempoTrecho;
    }

    @NonNull
    public Double getTarifaTrecho() {
        return tarifaTrecho;
    }

    public void setTarifaTrecho(@NonNull Double tarifaTrecho) {
        this.tarifaTrecho = tarifaTrecho;
    }

    @NonNull
    public Integer getInicio() {
        return inicio;
    }

    public void setInicio(@NonNull Integer inicio) {
        this.inicio = inicio;
    }

    @NonNull
    public Integer getFim() {
        return fim;
    }

    public void setFim(@NonNull Integer fim) {
        this.fim = fim;
    }
}
