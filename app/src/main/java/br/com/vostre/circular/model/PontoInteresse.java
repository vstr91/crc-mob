package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

@Entity(tableName = "ponto_interesse")
public class PontoInteresse extends EntidadeSlug {

    @NonNull
    private Double latitude;

    @NonNull
    private Double longitude;

    private String imagem;

    @NonNull
    private DateTime dataInicial;

    @NonNull
    private DateTime dataFinal;

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
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@NonNull Double latitude) {
        this.latitude = latitude;
    }

    @NonNull
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(@NonNull Double longitude) {
        this.longitude = longitude;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    @NonNull
    public DateTime getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(@NonNull DateTime dataInicial) {
        this.dataInicial = dataInicial;
    }

    @NonNull
    public DateTime getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(@NonNull DateTime dataFinal) {
        this.dataFinal = dataFinal;
    }

    public boolean valida(PontoInteresse pontoInteresse) {

        if(super.valida(pontoInteresse) && pontoInteresse.getLatitude() != null && pontoInteresse.getLongitude() != null){
            return true;
        } else{
            return false;
        }

    }
}
