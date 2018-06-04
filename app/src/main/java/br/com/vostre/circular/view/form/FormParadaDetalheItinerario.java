package br.com.vostre.circular.view.form;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormParadaDetalheItinerarioBinding;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;

public class FormParadaDetalheItinerario extends FormBase {

    FormParadaDetalheItinerarioBinding binding;
    Calendar data;

    TextView textViewProgramado;
    Button btnTrocar;

    DetalhesItinerarioViewModel viewModel;

    ParadaItinerarioBairro parada;
    public Boolean flagInicioEdicao;
    public Boolean edicaoItinerario = false;

    Application ctx;

    public Application getCtx() {
        return ctx;
    }

    public void setCtx(Application ctx) {
        this.ctx = ctx;
    }

    public ParadaItinerarioBairro getParada() {
        return parada;
    }

    public void setParada(ParadaItinerarioBairro paradaItinerarioBairro) {
        this.parada = paradaItinerarioBairro;
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
                inflater, R.layout.form_parada_detalhe_itinerario, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(DetalhesItinerarioViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(parada != null){
            viewModel.parada = parada;
            flagInicioEdicao = true;
        }

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        if(viewModel.parada.getParadaItinerario().getProgramadoPara() == null){
            textViewProgramado.setVisibility(View.GONE);
            btnTrocar.setVisibility(View.GONE);
        } else{
            exibeDataEscolhida();
        }

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        if(parada != null){
            viewModel.editarParada();
        } else{
            viewModel.salvarParada();
        }

        dismiss();
    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickTrocar(View v){
        FormCalendario formCalendario = new FormCalendario();
        formCalendario.setParent(this);
        formCalendario.setDataAnterior(viewModel.parada.getParadaItinerario().getProgramadoPara().toCalendar(null));
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    public void onSwitchProgramadoChange(CompoundButton btn, boolean ativo){

        if(flagInicioEdicao && parada.getParadaItinerario().getProgramadoPara() != null){
            flagInicioEdicao = false;
            return;
        }

        if(ativo){
            FormCalendario formCalendario = new FormCalendario();
            formCalendario.setParent(this);

            if(viewModel.parada.getParadaItinerario().getProgramadoPara() != null){
                formCalendario.setDataAnterior(viewModel.parada.getParadaItinerario().getProgramadoPara().toCalendar(null));
            }

            formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
        } else{
            ocultaDataEscolhida();
        }

    }

    @Override
    public void setData(Calendar umaData) {
        viewModel.parada.getParadaItinerario().setProgramadoPara(new DateTime(umaData,
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))));

        if(viewModel.parada.getParadaItinerario().getProgramadoPara() == null){
            ocultaDataEscolhida();
        } else{
            exibeDataEscolhida();
        }

    }

    private void ocultaDataEscolhida(){
        binding.switchProgramado.setChecked(false);
        textViewProgramado.setVisibility(View.GONE);
        textViewProgramado.setText("");
        btnTrocar.setVisibility(View.GONE);
        viewModel.parada.getParadaItinerario().setProgramadoPara(null);
    }

    private void exibeDataEscolhida(){

        textViewProgramado.setText(DateTimeFormat
                .forPattern("dd/MM/yy HH:mm").print(viewModel.parada.getParadaItinerario().getProgramadoPara()));

        textViewProgramado.setVisibility(View.VISIBLE);
        btnTrocar.setVisibility(View.VISIBLE);
    }

    @BindingAdapter("android:text")
    public static void setText(TextView view, Double valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();

        if(valor != null){
            view.setText(nf.format(valor));
        }

    }

    @InverseBindingAdapter(attribute = "android:text")
    public static Double getText(TextView view) {

        if(view.getText().toString().equals("null") || view.getText().toString().equals("")){
            return 0.0;
        } else{

            try{
                String valor = view.getText().toString();
                valor = valor.replace(NumberFormat.getCurrencyInstance().getCurrency().getSymbol(), "");
                valor = valor.replace(".", "");
                valor = valor.replace(",", ".");
                Double d = Double.parseDouble(valor);
                return d;
            } catch(NumberFormatException e){
                return 0.0;
            }

        }


    }

}
