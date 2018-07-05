package br.com.vostre.circular.view.form;

import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class FormBase extends DialogFragment {

    Calendar data;

    public Calendar getData() {
        return data;
    }

    public void setData(Calendar data) {
        this.data = data;
    }

}
