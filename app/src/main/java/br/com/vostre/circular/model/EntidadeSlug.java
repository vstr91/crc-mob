package br.com.vostre.circular.model;

import android.support.annotation.NonNull;

public class EntidadeSlug extends EntidadeBase {

    @NonNull
    private String nome;

    @NonNull
    private String slug;

    public EntidadeSlug(){
        this.setAtivo(true);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean valida(EntidadeSlug entidade){

        if(super.valida(entidade) && entidade.getNome() != null && !entidade.getNome().isEmpty()){
            return true;
        } else{
            return false;
        }

    }

}
