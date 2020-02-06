package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import java.util.List;

@Entity(indices = {@Index(value = {"nome", "bairro"},
        unique = true)})
public class Parada extends EntidadeSlug {

    @NonNull
    private Double latitude;

    @NonNull
    private Double longitude;

    private Double taxaDeEmbarque;

    private String imagem;

    @NonNull
    private String bairro;

    @NonNull
    private boolean imagemEnviada = true;

    private int sentido = -1; // 0 = esquerda | 1 = direita | 2 = ambas | -1 = nao mostra ||| NOVO => 0 = centro | 1 = bairro | -1 = nao mostra

    private String rua;

    private String cep;

    private List<String> servicos;

    public List<String> getServicos() {
        return servicos;
    }

    public void setServicos(List<String> servicos) {
        this.servicos = servicos;
    }

    public int getSentido() {
        return sentido;
    }

    public void setSentido(int sentido) {
        this.sentido = sentido;
    }

    @NonNull
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@NonNull Double latitude) {
        this.latitude = latitude;
    }

    @NonNull
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(@NonNull Double longitude) {
        this.longitude = longitude;
    }

    public Double getTaxaDeEmbarque() {
        return taxaDeEmbarque;
    }

    public void setTaxaDeEmbarque(Double taxaDeEmbarque) {
        this.taxaDeEmbarque = taxaDeEmbarque;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    @NonNull
    public String getBairro() {
        return bairro;
    }

    public void setBairro(@NonNull String bairro) {
        this.bairro = bairro;
    }

    @NonNull
    public boolean isImagemEnviada() {
        return imagemEnviada;
    }

    public void setImagemEnviada(@NonNull boolean imagemEnviada) {
        this.imagemEnviada = imagemEnviada;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public boolean valida(Parada parada) {

        if(super.valida(parada) && parada.getLatitude() != null && parada.getLongitude() != null
                && parada.getBairro() != null){
            return true;
        } else{
            return false;
        }

    }
}
