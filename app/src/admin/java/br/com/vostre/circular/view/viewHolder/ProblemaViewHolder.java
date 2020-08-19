package br.com.vostre.circular.view.viewHolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import br.com.vostre.circular.databinding.LinhaProblemasBinding;
import br.com.vostre.circular.model.pojo.ProblemaTipo;

public class ProblemaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaProblemasBinding binding;
    AppCompatActivity ctx;

    public ProblemaViewHolder(LinhaProblemasBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ProblemaTipo problema) {
        binding.setProblema(problema);

//        binding.cardview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FormDetalheMensagem formMensagem = new FormDetalheMensagem();
//                formMensagem.setMensagem(mensagem);
//                formMensagem.show(ctx.getSupportFragmentManager(), "formMensagem");
//            }
//        });
//
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
