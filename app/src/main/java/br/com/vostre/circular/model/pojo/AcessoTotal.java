package br.com.vostre.circular.model.pojo;

import androidx.room.ColumnInfo;

public class AcessoTotal {

    @ColumnInfo(name = "identificadorUnico")
    String identificadorUnico;

    @ColumnInfo(name = "versao")
    String versao;

    @ColumnInfo(name = "totalAcessos")
    Integer totalAcessos;

    public String getIdentificadorUnico() {
        return identificadorUnico;
    }

    public void setIdentificadorUnico(String identificadorUnico) {
        this.identificadorUnico = identificadorUnico;
    }

    public Integer getTotalAcessos() {
        return totalAcessos;
    }

    public void setTotalAcessos(Integer totalAcessos) {
        this.totalAcessos = totalAcessos;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

}
