package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"nome"},
        unique = true)})
public class Empresa extends EntidadeSlug {

    private String logo;

    private String email;

    private String telefone;

    @NonNull
    private boolean imagemEnviada = true;

    @NonNull
    public boolean isImagemEnviada() {
        return imagemEnviada;
    }

    public void setImagemEnviada(@NonNull boolean imagemEnviada) {
        this.imagemEnviada = imagemEnviada;
    }

    @NonNull
    public String getLogo() {
        return logo;
    }

    public void setLogo(@NonNull String logo) {
        this.logo = logo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean valida(Empresa empresa) {

        if(super.valida(empresa)){
            return true;
        } else{
            return false;
        }

    }

}
