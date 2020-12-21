package br.com.vostre.circular.model.pojo;

import android.location.Location;
import android.location.LocationManager;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Ignore;

import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.Parada;

public class ImagemParadaBairro {

    @Embedded
    ImagemParada imagemParada;

    @ColumnInfo(name = "idParada")
    String idParada;

    @ColumnInfo(name = "nomeParada")
    String nomeParada;

    @ColumnInfo(name = "sentidoParada")
    String sentidoParada;

    @ColumnInfo(name = "idBairro")
    String idBairro;

    @ColumnInfo(name = "nomeBairro")
    String nomeBairro;

    @ColumnInfo(name = "idCidade")
    String idCidade;

    @ColumnInfo(name = "nomeCidade")
    String nomeCidade;

    @ColumnInfo(name = "idEstado")
    String idEstado;

    @ColumnInfo(name = "nomeEstado")
    String nomeEstado;

    @ColumnInfo(name = "siglaEstado")
    String siglaEstado;

    @Ignore
    public String nomeBairroComCidade;

    public String getIdParada() {
        return idParada;
    }

    public void setIdParada(String idParada) {
        this.idParada = idParada;
    }

    public String getNomeParada() {
        return nomeParada;
    }

    public void setNomeParada(String nomeParada) {
        this.nomeParada = nomeParada;
    }

    public String getSentidoParada() {
        return sentidoParada;
    }

    public void setSentidoParada(String sentidoParada) {
        this.sentidoParada = sentidoParada;
    }

    public void setNomeBairroComCidade(String nomeBairroComCidade) {
        this.nomeBairroComCidade = nomeBairroComCidade;
    }

    public ImagemParada getImagemParada() {
        return imagemParada;
    }

    public void setImagemParada(ImagemParada imagemParada) {
        this.imagemParada = imagemParada;
    }

    public String getIdBairro() {
        return idBairro;
    }

    public void setIdBairro(String idBairro) {
        this.idBairro = idBairro;
    }

    public String getNomeBairro() {
        return nomeBairro;
    }

    public void setNomeBairro(String nomeBairro) {
        this.nomeBairro = nomeBairro;
    }

    public String getIdCidade() {
        return idCidade;
    }

    public void setIdCidade(String idCidade) {
        this.idCidade = idCidade;
    }

    public String getNomeCidade() {
        return nomeCidade;
    }

    public void setNomeCidade(String nomeCidade) {
        this.nomeCidade = nomeCidade;
    }

    public String getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    public String getNomeEstado() {
        return nomeEstado;
    }

    public void setNomeEstado(String nomeEstado) {
        this.nomeEstado = nomeEstado;
    }

    public String getSiglaEstado() {
        return siglaEstado;
    }

    public void setSiglaEstado(String siglaEstado) {
        this.siglaEstado = siglaEstado;
    }

    public String getNomeBairroComCidade() {
        return nomeBairro+" - "+nomeCidade+" / "+siglaEstado;
    }

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof ImagemParadaBairro)){
            return false;
        }

        ImagemParadaBairro parada = (ImagemParadaBairro) o;
        return parada.getImagemParada().getId().equals(this.getImagemParada().getId());
    }

}
