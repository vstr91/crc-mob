package br.com.vostre.circular.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.InverseMethod;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.UUID;

public class EntidadeBase {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private Boolean ativo;

    @NonNull
    private Boolean enviado;

    @NonNull
    @ColumnInfo(name = "data_cadastro")
    private DateTime dataCadastro;

    @ColumnInfo(name = "usuario_cadastro")
    private String usuarioCadastro;

    @NonNull
    @ColumnInfo(name = "ultima_alteracao")
    private DateTime ultimaAlteracao;

    @ColumnInfo(name = "usuario_ultima_alteracao")
    private String usuarioUltimaAlteracao;

    @ColumnInfo(name = "programado_para")
    private DateTime programadoPara;

    public EntidadeBase(){
        this.setId(UUID.randomUUID().toString());
        this.setAtivo(true);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getEnviado() {
        return enviado;
    }

    public void setEnviado(Boolean enviado) {
        this.enviado = enviado;
    }

    public DateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(DateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public DateTime getUltimaAlteracao() {
        return ultimaAlteracao;
    }

    public void setUltimaAlteracao(DateTime ultimaAlteracao) {
        this.ultimaAlteracao = ultimaAlteracao;
    }

    public String getUsuarioUltimaAlteracao() {
        return usuarioUltimaAlteracao;
    }

    public void setUsuarioUltimaAlteracao(String usuarioUltimaAlteracao) {
        this.usuarioUltimaAlteracao = usuarioUltimaAlteracao;
    }

    public DateTime getProgramadoPara() {
        return programadoPara;
    }

    public void setProgramadoPara(DateTime programadoPara) {
        this.programadoPara = programadoPara;
    }

    public boolean valida(EntidadeBase entidadeBase){

        if(entidadeBase.getAtivo() != null){
            return true;
        } else{
            return false;
        }

    }

//    @InverseMethod(value = "setProgramadoParaString")
//    public String getProgramadoParaString(){
//
//        if(this.getProgramadoPara() != null){
//            return DateTimeFormat.forPattern("dd/MM/yyyy HH:mm").print(this.getProgramadoPara());
//        } else{
//            return null;
//        }
//
//    }
//
//    public DateTime setProgramadoParaString(String data){
//        return DateTime.parse(data);
//    }

}
