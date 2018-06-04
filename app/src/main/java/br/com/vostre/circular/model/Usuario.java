package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(indices = {@Index(value = {"email"},
        unique = true)})
public class Usuario extends EntidadeBase {

    @NonNull
    private String nome;

    @NonNull
    private String email;

    private String idGoogle;

    @NonNull
    public String getNome() {
        return nome;
    }

    public void setNome(@NonNull String nome) {
        this.nome = nome;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public String getIdGoogle() {
        return idGoogle;
    }

    public void setIdGoogle(String idGoogle) {
        this.idGoogle = idGoogle;
    }

    public boolean valida(Usuario usuario) {

        if(super.valida(usuario) && usuario.getNome() != null && usuario.getEmail() != null){
            return true;
        } else{
            return false;
        }

    }
}
