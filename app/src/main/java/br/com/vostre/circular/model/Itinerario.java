package br.com.vostre.circular.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.annotation.NonNull;

import org.joda.time.DateTime;

@Entity
public class Itinerario extends EntidadeBase {

    private String sigla;

    @NonNull
    private Double tarifa;

    private Double distancia;

    private Double distanciaMetros;

    private DateTime tempo;

    @NonNull
    private Boolean acessivel;

    @NonNull
    private String empresa;

    private String observacao;

    private Boolean mostraRuas;

    private String aliasBairroPartida;

    private String aliasCidadePartida;

    private String aliasBairroDestino;

    private String aliasCidadeDestino;

    private String paradaInicial;

    private String paradaFinal;

    @NonNull
    private int totalParadas = 0;

    private String trajeto;

    public Itinerario(){
        this.setAcessivel(false);
        this.setMostraRuas(false);
        this.totalParadas = 0;
    }

    public String getTrajeto() {
        return trajeto;
    }

    public void setTrajeto(String trajeto) {
        this.trajeto = trajeto;
    }

    public String getParadaInicial() {
        return paradaInicial;
    }

    public void setParadaInicial(String paradaInicial) {
        this.paradaInicial = paradaInicial;
    }

    public String getParadaFinal() {
        return paradaFinal;
    }

    public void setParadaFinal(String paradaFinal) {
        this.paradaFinal = paradaFinal;
    }

    public int getTotalParadas() {
        return totalParadas;
    }

    public void setTotalParadas(@NonNull int totalParadas) {
        this.totalParadas = totalParadas;
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

    public Double getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(Double distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
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

    @NonNull
    public Boolean getMostraRuas() {
        return mostraRuas;
    }

    public void setMostraRuas(@NonNull Boolean mostraRuas) {
        this.mostraRuas = mostraRuas;
    }

    public boolean valida(Itinerario itinerario) {

        if(super.valida(itinerario) && itinerario.getAcessivel() != null && itinerario.getMostraRuas() != null && itinerario.getTarifa() != null && itinerario.getTarifa() > 0
                && itinerario.getEmpresa() != null){
            return true;
        } else{
            return false;
        }

    }
}
