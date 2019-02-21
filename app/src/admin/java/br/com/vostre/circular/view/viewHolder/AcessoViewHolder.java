package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaAcessosBinding;
import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.pojo.AcessoTotal;
import br.com.vostre.circular.view.form.FormAcesso;
import br.com.vostre.circular.view.form.FormPais;

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
