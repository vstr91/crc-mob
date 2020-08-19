package br.com.vostre.circular.view.viewHolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import br.com.vostre.circular.databinding.LinhaAcessosBinding;
import br.com.vostre.circular.model.pojo.AcessoTotal;
import br.com.vostre.circular.view.form.FormAcesso;

public class AcessoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaAcessosBinding binding;
    AppCompatActivity ctx;
    String dia;

    public AcessoViewHolder(LinhaAcessosBinding binding, AppCompatActivity context, String dia) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.dia = dia;
    }

    public void bind(final AcessoTotal acesso) {
        binding.setAcesso(acesso);

        binding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormAcesso formAcesso = new FormAcesso();
                formAcesso.setAcesso(acesso);
                formAcesso.setDia(dia);
                formAcesso.show(ctx.getSupportFragmentManager(), "formAcesso");
            }
        });

        binding.executePendingBindings();
    }
}
