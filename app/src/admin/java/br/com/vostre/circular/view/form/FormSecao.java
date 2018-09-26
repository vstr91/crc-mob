package br.com.vostre.circular.view.form;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormPaisBinding;
import br.com.vostre.circular.databinding.FormSecaoBinding;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.adapter.ParadaAdapterSpinner;
import br.com.vostre.circular.viewModel.SecoesItinerarioViewModel;

public class FormSecao extends FormBase {

    FormSecaoBinding binding;
    Calendar data;

    TextView textViewProgramado;
    Button btnTrocar;

    SecoesItinerarioViewModel viewModel;

    SecaoItinerario secao;
    public Boolean flagInicioEdicao;

    static Application ctx;
    FormSecao thiz;

    public Application getCtx() {
        return ctx;
    }

    public void setCtx(Application ctx) {
        this.ctx = ctx;
    }

    public SecaoItinerario getSecao() {
        return secao;
    }

    public void setSecao(SecaoItinerario secao) {
        this.secao = secao;
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
                inflater, R.layout.form_secao, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(SecoesItinerarioViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        thiz = this;

        if(secao != null){
            viewModel.secao = secao;
            flagInicioEdicao = true;
        }

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        if(viewModel.secao.getProgramadoPara() == null){
            textViewProgramado.setVisibility(View.GONE);
            btnTrocar.setVisibility(View.GONE);
        } else{
            exibeDataEscolhida();
        }

        viewModel.paradasIniciais.observe(this, paradasIniciaisObserver);

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        if(secao != null){
            viewModel.editarSecao();
        } else{
            viewModel.salvarSecao();
        }

        dismiss();
    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickTrocar(View v){
        FormCalendario formCalendario = new FormCalendario();
        formCalendario.setParent(this);
        formCalendario.setDataAnterior(viewModel.secao.getProgramadoPara().toCalendar(null));
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    public void onSwitchProgramadoChange(CompoundButton btn, boolean ativo){

        if(flagInicioEdicao && secao.getProgramadoPara() != null){
            flagInicioEdicao = false;
            return;
        }

        if(ativo){
            FormCalendario formCalendario = new FormCalendario();
            formCalendario.setParent(this);

            if(viewModel.secao.getProgramadoPara() != null){
                formCalendario.setDataAnterior(viewModel.secao.getProgramadoPara().toCalendar(null));
            }

            formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
        } else{
            ocultaDataEscolhida();
        }

    }

    @Override
    public void setData(Calendar umaData) {
        viewModel.secao.setProgramadoPara(new DateTime(umaData,
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))));

        if(viewModel.secao.getProgramadoPara() == null){
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
        viewModel.secao.setProgramadoPara(null);
    }

    private void exibeDataEscolhida(){

        textViewProgramado.setText(DateTimeFormat
                .forPattern("dd/MM/yy HH:mm").print(viewModel.secao.getProgramadoPara()));

        textViewProgramado.setVisibility(View.VISIBLE);
        btnTrocar.setVisibility(View.VISIBLE);
    }

    @BindingAdapter("entries")
    public static void setSpinnerEntries(Spinner spinner, LiveData<List<ParadaBairro>> paradas){

        if(paradas.getValue() != null){
            ParadaAdapterSpinner adapter = new ParadaAdapterSpinner(ctx, R.layout.linha_paradas_spinner, R.id.textViewNome, paradas.getValue());
            spinner.setAdapter(adapter);
        }

    }

    public void setSpinnerEntries(Spinner spinner, List<ParadaBairro> paradas, int tipo){

        if(paradas != null){
            ParadaAdapterSpinner adapter = new ParadaAdapterSpinner(ctx, R.layout.linha_paradas_spinner, R.id.textViewNome, paradas);
            spinner.setAdapter(adapter);

            if(secao != null){
                ParadaBairro parada = new ParadaBairro();

                if(tipo == 0){
                    parada.getParada().setId(secao.getParadaInicial());
                    int i = viewModel.paradasIniciais.getValue().indexOf(parada);
                    binding.spinnerInicial.setSelection(i);
                } else{
                    parada.getParada().setId(secao.getParadaFinal());
                    int i = viewModel.paradasFinais.getValue().indexOf(parada);
                    binding.spinnerFinal.setSelection(i);
                }


            }

        }

    }

    public void onItemSelectedSpinnerInicial (AdapterView<?> adapterView, View view, int i, long l){
        viewModel.setParadaInicial(viewModel.paradasIniciais.getValue().get(i));
    }

    public void onItemSelectedSpinnerFinal (AdapterView<?> adapterView, View view, int i, long l){
        viewModel.setParadaInicial(viewModel.paradasIniciais.getValue().get(i));
    }

    Observer<List<ParadaBairro>> paradasIniciaisObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            setSpinnerEntries(binding.spinnerInicial, paradas, 0);
            viewModel.paradasFinais.observe(thiz, paradasFinaisObserver);
        }
    };

    Observer<List<ParadaBairro>> paradasFinaisObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            setSpinnerEntries(binding.spinnerInicial, paradas, 1);
        }
    };

}
