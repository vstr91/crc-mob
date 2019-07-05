package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

@Entity(indices = {@Index(value = {"nome"},
        unique = true)})
public class Horario extends EntidadeBase {

    @NonNull
    private DateTime nome;

    public Horario(){
        this.setAtivo(true);
    }

    @NonNull
    public DateTime getNome() {
        return nome;
    }

    public void setNome(@NonNull DateTime nome) {
        this.nome = nome;
    }

    public boolean valida(Horario horario) {

        if(super.valida(horario) && horario.getNome() != null){
            return true;
        } else{
            return false;
        }

    }

    @Override
    public boolean equals(Object o) {
        Horario h = (Horario) o;
        return DateTimeFormat.forPattern("HH:mm").print(this.getNome()).equals(DateTimeFormat.forPattern("HH:mm").print(h.getNome()));
    }
}
