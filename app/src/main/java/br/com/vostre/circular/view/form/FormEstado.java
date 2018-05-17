package br.com.vostre.circular.view.form;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormEstadoBinding;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.adapter.PaisAdapter;
import br.com.vostre.circular.view.adapter.PaisAdapterSpinner;
import br.com.vostre.circular.viewModel.EstadosViewModel;

public class FormEstado extends FormBase {

    FormEstadoBinding binding;
    Calendar data;

    TextView textViewProgramado;
    Button btnTrocar;

    EstadosViewModel viewModel;

    Estado estado;
    Boolean flagInicioEdicao;
    PaisAdapterSpinner adapter;

    static Application ctx;

    public Application getCtx() {
        return ctx;
    }

    public void setCtx(Application ctx) {
        this.ctx = ctx;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public PaisAdapterSpinner getAdapter() {
        return adapter;
    }

    public void setAdapter(PaisAdapterSpinner adapter) {
        this.adapter = adapter;
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
                inflater, R.layout.form_estado, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(EstadosViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(estado != null){
            viewModel.estado = estado;
            flagInicioEdicao = true;
        }

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        if(viewModel.estado.getProgramadoPara() == null){
            textViewProgramado.setVisibility(View.GONE);
            btnTrocar.setVisibility(View.GONE);
        } else{
            exibeDataEscolhida();
        }

        viewModel.paises.observe(this, paisesObserver);

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        if(estado != null){
            viewModel.editarEstado();
        } else{
            viewModel.salvarEstado();
        }

        dismiss();
    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickTrocar(View v){
        FormCalendario formCalendario = new FormCalendario();
        formCalendario.setParent(this);
        formCalendario.setDataAnterior(viewModel.estado.getProgramadoPara().toCalendar(null));
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    public void onSwitchProgramadoChange(CompoundButton btn, boolean ativo){

        if(flagInicioEdicao){
            flagInicioEdicao = false;
            return;
        }

        if(ativo){
            FormCalendario formCalendario = new FormCalendario();
            formCalendario.setParent(this);

            if(viewModel.estado.getProgramadoPara() != null){
                formCalendario.setDataAnterior(viewModel.estado.getProgramadoPara().toCalendar(null));
            }

            formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
        } else{
            ocultaDataEscolhida();
        }

    }

    @Override
    public void setData(Calendar umaData) {
        viewModel.estado.setProgramadoPara(new DateTime(umaData,
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))));

        if(viewModel.estado.getProgramadoPara() == null){
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
        viewModel.estado.setProgramadoPara(null);
    }

    private void exibeDataEscolhida(){

        textViewProgramado.setText(DateTimeFormat
                .forPattern("dd/MM/yy HH:mm").print(viewModel.estado.getProgramadoPara()));

        textViewProgramado.setVisibility(View.VISIBLE);
        btnTrocar.setVisibility(View.VISIBLE);
    }

    @BindingAdapter("entries")
    public static void setSpinnerEntries(Spinner spinner, LiveData<List<Pais>> paises){

        if(paises.getValue() != null){
            PaisAdapterSpinner adapter = new PaisAdapterSpinner(ctx, R.layout.linha_paises_spinner, R.id.textViewNome, paises.getValue());
            spinner.setAdapter(adapter);
        }

    }

    public void setSpinnerEntries(Spinner spinner, List<Pais> paises){

        if(paises != null){
            PaisAdapterSpinner adapter = new PaisAdapterSpinner(ctx, R.layout.linha_paises_spinner, R.id.textViewNome, paises);
            spinner.setAdapter(adapter);
            //viewModel.carregaPais(estado);
            //spinner.setSelection(viewModel.paises.getValue().indexOf(viewModel.pais));
        }

    }

    public void onItemSelectedSpinnerPais(AdapterView<?> adapterView, View view, int i, long l){
        viewModel.pais = viewModel.paises.getValue().get(i);
    }

    Observer<List<Pais>> paisesObserver = new Observer<List<Pais>>() {
        @Override
        public void onChanged(List<Pais> paises) {
            setSpinnerEntries(binding.spinnerPais, paises);
        }
    };

}
