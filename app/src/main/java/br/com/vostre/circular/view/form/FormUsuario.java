package br.com.vostre.circular.view.form;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormUsuarioBinding;

public class FormUsuario extends FormBase {

    FormUsuarioBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.form_pais, container, false);
//
//        if(this.getDialog() != null){
//            this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        }
//
//        return view;

        binding = DataBindingUtil.inflate(
                inflater, R.layout.form_usuario, container, false);
        super.onCreate(savedInstanceState);
        binding.setView(this);

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

    }

    public void onClickFechar(View v){
        dismiss();
    }

}
