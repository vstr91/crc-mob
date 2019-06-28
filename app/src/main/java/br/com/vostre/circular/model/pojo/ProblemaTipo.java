package br.com.vostre.circular.model.pojo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Problema;

public class ProblemaTipo {

    @Embedded
    Problema problema;

    @ColumnInfo(name = "tipo")
    String tipo;

    @ColumnInfo(name = "usuario")
    String usuario;

    public Problema getProblema() {
        return problema;
    }

    public void setProblema(Problema problema) {
        this.problema = problema;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

}
