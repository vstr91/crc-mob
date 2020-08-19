package br.com.vostre.circular.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

@Entity
public class Feriado extends EntidadeSlug {

    @NonNull
    private DateTime data;

    private String cidade;

    private int tipo; // 1 = feriado nacional | 2 = feriado estadual | 3 = feriado municipal | 4 = facultativo | 9 = dia convencional

    private String descricao;

    public String getDataCompleta(){
        return DateTimeFormat.forPattern("dd/MM").print(this.getData());
    }

    public String getAno(){
        return DateTimeFormat.forPattern("yyyy").print(this.getData());
    }

    @NonNull
    public DateTime getData() {
        return data;
    }

    public void setData(@NonNull DateTime data) {
        this.data = data;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean valida(Feriado feriado) {

        if(super.valida(feriado)){
            return true;
        } else{
            return false;
        }

    }
}
