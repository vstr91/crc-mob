package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity
public class Mensagem extends EntidadeBase {

    @NonNull
    private String titulo;

    private String resumo;

    @NonNull
    private String descricao;

    @NonNull
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(@NonNull String titulo) {
        this.titulo = titulo;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    @NonNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NonNull String descricao) {
        this.descricao = descricao;
    }

    public boolean valida(Mensagem mensagem) {

        if(super.valida(mensagem) && mensagem.getTitulo() != null && mensagem.getDescricao() != null){
            return true;
        } else{
            return false;
        }

    }

}
