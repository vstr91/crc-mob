package br.com.vostre.circular.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Entity(tableName = "ponto_interesse")
public class PontoInteresse extends EntidadeSlug {

    private String descricao;

    @NonNull
    private Double latitude;

    @NonNull
    private Double longitude;

    private String imagem;

    @NonNull
    private DateTime dataInicial;

    private DateTime dataFinal;

    @NonNull
    private boolean imagemEnviada = true;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

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

    public DateTime getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(DateTime dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getDatas(){
        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/YYYY");
        return dtf.print(dataInicial)+" a "+dtf.print(dataFinal);
    }

    public boolean valida(PontoInteresse pontoInteresse) {

        if(super.valida(pontoInteresse) && pontoInteresse.getLatitude() != null && pontoInteresse.getLongitude() != null){
            return true;
        } else{
            return false;
        }

    }
}
