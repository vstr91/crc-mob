package br.com.vostre.circular.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "imagem_parada")
public class ImagemParada extends EntidadeBase {

    @NonNull
    private String parada;

    @NonNull
    private String imagem;

    private String descricao;

    private int status = 0; // 0 - pendente / 1 - ativo / 2 - rejeitado/inativo

    @NonNull
    private boolean imagemEnviada = true;

    public boolean isImagemEnviada() {
        return imagemEnviada;
    }

    public void setImagemEnviada(boolean imagemEnviada) {
        this.imagemEnviada = imagemEnviada;
    }

    @NonNull
    public String getImagem() {
        return imagem;
    }

    public void setImagem(@NonNull String imagem) {
        this.imagem = imagem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @NonNull
    public String getParada() {
        return parada;
    }

    public void setParada(@NonNull String parada) {
        this.parada = parada;
    }

    public boolean valida(ImagemParada imagemParada) {

        if(super.valida(imagemParada) && imagemParada.getParada() != null
                && imagemParada.getImagem() != null){
            return true;
        } else{
            return false;
        }

    }
}
