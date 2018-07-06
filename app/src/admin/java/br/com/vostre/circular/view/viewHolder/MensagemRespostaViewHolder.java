package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaMensagensBinding;
import br.com.vostre.circular.databinding.LinhaRespostasBinding;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.MensagemResposta;

public class MensagemRespostaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaRespostasBinding binding;
    AppCompatActivity ctx;

    public MensagemRespostaViewHolder(LinhaRespostasBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final MensagemResposta resposta) {
        binding.setResposta(resposta);
        binding.executePendingBindings();
    }
}
