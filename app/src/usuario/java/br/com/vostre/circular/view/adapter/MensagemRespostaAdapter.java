package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaMensagensBinding;
import br.com.vostre.circular.databinding.LinhaRespostasBinding;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.MensagemResposta;
import br.com.vostre.circular.view.viewHolder.MensagemRespostaViewHolder;

public class MensagemRespostaAdapter extends RecyclerView.Adapter<MensagemRespostaViewHolder> {

    public List<MensagemResposta> respostas;
    AppCompatActivity ctx;

    public MensagemRespostaAdapter(List<MensagemResposta> respostas, AppCompatActivity context){
        this.respostas = respostas;
        ctx = context;
    }

    @Override
    public MensagemRespostaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaRespostasBinding itemBinding =
                LinhaRespostasBinding.inflate(layoutInflater, parent, false);
        return new MensagemRespostaViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(MensagemRespostaViewHolder holder, int position) {
        MensagemResposta resposta = respostas.get(position);
        holder.bind(resposta);
    }

    @Override
    public int getItemCount() {

        if(respostas == null){
            return 0;
        } else{
            return respostas.size();
        }


    }
}
