package br.com.vostre.circular.model.pojo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;

import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaSugestao;

public class ParadaSugestaoBairro {

    @Embedded
    ParadaSugestao parada;

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

    @ColumnInfo(name = "usuario")
    String usuario;

    @Ignore
    String nomeBairroComCidade;

    public ParadaSugestaoBairro(){
        setParada(new ParadaSugestao());
    }

    public ParadaSugestao getParada() {
        return parada;
    }

    public void setParada(ParadaSugestao parada) {
        this.parada = parada;
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNomeBairroComCidade() {
        return nomeBairro+" - "+nomeCidade+" / "+siglaEstado;
    }

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof ParadaSugestaoBairro)){
            return false;
        }

        ParadaSugestaoBairro parada = (ParadaSugestaoBairro) o;
        return parada.getParada().getId().equals(this.getParada().getId());
    }

}
