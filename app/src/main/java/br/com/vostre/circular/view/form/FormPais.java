package br.com.vostre.circular.view.form;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import br.com.vostre.circular.R;

public class FormPais extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.form_pais, container, false);

        if(this.getDialog() != null){
            this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        return view;

    }

    public void onClickSalvar(View v){

    }

    public void onClickFechar(View v){
        dismiss();
    }

}
