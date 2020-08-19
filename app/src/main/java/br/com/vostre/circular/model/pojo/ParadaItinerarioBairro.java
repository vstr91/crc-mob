package br.com.vostre.circular.model.pojo;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Ignore;

import br.com.vostre.circular.model.ParadaItinerario;

public class ParadaItinerarioBairro {

    @Embedded
    ParadaItinerario paradaItinerario;

    @ColumnInfo(name = "idParada")
    String idParada;

    @ColumnInfo(name = "nomeParada")
    String nomeParada;

    @ColumnInfo(name = "idBairro")
    String idBairro;

    @ColumnInfo(name = "nomeBairro")
    String nomeBairro;

    @ColumnInfo(name = "idCidade")
    String idCidade;

    @ColumnInfo(name = "nomeCidade")
    String nomeCidade;

    @ColumnInfo(name = "latitude")
    String latitude;

    @ColumnInfo(name = "longitude")
    String longitude;

    @Ignore
    String bairroComCidade;

    public ParadaItinerarioBairro(){
        ParadaItinerario pit = new ParadaItinerario();
        pit.setDestaque(false);
        setParadaItinerario(pit);
    }

    public ParadaItinerario getParadaItinerario() {
        return paradaItinerario;
    }

    public void setParadaItinerario(ParadaItinerario paradaItinerario) {
        this.paradaItinerario = paradaItinerario;
    }

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

    public String getBairroComCidade() {
        return nomeBairro+" - "+nomeCidade;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof ParadaItinerarioBairro)){
            return false;
        }

        ParadaItinerarioBairro paradaItinerario = (ParadaItinerarioBairro) o;
        return paradaItinerario.getParadaItinerario().getParada().equals(this.getParadaItinerario().getParada());
    }

}
