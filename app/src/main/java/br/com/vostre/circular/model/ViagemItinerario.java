package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.bonuspack.kml.KmlDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(indices = {@Index(value = {"itinerario", "trajeto"},
        unique = true)}, tableName = "viagem_itinerario")
public class ViagemItinerario extends EntidadeBase {

    @NonNull
    private String itinerario;

    @NonNull
    private String trajeto;

    private DateTime horaInicial;

    private DateTime horaFinal;

    @NonNull
    private boolean trajetoEnviado = false;

    public boolean isTrajetoEnviado() {
        return trajetoEnviado;
    }

    public void setTrajetoEnviado(boolean trajetoEnviado) {
        this.trajetoEnviado = trajetoEnviado;
    }

    @NonNull
    public String getItinerario() {
        return itinerario;
    }

    public void setItinerario(@NonNull String itinerario) {
        this.itinerario = itinerario;
    }

    @NonNull
    public String getTrajeto() {
        return trajeto;
    }

    public List<Location> getTrajetoLista(Context ctx){
        return new ArrayList<>();
    }

    public int getTotalPontos(Context ctx){
        String jsonString = null;

        try {
            InputStream jsonStream = new FileInputStream(new File(ctx.getFilesDir(), getTrajeto()));
            int size = jsonStream.available();
            byte[] buffer = new byte[size];
            jsonStream.read(buffer);
            jsonStream.close();
            jsonString = new String(buffer,"UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        KmlDocument kmlDocument = new KmlDocument();
        kmlDocument.parseGeoJSON(jsonString);

        return kmlDocument.mKmlRoot.mItems.size();

    }

    public String getHorarioTotal(){

        if(horaInicial != null && horaFinal != null){
            return String.valueOf(Minutes.minutesBetween(horaInicial, horaFinal).getMinutes());
        } else{
            return "-";
        }

    }

    public void setTrajeto(@NonNull String trajeto) {
        this.trajeto = trajeto;
    }

    public DateTime getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(DateTime horaInicial) {
        this.horaInicial = horaInicial;
    }

    public DateTime getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(DateTime horaFinal) {
        this.horaFinal = horaFinal;
    }

    public boolean valida(ViagemItinerario historicoItinerario) {

        if(super.valida(historicoItinerario) && historicoItinerario.getItinerario() != null
                && historicoItinerario.getTrajeto() != null){
            return true;
        } else{
            return false;
        }

    }
}
