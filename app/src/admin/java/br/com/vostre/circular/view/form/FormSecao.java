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
import android.widget.Toast;

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

        viewModel.retorno.observe(this, retornoObserver);
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

        if(paradas != null && secao != null){

            ParadaBairro parada = new ParadaBairro();

            if(tipo == 0){
                ParadaAdapterSpinner adapter = new ParadaAdapterSpinner(ctx, R.layout.linha_paradas_spinner, R.id.textViewNome, paradas);
                binding.spinnerInicial.setAdapter(adapter);

                parada.getParada().setId(secao.getParadaInicial());
                int i = viewModel.paradasIniciais.getValue().indexOf(parada);

                if(i == -1){
                    i = 0;
                }

                binding.spinnerInicial.setSelection(i);

            } else{
                ParadaAdapterSpinner adapter = new ParadaAdapterSpinner(ctx, R.layout.linha_paradas_spinner, R.id.textViewNome, paradas);
                binding.spinnerFinal.setAdapter(adapter);

                parada.getParada().setId(secao.getParadaFinal());
                int i = viewModel.paradasFinais.getValue().indexOf(parada);

                if(i == -1){
                    i = 0;
                }

                binding.spinnerFinal.setSelection(i);
            }

        } else if(paradas != null){
            ParadaBairro parada = new ParadaBairro();

            if(tipo == 0){
                ParadaAdapterSpinner adapter = new ParadaAdapterSpinner(ctx, R.layout.linha_paradas_spinner, R.id.textViewNome, paradas);
                binding.spinnerInicial.setAdapter(adapter);

            } else{
                ParadaAdapterSpinner adapter = new ParadaAdapterSpinner(ctx, R.layout.linha_paradas_spinner, R.id.textViewNome, paradas);
                binding.spinnerFinal.setAdapter(adapter);
            }
        }

    }

    public void onItemSelectedSpinnerInicial (AdapterView<?> adapterView, View view, int i, long l){
        viewModel.setParadaInicial(viewModel.paradasIniciais.getValue().get(i));
        viewModel.paradasFinais.observe(thiz, paradasFinaisObserver);
    }

    public void onItemSelectedSpinnerFinal (AdapterView<?> adapterView, View view, int i, long l){

        if(viewModel.paradasFinais.getValue() != null){
            viewModel.setParadaFinal(viewModel.paradasFinais.getValue().get(i));
        }

    }

    Observer<List<ParadaBairro>> paradasIniciaisObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            setSpinnerEntries(binding.spinnerInicial, paradas, 0);
        }
    };

    Observer<List<ParadaBairro>> paradasFinaisObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            setSpinnerEntries(binding.spinnerInicial, paradas, 1);
        }
    };

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getContext().getApplicationContext(), "Seção cadastrada!", Toast.LENGTH_SHORT).show();
                viewModel.setSecao(new SecaoItinerario());
                dismiss();
            } else if(retorno == 0){
                Toast.makeText(getContext().getApplicationContext(),
                        "Dados necessários não informados. Por favor preencha " +
                                "todos os dados obrigatórios!",
                        Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getContext().getApplicationContext(),
                        "A parada inicial selecionada para a seção deve vir antes da parada final no trajeto do itinerário.",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

}
