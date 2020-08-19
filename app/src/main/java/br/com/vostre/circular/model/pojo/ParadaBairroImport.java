package br.com.vostre.circular.model.pojo;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Ignore;
import android.location.Location;
import android.location.LocationManager;

import br.com.vostre.circular.model.Parada;

public class ParadaBairroImport {

    @Embedded
    Parada parada;

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
    String nomeBairroComCidade;

    @Ignore
    String observacao;

    @Ignore
    Float distancia;

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public ParadaBairroImport(){
        setParada(new Parada());
    }

    public Float getDistancia() {
        return distancia;
    }

    public void setDistancia(Float distancia) {
        this.distancia = distancia;
    }

    public Parada getParada() {
        return parada;
    }

    public void setParada(Parada parada) {
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

    public String getNomeBairroComCidade() {
        return nomeBairro+" - "+nomeCidade+" / "+siglaEstado;
    }

    public Location getLocation(){
        Location l = new Location(LocationManager.GPS_PROVIDER);
        l.setLatitude(this.getParada().getLatitude());
        l.setLongitude(this.getParada().getLongitude());
        return l;
    }

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof ParadaBairroImport)){
            return false;
        }

        ParadaBairroImport parada = (ParadaBairroImport) o;

        if(parada.getParada().getLatitude() == null || parada.getParada().getLongitude() == null ||
                this.getParada().getLatitude() == null || this.getParada().getLongitude() == null){
            return parada.getParada().getId().equals(this.getParada().getId());
        } else{
            return parada.getParada().getLatitude().equals(this.getParada().getLatitude()) &&
                    parada.getParada().getLongitude().equals(this.getParada().getLongitude());
        }

    }

}
