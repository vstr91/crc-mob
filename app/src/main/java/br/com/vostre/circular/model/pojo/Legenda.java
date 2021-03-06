package br.com.vostre.circular.model.pojo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

import br.com.vostre.circular.model.Cidade;

public class Legenda {

    String itinerario;
    String texto;
    int cor;

    public String getItinerario() {
        return itinerario;
    }

    public void setItinerario(String itinerario) {
        this.itinerario = itinerario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getCor() {
        return cor;
    }

    public void setCor(int cor) {
        this.cor = cor;
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof Legenda)){
            return false;
        }

        Legenda legenda = (Legenda) obj;
        return legenda.getItinerario().equals(this.getItinerario());
    }
}
