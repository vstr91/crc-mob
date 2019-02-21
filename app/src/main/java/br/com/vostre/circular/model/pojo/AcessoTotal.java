package br.com.vostre.circular.model.pojo;

import android.arch.persistence.room.ColumnInfo;

public class AcessoTotal {

    @ColumnInfo(name = "identificadorUnico")
    String identificadorUnico;

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
}
