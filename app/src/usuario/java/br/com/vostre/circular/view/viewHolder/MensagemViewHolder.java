package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaMensagensBinding;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.view.form.FormDetalheMensagem;
import br.com.vostre.circular.view.form.FormMensagem;

public class MensagemViewHolder extends RecyclerView.ViewHolder {

    private final LinhaMensagensBinding binding;
    AppCompatActivity ctx;

    public MensagemViewHolder(LinhaMensagensBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Mensagem mensagem) {
        binding.setMensagem(mensagem);

        binding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormDetalheMensagem formMensagem = new FormDetalheMensagem();
                formMensagem.setMensagem(mensagem);
                formMensagem.show(ctx.getSupportFragmentManager(), "formMensagem");
            }
        });

        // EDICAO DE MENSAGEM ENVIADA PELO USUARIO DESABILITADA

//        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                FormMensagem formMensagem = new FormMensagem();
//                formMensagem.setMensagem(mensagem);
//                formMensagem.flagInicioEdicao = true;
//                formMensagem.show(ctx.getSupportFragmentManager(), "formMensagem");
//                return false;
//            }
//        });

        binding.executePendingBindings();
    }
}
