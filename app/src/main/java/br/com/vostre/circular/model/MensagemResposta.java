package br.com.vostre.circular.model;

import androidx.room.Entity;
import androidx.annotation.NonNull;

@Entity(tableName = "mensagem_resposta")
public class MensagemResposta extends EntidadeBase {

    @NonNull
    private String resposta;

    @NonNull
    private String mensagem;

    @NonNull
    public String getResposta() {
        return resposta;
    }

    public void setResposta(@NonNull String resposta) {
        this.resposta = resposta;
    }

    @NonNull
    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(@NonNull String mensagem) {
        this.mensagem = mensagem;
    }

    public boolean valida(MensagemResposta mensagem) {

        if(super.valida(mensagem) && mensagem.getResposta() != null && mensagem.getMensagem() != null){
            return true;
        } else{
            return false;
        }

    }

}
