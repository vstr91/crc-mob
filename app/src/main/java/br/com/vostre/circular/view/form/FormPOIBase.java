package br.com.vostre.circular.view.form;

import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class FormPOIBase extends FormBase {

    Calendar dataInicio;
    Calendar dataFim;

    public Calendar getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Calendar dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Calendar getDataFim() {
        return dataFim;
    }

    public void setDataFim(Calendar dataFim) {
        this.dataFim = dataFim;
    }

}
