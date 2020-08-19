package br.com.vostre.circular.model.log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.annotation.NonNull;

import org.joda.time.DateTime;

import br.com.vostre.circular.model.EntidadeBase;

@Entity()
public class LogConsulta extends EntidadeBase {

    @NonNull
    @ColumnInfo(name = "data_inicio")
    private DateTime dataInicio;

    @NonNull
    @ColumnInfo(name = "data_fim")
    private DateTime dataFim;

    @NonNull
    @ColumnInfo(name = "tipo")
    private String tipo;

    @NonNull
    @ColumnInfo(name = "local")
    private String local;

    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @NonNull
    @ColumnInfo(name = "versao")
    private String versao;

    @NonNull
    public DateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(@NonNull DateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    @NonNull
    public DateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(@NonNull DateTime dataFim) {
        this.dataFim = dataFim;
    }

    @NonNull
    public String getTipo() {
        return tipo;
    }

    public void setTipo(@NonNull String tipo) {
        this.tipo = tipo;
    }

    @NonNull
    public String getLocal() {
        return local;
    }

    public void setLocal(@NonNull String local) {
        this.local = local;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    @NonNull
    public String getVersao() {
        return versao;
    }

    public void setVersao(@NonNull String versao) {
        this.versao = versao;
    }

    public LogConsulta(){
        this.setAtivo(true);
    }

    public boolean valida(LogConsulta log) {

        if(super.valida(log)){
            return true;
        } else{
            return false;
        }

    }

}
