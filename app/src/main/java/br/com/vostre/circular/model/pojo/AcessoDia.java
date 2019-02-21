package br.com.vostre.circular.model.pojo;

import android.arch.persistence.room.ColumnInfo;

import org.joda.time.DateTime;

public class AcessoDia {

    @ColumnInfo(name = "dia")
    String dia;

    @ColumnInfo(name = "totalAcessos")
    Integer totalAcessos;

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public Integer getTotalAcessos() {
        return totalAcessos;
    }

    public void setTotalAcessos(Integer totalAcessos) {
        this.totalAcessos = totalAcessos;
    }
}
