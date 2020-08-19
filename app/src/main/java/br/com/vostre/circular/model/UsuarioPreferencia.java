package br.com.vostre.circular.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(indices = {@Index(value = {"usuario"},
        unique = true)}, tableName = "usuario_preferencia")
public class UsuarioPreferencia extends EntidadeBase {

    @NonNull
    private String usuario;

    @NonNull
    private String preferencia;

    public UsuarioPreferencia(){
        this.setAtivo(true);
    }

    @NonNull
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(@NonNull String usuario) {
        this.usuario = usuario;
    }

    @NonNull
    public String getPreferencia() {
        return preferencia;
    }

    public void setPreferencia(@NonNull String preferencia) {
        this.preferencia = preferencia;
    }

    public boolean valida(UsuarioPreferencia usuarioPreferencia) {

        if(super.valida(usuarioPreferencia) && !usuarioPreferencia.getUsuario().isEmpty() && !usuarioPreferencia.getPreferencia().isEmpty()){
            return true;
        } else{
            return false;
        }

    }

}
