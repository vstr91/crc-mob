package br.com.vostre.circular.view.form;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormItinerarioBinding;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.view.adapter.EmpresaAdapterSpinner;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;

public class FormItinerario extends FormBase {

    FormItinerarioBinding binding;
    Calendar data;

    TextView textViewProgramado;
    Button btnTrocar;

    ItinerariosViewModel viewModel;
    Itinerario itinerario;
    public Boolean flagInicioEdicao;

    EmpresaAdapterSpinner adapter;

    static Application ctx;
    int viacaoSelecionada = -1;

    public static Application getCtx() {
        return ctx;
    }

    public static void setCtx(Application ctx) {
        FormItinerario.ctx = ctx;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
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
                inflater, R.layout.form_itinerario, container, false);
        super.onCreate(savedInstanceState);

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        textViewProgramado.setVisibility(View.GONE);
        btnTrocar.setVisibility(View.GONE);

        viewModel = ViewModelProviders.of(this.getActivity()).get(ItinerariosViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(itinerario != null){
            viewModel.itinerario = itinerario;
            flagInicioEdicao = true;
        } else{
            //viewModel.itinerario.setTempo(new DateTime(0));
        }

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        if(viewModel.itinerario.getProgramadoPara() == null){
            textViewProgramado.setVisibility(View.GONE);
            btnTrocar.setVisibility(View.GONE);
        } else{
            exibeDataEscolhida();
        }

        viewModel.empresas.observe(this, empresasObserver);

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        if(itinerario != null){
            viewModel.editarItinerario();
        } else{
            viewModel.salvarItinerario(false);
        }

        viewModel.retorno.observe(this, retornoObserver);

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

        if(flagInicioEdicao && itinerario.getProgramadoPara() != null){
            flagInicioEdicao = false;
            return;
        }

        if(ativo){
            FormCalendario formCalendario = new FormCalendario();
            formCalendario.setParent(this);

            if(viewModel.itinerario.getProgramadoPara() != null){
                formCalendario.setDataAnterior(viewModel.itinerario.getProgramadoPara().toCalendar(null));
            }

            formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
        } else{
            ocultaDataEscolhida();
        }

    }

    @Override
    public void setData(Calendar umaData) {
        viewModel.itinerario.setProgramadoPara(new DateTime(umaData,
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))));

        if(viewModel.itinerario.getProgramadoPara() == null){
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
        viewModel.itinerario.setProgramadoPara(null);
    }

    private void exibeDataEscolhida(){

        textViewProgramado.setText(DateTimeFormat
                .forPattern("dd/MM/yy HH:mm").print(viewModel.itinerario.getProgramadoPara()));

        textViewProgramado.setVisibility(View.VISIBLE);
        btnTrocar.setVisibility(View.VISIBLE);
    }

    @BindingAdapter("entries")
    public static void setSpinnerEntries(Spinner spinner, LiveData<List<Empresa>> empresas){

        if(empresas.getValue() != null){
            EmpresaAdapterSpinner adapter = new EmpresaAdapterSpinner(ctx, R.layout.linha_empresas_spinner,
                    R.id.textViewNome, empresas.getValue());
            spinner.setAdapter(adapter);
        }

    }

    public void setSpinnerEntries(Spinner spinner, List<Empresa> empresas){

        if(empresas != null){
            EmpresaAdapterSpinner adapter = new EmpresaAdapterSpinner(ctx, R.layout.linha_empresas_spinner,
                    R.id.textViewNome, empresas);
            spinner.setAdapter(adapter);

            if(itinerario != null){
                Empresa empresa = new Empresa();
                empresa.setId(itinerario.getEmpresa());
                viacaoSelecionada = viewModel.empresas.getValue().indexOf(empresa);
                binding.spinnerEmpresa.setSelection(viacaoSelecionada);
                viewModel.empresa = viewModel.empresas.getValue().get(viacaoSelecionada);
            } else if(viewModel.empresa != null){
                Empresa empresa = new Empresa();
                empresa.setId(viewModel.empresa.getId());
                viacaoSelecionada = viewModel.empresas.getValue().indexOf(empresa);
                binding.spinnerEmpresa.setSelection(viacaoSelecionada);
            }

        }

    }

    public void onItemSelectedSpinnerEmpresa(AdapterView<?> adapterView, View view, int i, long l){

        if(((i == 0 && i != viacaoSelecionada) || (i == 0 && i != viacaoSelecionada)) && viacaoSelecionada != -1){
            viewModel.empresa = viewModel.empresas.getValue().get(viacaoSelecionada);
            binding.spinnerEmpresa.setSelection(viacaoSelecionada, false);
        } else{
            viewModel.empresa = viewModel.empresas.getValue().get(i);
            binding.spinnerEmpresa.setSelection(i, false);
        }


    }

    @BindingAdapter("android:text")
    public static void setText(TextInputEditText view, DateTime date) {

        if(date != null){
            String formatted = DateTimeFormat.forPattern("HH:mm").print(date);
            view.setText(formatted);
        } else{
            view.setText("00:00");
        }


    }

    @InverseBindingAdapter(attribute = "android:text")
    public static DateTime getText(TextInputEditText view){

        try{
            return DateTimeFormat.forPattern("HH:mm").parseDateTime(view.getText().toString());
        } catch(IllegalArgumentException e){
            return null;
        }


    }

    @InverseBindingAdapter(attribute = "android:text")
    public static Double getDistancia(TextInputEditText view){

        try{
            return Double.parseDouble(view.getText().toString());
        } catch(IllegalArgumentException e){
            return null;
        }


    }

    @BindingAdapter("android:text")
    public static void setDistancia(TextInputEditText view, Double distancia) {

        if(distancia != null){
            String formatted = NumberFormat.getNumberInstance().format(distancia);
            view.setText(formatted);
        } else{
            view.setText("0");
        }


    }

    Observer<List<Empresa>> empresasObserver = new Observer<List<Empresa>>() {
        @Override
        public void onChanged(List<Empresa> empresas) {
            setSpinnerEntries(binding.spinnerEmpresa, empresas);
        }
    };

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getContext().getApplicationContext(), "Itinerário cadastrado!", Toast.LENGTH_SHORT).show();
                viewModel.paradasItinerario.setValue(new ArrayList<ParadaItinerarioBairro>());
                viewModel.setItinerario(new Itinerario());
                dismiss();
            } else if(retorno == 0){
                Toast.makeText(getContext().getApplicationContext(),
                        "Dados necessários não informados. Por favor preencha " +
                                "todos os dados obrigatórios!",
                        Toast.LENGTH_SHORT).show();
            } else if(retorno == 2){
                Toast.makeText(getContext().getApplicationContext(),
                        "Houve erro ao cadastrar o itinerário no sentido reverso. Por favor cadastre novamente de forma manual.",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

}
