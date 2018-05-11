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
import br.com.vostre.circular.databinding.FormParadaBinding;
import br.com.vostre.circular.databinding.FormPontoInteresseBinding;

public class FormPontoInteresse extends FormPOIBase {

    FormPontoInteresseBinding binding;
    Calendar dataInicio;
    Calendar dataFim;

    TextView textViewInicio;
    Button btnTrocarInicio;

    TextView textViewFim;
    Button btnTrocarFim;

    Double latitude;
    Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

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
                inflater, R.layout.form_ponto_interesse, container, false);
        super.onCreate(savedInstanceState);
        binding.setView(this);

        textViewInicio = binding.textViewInicio;
        btnTrocarInicio = binding.btnTrocarInicio;

        textViewFim = binding.textViewFim;
        btnTrocarFim = binding.btnTrocarFim;

        textViewInicio.setVisibility(View.GONE);
        btnTrocarInicio.setVisibility(View.GONE);

        textViewFim.setVisibility(View.GONE);
        btnTrocarFim.setVisibility(View.GONE);

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickTrocarInicio(View v){
        FormCalendarioPOI formCalendario = new FormCalendarioPOI();
        formCalendario.setParent(this);
        formCalendario.setDataAnterior(dataInicio);
        formCalendario.setQual(0);
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    public void onClickTrocarFim(View v){
        FormCalendarioPOI formCalendario = new FormCalendarioPOI();
        formCalendario.setParent(this);
        formCalendario.setDataAnterior(dataFim);
        formCalendario.setQual(1);
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    @Override
    public void setDataInicio(Calendar umaData) {
        this.dataInicio = umaData;
        exibeDataEscolhida(0);

    }

    @Override
    public void setDataFim(Calendar umaData) {
        this.dataFim = umaData;
        exibeDataEscolhida(1);

    }

    private void exibeDataEscolhida(Integer qual){

        if(qual == 0){
            textViewInicio.setText(DateFormat.getDateTimeInstance().format(dataInicio.getTime()));

            textViewInicio.setVisibility(View.VISIBLE);
            btnTrocarInicio.setVisibility(View.VISIBLE);
        } else{
            textViewFim.setText(DateFormat.getDateTimeInstance().format(dataFim.getTime()));

            textViewFim.setVisibility(View.VISIBLE);
            btnTrocarFim.setVisibility(View.VISIBLE);
        }


    }

}
