package br.com.vostre.circular.view.form;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
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
import br.com.vostre.circular.databinding.FormBairroBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.adapter.CidadeAdapterSpinner;
import br.com.vostre.circular.viewModel.BairrosViewModel;

public class FormBairro extends FormBase {

    FormBairroBinding binding;
    Calendar data;

    TextView textViewProgramado;
    Button btnTrocar;

    BairrosViewModel viewModel;

    BairroCidade bairro;
    public Boolean flagInicioEdicao;
    CidadeAdapterSpinner adapter;

    static Application ctx;

    CidadeEstado cidadeDetalhada;

    public Application getCtx() {
        return ctx;
    }

    public void setCtx(Application ctx) {
        this.ctx = ctx;
    }

    public BairroCidade getBairro() {
        return bairro;
    }

    public void setBairro(BairroCidade bairro) {
        this.bairro = bairro;
    }

    public CidadeAdapterSpinner getAdapter() {
        return adapter;
    }

    public void setAdapter(CidadeAdapterSpinner adapter) {
        this.adapter = adapter;
    }

    public CidadeEstado getCidadeDetalhada() {
        return cidadeDetalhada;
    }

    public void setCidadeDetalhada(CidadeEstado cidadeDetalhada) {
        this.cidadeDetalhada = cidadeDetalhada;
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
                inflater, R.layout.form_bairro, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(BairrosViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(bairro != null){
            viewModel.bairro = bairro;
            flagInicioEdicao = true;
        }

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        if(viewModel.bairro.getBairro().getProgramadoPara() == null){
            textViewProgramado.setVisibility(View.GONE);
            btnTrocar.setVisibility(View.GONE);
        } else{
            exibeDataEscolhida();
        }

        viewModel.cidades.observe(this, cidadesObserver);

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        if(bairro != null){
            viewModel.editarBairro();
        } else{
            viewModel.salvarBairro();
        }

        viewModel.retorno.observe(this, retornoObserver);
    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickTrocar(View v){
        FormCalendario formCalendario = new FormCalendario();
        formCalendario.setParent(this);
        formCalendario.setDataAnterior(viewModel.bairro.getBairro().getProgramadoPara().toCalendar(null));
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    public void onSwitchProgramadoChange(CompoundButton btn, boolean ativo){

        if(flagInicioEdicao && bairro.getBairro().getProgramadoPara() != null){
            flagInicioEdicao = false;
            return;
        }

        if(ativo){
            FormCalendario formCalendario = new FormCalendario();
            formCalendario.setParent(this);

            if(viewModel.bairro.getBairro().getProgramadoPara() != null){
                formCalendario.setDataAnterior(viewModel.bairro.getBairro().getProgramadoPara().toCalendar(null));
            }

            formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
        } else{
            ocultaDataEscolhida();
        }

    }

    @Override
    public void setData(Calendar umaData) {
        viewModel.bairro.getBairro().setProgramadoPara(new DateTime(umaData,
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))));

        if(viewModel.bairro.getBairro().getProgramadoPara() == null){
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
        viewModel.bairro.getBairro().setProgramadoPara(null);
    }

    private void exibeDataEscolhida(){

        textViewProgramado.setText(DateTimeFormat
                .forPattern("dd/MM/yy HH:mm").print(viewModel.bairro.getBairro().getProgramadoPara()));

        textViewProgramado.setVisibility(View.VISIBLE);
        btnTrocar.setVisibility(View.VISIBLE);
    }

    @BindingAdapter("entries")
    public static void setSpinnerEntries(Spinner spinner, LiveData<List<CidadeEstado>> cidades){

        if(cidades.getValue() != null){
            CidadeAdapterSpinner adapter = new CidadeAdapterSpinner(ctx, R.layout.linha_cidades_spinner, R.id.textViewNome, cidades.getValue());
            spinner.setAdapter(adapter);
        }

    }

    public void setSpinnerEntries(Spinner spinner, List<CidadeEstado> cidades){

        if(cidades != null){
            CidadeAdapterSpinner adapter = new CidadeAdapterSpinner(ctx, R.layout.linha_cidades_spinner, R.id.textViewNome, cidades);
            spinner.setAdapter(adapter);

            if(bairro != null){
                CidadeEstado cidade = new CidadeEstado();
                cidade.getCidade().setId(bairro.getIdCidade());
                int i = viewModel.cidades.getValue().indexOf(cidade);
                binding.spinnerCidade.setSelection(i);
            }

            if(cidadeDetalhada != null){
                CidadeEstado cidade = new CidadeEstado();
                cidade.getCidade().setId(cidadeDetalhada.getCidade().getId());
                int i = viewModel.cidades.getValue().indexOf(cidade);
                binding.spinnerCidade.setSelection(i);
            }

        }

    }

    public void onItemSelectedSpinnerCidade (AdapterView<?> adapterView, View view, int i, long l){
        viewModel.cidade = viewModel.cidades.getValue().get(i);
    }

    Observer<List<CidadeEstado>> cidadesObserver = new Observer<List<CidadeEstado>>() {
        @Override
        public void onChanged(List<CidadeEstado> cidades) {
            setSpinnerEntries(binding.spinnerCidade, cidades);
        }
    };

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getContext().getApplicationContext(), "Bairro cadastrado!", Toast.LENGTH_SHORT).show();
                viewModel.setBairro(new BairroCidade());
                dismiss();
            } else if(retorno == 0){
                Toast.makeText(getContext().getApplicationContext(),
                        "Dados necessários não informados. Por favor preencha " +
                                "todos os dados obrigatórios!",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

}
