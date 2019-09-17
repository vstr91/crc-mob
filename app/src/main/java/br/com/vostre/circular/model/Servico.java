package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"nome"},
        unique = true)})
public class Servico extends EntidadeSlug {

    @NonNull
    private String icone;

    @NonNull
    private boolean imagemEnviada = true;

    @Ignore
    private boolean selecionado = false;

    public Servico(){
        this.setAtivo(true);
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    @NonNull
    public String getIcone() {
        return icone;
    }

    public void setIcone(@NonNull String icone) {
        this.icone = icone;
    }

    public boolean isImagemEnviada() {
        return imagemEnviada;
    }

    public void setImagemEnviada(boolean imagemEnviada) {
        this.imagemEnviada = imagemEnviada;
    }

    public boolean valida(Servico servico) {

        if(super.valida(servico) && servico.getIcone() != null && !servico.getIcone().isEmpty()){
            return true;
        } else{
            return false;
        }

    }

}
