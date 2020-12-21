package br.com.vostre.circular.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "feedback_itinerario")
public class FeedbackItinerario extends Feedback {

    @NonNull
    private String itinerario;

    public String getItinerario() {
        return itinerario;
    }

    public void setItinerario(String itinerario) {
        this.itinerario = itinerario;
    }

    public boolean valida(FeedbackItinerario feedback) {

        if(super.valida(feedback) && feedback.itinerario != null){
            return true;
        } else{
            return false;
        }

    }
}
