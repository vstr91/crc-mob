package br.com.vostre.circular.view.form;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
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

import java.util.Calendar;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormMensagemBinding;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.viewModel.MensagensViewModel;

public class FormMensagem extends FormBase {

    FormMensagemBinding binding;
    MensagensViewModel viewModel;

    Mensagem mensagem;
    public Boolean flagInicioEdicao;

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
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
                inflater, R.layout.form_mensagem, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(MensagensViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(mensagem != null){
            viewModel.mensagem = mensagem;
            flagInicioEdicao = true;
        }

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        String id = PreferenceUtils.carregarUsuarioLogado(getContext().getApplicationContext());

        if(id != null && !id.equals("")){
            mensagem.setUsuarioCadastro(id);
            mensagem.setUsuarioUltimaAlteracao(id);
        }

        mensagem.setServidor(false);

        if(mensagem != null){
            viewModel.editarMensagem();
        } else{
            viewModel.salvarMensagem();
        }

        dismiss();
    }

    public void onClickFechar(View v){
        dismiss();
    }

}
