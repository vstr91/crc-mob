package br.com.vostre.circular.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

import org.joda.time.DateTime;

@Entity(tableName = "clima_cidade")
public class ClimaCidade extends EntidadeBase {

    @NonNull
    private String idClima;

    @NonNull
    private String clima;

    @NonNull
    private String icone;

    @NonNull
    private String descricao;

    @NonNull
    private Double temperatura;

    @NonNull
    private Double sensacao;

    @NonNull
    private Double tempMinima;

    @NonNull
    private Double tempMaxima;

    @NonNull
    private Double umidade;

    @NonNull
    private DateTime nascerDoSol;

    @NonNull
    private DateTime porDoSol;

    @NonNull
    private String cidade;

    @NonNull
    public String getIdClima() {
        return idClima;
    }

    public void setIdClima(@NonNull String idClima) {
        this.idClima = idClima;
    }

    @NonNull
    public String getClima() {
        return clima;
    }

    public void setClima(@NonNull String clima) {
        this.clima = clima;
    }

    @NonNull
    public String getIcone() {
        return icone;
    }

    public void setIcone(@NonNull String icone) {
        this.icone = icone;
    }

    @NonNull
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NonNull String descricao) {
        this.descricao = descricao;
    }

    @NonNull
    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(@NonNull Double temperatura) {
        this.temperatura = temperatura;
    }

    @NonNull
    public Double getSensacao() {
        return sensacao;
    }

    public void setSensacao(@NonNull Double sensacao) {
        this.sensacao = sensacao;
    }

    @NonNull
    public Double getTempMinima() {
        return tempMinima;
    }

    public void setTempMinima(@NonNull Double tempMinima) {
        this.tempMinima = tempMinima;
    }

    @NonNull
    public Double getTempMaxima() {
        return tempMaxima;
    }

    public void setTempMaxima(@NonNull Double tempMaxima) {
        this.tempMaxima = tempMaxima;
    }

    @NonNull
    public Double getUmidade() {
        return umidade;
    }

    public void setUmidade(@NonNull Double umidade) {
        this.umidade = umidade;
    }

    @NonNull
    public DateTime getNascerDoSol() {
        return nascerDoSol;
    }

    public void setNascerDoSol(@NonNull DateTime nascerDoSol) {
        this.nascerDoSol = nascerDoSol;
    }

    @NonNull
    public DateTime getPorDoSol() {
        return porDoSol;
    }

    public void setPorDoSol(@NonNull DateTime porDoSol) {
        this.porDoSol = porDoSol;
    }

    @NonNull
    public String getCidade() {
        return cidade;
    }

    public void setCidade(@NonNull String cidade) {
        this.cidade = cidade;
    }

    public boolean valida(ClimaCidade bairro) {

        if(super.valida(bairro) && bairro.getCidade() != null){
            return true;
        } else{
            return false;
        }

    }
}
