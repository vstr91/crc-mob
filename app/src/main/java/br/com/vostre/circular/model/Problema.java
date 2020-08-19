package br.com.vostre.circular.model;

import androidx.room.Entity;
import androidx.annotation.NonNull;

@Entity
public class Problema extends EntidadeBase {

  @NonNull
  private String descricao;

  @NonNull
  private String tipoProblema;

  @NonNull
  private Boolean lida = false;

  private String imagem;

  @NonNull
  private boolean imagemEnviada = true;

  @NonNull
  private Integer situacao = 0; // 0 = aberto | 1 = resolvido | 2 = nao procede

  @NonNull
  public Boolean getLida() {
    return lida;
  }

  public void setLida(@NonNull Boolean lida) {
    this.lida = lida;
  }

  @NonNull
  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(@NonNull String descricao) {
    this.descricao = descricao;
  }

  @NonNull
  public String getTipoProblema() {
    return tipoProblema;
  }

  public void setTipoProblema(@NonNull String tipoProblema) {
    this.tipoProblema = tipoProblema;
  }

  public String getImagem() {
    return imagem;
  }

  public void setImagem(String imagem) {
    this.imagem = imagem;
  }

  public boolean isImagemEnviada() {
    return imagemEnviada;
  }

  public void setImagemEnviada(boolean imagemEnviada) {
    this.imagemEnviada = imagemEnviada;
  }

  @NonNull
  public Integer getSituacao() {
    return situacao;
  }

  public void setSituacao(@NonNull Integer situacao) {
    this.situacao = situacao;
  }

  public boolean valida(Problema mensagem) {

    if(super.valida(mensagem) && mensagem.getDescricao() != null){
      return true;
    } else{
      return false;
    }

  }

}
