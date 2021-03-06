package br.com.vostre.circular.view.form;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormDetalheMensagemBinding;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.MensagemResposta;
import br.com.vostre.circular.view.adapter.MensagemRespostaAdapter;
import br.com.vostre.circular.viewModel.DetalheMensagensViewModel;

public class FormDetalheMensagem extends FormBase {

    FormDetalheMensagemBinding binding;
    Calendar data;

    DetalheMensagensViewModel viewModel;

    Mensagem mensagem;
//    MensagemResposta resposta;

//    RecyclerView listRespostas;
//    List<MensagemResposta> respostas;
//    MensagemRespostaAdapter adapter;

    Application ctx;

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

//    public MensagemResposta getResposta() {
//        return resposta;
//    }
//
//    public void setResposta(MensagemResposta resposta) {
//        this.resposta = resposta;
//    }

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
                inflater, R.layout.form_detalhe_mensagem, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(DetalheMensagensViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);
        binding.setMensagem(mensagem);

        if(mensagem != null){
            viewModel.mensagem = mensagem;
            viewModel.carregarRespostas(mensagem);

            if(mensagem.getResumo() == null || mensagem.getResumo().isEmpty()){
                binding.textViewResumo.setVisibility(View.GONE);
            } else{
                binding.textViewResumo.setVisibility(View.VISIBLE);
            }

            if(mensagem.getServidor()){
                viewModel.marcarComoLida(mensagem);
            }

//            viewModel.respostas.observe(this, respostasObserver);
        }

//        listRespostas = binding.listRespostas;

//        adapter = new MensagemRespostaAdapter(respostas, (AppCompatActivity) getActivity());
//
//        listRespostas.setAdapter(adapter);

        return binding.getRoot();

    }

    public void onClickResponder(View v){

        if(viewModel.resposta.getResposta() != null && !viewModel.resposta.getResposta().isEmpty()){
            viewModel.resposta.setMensagem(viewModel.mensagem.getId());
            viewModel.salvarResposta();
            dismiss();
        } else{
            Toast.makeText(getContext(), "Por favor digite o texto de resposta", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClickFechar(View v){
        dismiss();
    }

    @BindingAdapter("android:text")
    public static void setText(TextView view, DateTime date) {
        String formatted = DateTimeFormat.forPattern("dd/MM/yyyy à's' HH:mm").print(date);
        view.setText(formatted);
    }

//    Observer<List<MensagemResposta>> respostasObserver = new Observer<List<MensagemResposta>>() {
//        @Override
//        public void onChanged(List<MensagemResposta> respostas) {
//            adapter.respostas = respostas;
//            adapter.notifyDataSetChanged();
//        }
//    };

}
