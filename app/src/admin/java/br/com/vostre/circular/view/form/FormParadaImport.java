package br.com.vostre.circular.view.form;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingAdapter;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormParadaImportBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.adapter.BairroAdapterSpinner;
import br.com.vostre.circular.viewModel.ImportarParadasViewModel;

public class FormParadaImport extends FormBase {

    static FormParadaImportBinding binding;

    Double latitude;
    Double longitude;

    static ParadaBairro parada;
    public Boolean flagInicioEdicao;
    static Application ctx;
    static ImportarParadasViewModel viewModel;

    BairroAdapterSpinner adapter;

    public ParadaBairro getParada() {
        return parada;
    }

    public void setParada(ParadaBairro parada) {
        this.parada = parada;
    }

    public static Application getCtx() {
        return ctx;
    }

    public static void setCtx(Application ctx) {
        FormParadaImport.ctx = ctx;
    }

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
                inflater, R.layout.form_parada_import, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(ImportarParadasViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        viewModel.bairros.observe(this, bairrosObserver);

        if(parada != null){
            viewModel.parada = parada;

            int sentido = parada.getParada().getSentido();

            switch(sentido){
                case 0:
                    binding.spinnerSentido.setSelection(1);
                    break;
                case 1:
                    binding.spinnerSentido.setSelection(2);
                    break;
                default:
                    binding.spinnerSentido.setSelection(0);
                    break;
            }

            flagInicioEdicao = true;
        }

        if(parada != null && (parada.getParada().getRua() == null || parada.getParada().getRua().isEmpty())){
            viewModel.buscarRua(parada.getParada());
        }

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        viewModel.parada.getParada().setLatitude(latitude);
        viewModel.parada.getParada().setLongitude(longitude);

        viewModel.salvarParada();

        viewModel.retorno.observe(this, retornoObserver);

    }

    public void onClickFechar(View v){
        viewModel.bairro = null;
        dismiss();
    }

    public void onClickBtnBuscaRua(View v){
        Toast.makeText(ctx, "Atualizando endereço...", Toast.LENGTH_SHORT).show();
        viewModel.buscarRua(parada.getParada());
    }

    @BindingAdapter("entriesParada")
    public static void setSpinnerEntries(Spinner spinner, LiveData<List<BairroCidade>> bairros){

        if(bairros.getValue() != null && ctx != null){
            BairroAdapterSpinner adapter = new BairroAdapterSpinner(ctx, R.layout.linha_bairros_spinner,
                    R.id.textViewNome, bairros.getValue());
            spinner.setAdapter(adapter);

            if(parada != null){
                BairroCidade bairro = new BairroCidade();
                bairro.getBairro().setId(parada.getParada().getBairro());
                int i = viewModel.bairros.getValue().indexOf(bairro);
                binding.spinnerBairro.setSelection(i);
            }

        }

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
                valor = valor.replace(".", "");
                valor = valor.replace(",", ".");
                Double d = Double.parseDouble(valor);
                return d;
            } catch(NumberFormatException e){
                return 0.0;
            }

        }


    }

    public void setSpinnerEntries(Spinner spinner, List<BairroCidade> bairros){

        if(bairros != null){
            BairroAdapterSpinner adapter = new BairroAdapterSpinner(ctx, R.layout.linha_bairros_spinner,
                    R.id.textViewNome, bairros);
            spinner.setAdapter(adapter);

            if(parada != null){
                BairroCidade bairro = new BairroCidade();
                bairro.getBairro().setId(parada.getParada().getBairro());
                int i = viewModel.bairros.getValue().indexOf(bairro);
                binding.spinnerBairro.setSelection(i);
            }

        }

    }

    public void onItemSelectedSpinnerBairro (AdapterView<?> adapterView, View view, int i, long l){
        viewModel.bairro = viewModel.bairros.getValue().get(i);
    }

    public void onItemSelectedSpinnerSentido (AdapterView<?> adapterView, View view, int i, long l){

        switch(i){
            case 0: // nao mostrar
                viewModel.getParada().getParada().setSentido(-1);
                break;
            case 1: // centro
                viewModel.getParada().getParada().setSentido(0);
                break;
            case 2: // bairro
                viewModel.getParada().getParada().setSentido(1);
                break;
        }

    }

    Observer<List<BairroCidade>> bairrosObserver = new Observer<List<BairroCidade>>() {
        @Override
        public void onChanged(List<BairroCidade> bairros) {
            setSpinnerEntries(binding.spinnerBairro, bairros);
        }
    };

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getContext().getApplicationContext(), "Parada cadastrada!", Toast.LENGTH_SHORT).show();
                viewModel.setParada(new ParadaBairro());
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
