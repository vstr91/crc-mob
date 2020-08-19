package br.com.vostre.circular.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

@Entity
public class Onibus extends EntidadeBase {

    @NonNull
    private String sufixo;

    @NonNull
    private String placa;

    private Integer ano;

    private String marca;

    private String modelo;

    @NonNull
    private Boolean acessivel;

    @NonNull
    private String empresa;

    private String observacao;

    @Ignore
    private String descricaoCompleta;

    public Onibus(){
        this.acessivel = false;
    }

    @NonNull
    public String getSufixo() {
        return sufixo;
    }

    public void setSufixo(@NonNull String sufixo) {
        this.sufixo = sufixo;
    }

    @NonNull
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(@NonNull String placa) {
        this.placa = placa;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    @NonNull
    public Boolean getAcessivel() {
        return acessivel;
    }

    public void setAcessivel(@NonNull Boolean acessivel) {
        this.acessivel = acessivel;
    }

    @NonNull
    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(@NonNull String empresa) {
        this.empresa = empresa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getDescricaoCompleta() {
        return this.sufixo+" - "+this.marca+" "+this.modelo+" - Ano "+this.ano;
    }

    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = descricaoCompleta;
    }

    public boolean valida(Onibus onibus) {

        if(super.valida(onibus) && onibus.getAcessivel() != null && onibus.getSufixo() != null
                && onibus.getPlaca() != null && onibus.getEmpresa() != null){
            return true;
        } else{
            return false;
        }

    }
}
