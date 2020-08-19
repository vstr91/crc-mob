package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaMensagensBinding;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.view.viewHolder.MensagemViewHolder;

public class MensagemAdapter extends RecyclerView.Adapter<MensagemViewHolder> {

    public List<Mensagem> mensagens;
    AppCompatActivity ctx;

    public MensagemAdapter(List<Mensagem> mensagens, AppCompatActivity context){
        this.mensagens = mensagens;
        ctx = context;
    }

    @Override
    public MensagemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaMensagensBinding itemBinding =
                LinhaMensagensBinding.inflate(layoutInflater, parent, false);
        return new MensagemViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(MensagemViewHolder holder, int position) {
        Mensagem mensagem = mensagens.get(position);
        holder.bind(mensagem);
    }

    @Override
    public int getItemCount() {

        if(mensagens == null){
            return 0;
        } else{
            return mensagens.size();
        }


    }
}
