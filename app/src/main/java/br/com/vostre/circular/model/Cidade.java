package br.com.vostre.circular.model;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(indices = {@Index(value = {"nome", "estado"},
        unique = true)})
public class Cidade extends EntidadeSlug {

    private String brasao;

    @NonNull
    private String estado;

    @NonNull
    private boolean imagemEnviada = true;

    @Nullable
    @ColumnInfo(name = "id_clima")
    private Integer idClima;

    @Nullable
    private Double latitude;

    @Nullable
    private Double longitude;

    @Nullable
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(@Nullable Double latitude) {
        this.latitude = latitude;
    }

    @Nullable
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(@Nullable Double longitude) {
        this.longitude = longitude;
    }

    public Integer getIdClima() {
        return idClima;
    }

    public void setIdClima(Integer idClima) {
        this.idClima = idClima;
    }

    @NonNull
    public boolean isImagemEnviada() {
        return imagemEnviada;
    }

    public void setImagemEnviada(@NonNull boolean imagemEnviada) {
        this.imagemEnviada = imagemEnviada;
    }

    public String getBrasao() {
        return brasao;
    }

    public void setBrasao(String brasao) {
        this.brasao = brasao;
    }

    @NonNull
    public String getEstado() {
        return estado;
    }

    public void setEstado(@NonNull String estado) {
        this.estado = estado;
    }

    public boolean valida(Cidade cidade) {

        if(super.valida(cidade) && cidade.getEstado() != null){
            return true;
        } else{
            return false;
        }

    }

}
