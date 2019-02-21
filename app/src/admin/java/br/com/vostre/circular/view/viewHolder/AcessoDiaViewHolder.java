package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaAcessosDetalheBinding;
import br.com.vostre.circular.databinding.LinhaAcessosDiaBinding;
import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.model.pojo.AcessoDia;
import br.com.vostre.circular.view.AcessoDiaActivity;
import br.com.vostre.circular.view.DetalheAcessoActivity;

public class AcessoDiaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaAcessosDiaBinding binding;
    AppCompatActivity ctx;

    public AcessoDiaViewHolder(LinhaAcessosDiaBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final AcessoDia acesso) {
        //final String umDia = acesso.getDia();

        //acesso.setDia(DateTimeFormat.forPattern("dd/MM/YYYY").print(DateTimeFormat.forPattern("YYYY-MM-dd").parseDateTime(acesso.getDia())));

        binding.setAcesso(acesso);

        binding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, DetalheAcessoActivity.class);
                i.putExtra("dia", acesso.getDia());
                ctx.startActivity(i);
            }
        });

        binding.executePendingBindings();
    }
}
