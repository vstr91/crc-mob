package br.com.vostre.circular.model.pojo;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Ignore;

import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Parada;

public class BairroCidade {

    @Embedded
    Bairro bairro;

    @ColumnInfo(name = "idCidade")
    String idCidade;

    @ColumnInfo(name = "nomeCidade")
    String nomeCidade;

    @ColumnInfo(name = "brasao")
    String brasao;

    @ColumnInfo(name = "idEstado")
    String idEstado;

    @ColumnInfo(name = "nomeEstado")
    String nomeEstado;

    @ColumnInfo(name = "siglaEstado")
    String siglaEstado;

    @Ignore
    String nomeCidadeComEstado;

    @Ignore
    List<ParadaBairro> paradas;

    public List<ParadaBairro> getParadas() {
        return paradas;
    }

    public void setParadas(List<ParadaBairro> paradas) {
        this.paradas = paradas;
    }

    public String getBrasao() {
        return brasao;
    }

    public void setBrasao(String brasao) {
        this.brasao = brasao;
    }

    public BairroCidade(){
        setBairro(new Bairro());
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
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

    public String getNomeCidadeComEstado() {
        return nomeCidade+" / "+siglaEstado;
    }

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof BairroCidade)){
            return false;
        }

        BairroCidade bairro = (BairroCidade) o;
        return bairro.getBairro().getId().equals(this.getBairro().getId());
    }

}
