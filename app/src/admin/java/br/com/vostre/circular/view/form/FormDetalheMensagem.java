package br.com.vostre.circular.view.form;

import android.app.Application;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;
import java.util.List;

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
    MensagemResposta resposta;

    //RecyclerView listRespostas;
    List<MensagemResposta> respostas;
    MensagemRespostaAdapter adapter;

    Application ctx;

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    public MensagemResposta getResposta() {
        return resposta;
    }

    public void setResposta(MensagemResposta resposta) {
        this.resposta = resposta;
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
                inflater, R.layout.form_detalhe_mensagem, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(DetalheMensagensViewModel.class);
        viewModel.respostas.observe(this, respostasObserver);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(mensagem != null){
            viewModel.mensagem = mensagem;
            binding.setMensagem(mensagem);

            if(mensagem.getResumo() == null || mensagem.getResumo().isEmpty()){
                binding.textViewResumo.setVisibility(View.GONE);
            } else{
                binding.textViewResumo.setVisibility(View.VISIBLE);
            }

            if(mensagem.getEmail() == null || mensagem.getEmail().isEmpty()){
                binding.textViewEmail.setVisibility(View.GONE);
            } else{
                binding.textViewEmail.setVisibility(View.VISIBLE);
            }
//            viewModel.carregarRespostas(mensagem);

            if(!mensagem.getServidor()){
                viewModel.marcarComoLida(mensagem);
            }

        }

        //listRespostas = binding.listRespostas;

        adapter = new MensagemRespostaAdapter(respostas, (AppCompatActivity) getActivity());

        //listRespostas.setAdapter(adapter);

        return binding.getRoot();

    }

    public void onClickResponder(View v){

        if(viewModel.resposta.getResposta() != null && !viewModel.resposta.getResposta().isEmpty()){
            viewModel.resposta.setMensagem(viewModel.mensagem.getId());
            viewModel.salvarResposta();
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

    Observer<List<MensagemResposta>> respostasObserver = new Observer<List<MensagemResposta>>() {
        @Override
        public void onChanged(List<MensagemResposta> respostas) {
            adapter.respostas = respostas;
            adapter.notifyDataSetChanged();
        }
    };

}
