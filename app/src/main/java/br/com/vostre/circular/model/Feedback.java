package br.com.vostre.circular.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

import java.util.List;

public class Feedback extends EntidadeBase {

    @NonNull
    private String descricao;

    private String imagem;

    @NonNull
    private boolean imagemEnviada = true;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    @NonNull
    public boolean isImagemEnviada() {
        return imagemEnviada;
    }

    public void setImagemEnviada(@NonNull boolean imagemEnviada) {
        this.imagemEnviada = imagemEnviada;
    }

    public boolean valida(Feedback feedback) {

        if(super.valida(feedback) && feedback.descricao != null && !feedback.descricao.trim().isEmpty()){
            return true;
        } else{
            return false;
        }

    }
}
