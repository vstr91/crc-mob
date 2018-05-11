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
import br.com.vostre.circular.databinding.FormItinerarioBinding;
import br.com.vostre.circular.databinding.FormParadaBinding;

public class FormItinerario extends FormBase {

    FormItinerarioBinding binding;
    Calendar data;

    TextView textViewProgramado;
    Button btnTrocar;

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
                inflater, R.layout.form_itinerario, container, false);
        super.onCreate(savedInstanceState);
        binding.setView(this);

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        textViewProgramado.setVisibility(View.GONE);
        btnTrocar.setVisibility(View.GONE);

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickTrocar(View v){
        FormCalendario formCalendario = new FormCalendario();
        formCalendario.setParent(this);
        formCalendario.setDataAnterior(data);
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    public void onSwitchProgramadoChange(CompoundButton btn, boolean ativo){

        if(ativo){
            FormCalendario formCalendario = new FormCalendario();
            formCalendario.setParent(this);
            formCalendario.setDataAnterior(data);
            formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
        } else{
            ocultaDataEscolhida();
        }

    }

    @Override
    public void setData(Calendar umaData) {
        this.data = umaData;

        if(data == null){
            ocultaDataEscolhida();
        } else{
            exibeDataEscolhida();
        }

    }

    private void ocultaDataEscolhida(){
        binding.switchProgramado.setChecked(false);
        textViewProgramado.setVisibility(View.GONE);
        btnTrocar.setVisibility(View.GONE);
        data = null;
    }

    private void exibeDataEscolhida(){

        textViewProgramado.setText(DateFormat.getDateTimeInstance().format(data.getTime()));

        textViewProgramado.setVisibility(View.VISIBLE);
        btnTrocar.setVisibility(View.VISIBLE);
    }

}
