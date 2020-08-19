package br.com.vostre.circular.model.pojo;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import br.com.vostre.circular.model.Cidade;

public class CidadeEstado {

    @Embedded
    Cidade cidade;

    @ColumnInfo(name = "idEstado")
    String idEstado;

    @ColumnInfo(name = "nomeEstado")
    String nomeEstado;

    @ColumnInfo(name = "totalBairros")
    Integer totalBairros;

    public CidadeEstado(){
        this.setCidade(new Cidade());
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
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

    public Integer getTotalBairros() {
        return totalBairros;
    }

    public void setTotalBairros(Integer totalBairros) {
        this.totalBairros = totalBairros;
    }

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof CidadeEstado)){
            return false;
        }

        CidadeEstado cidade = (CidadeEstado) o;
        return cidade.getCidade().getId().equals(this.getCidade().getId());
    }
}
