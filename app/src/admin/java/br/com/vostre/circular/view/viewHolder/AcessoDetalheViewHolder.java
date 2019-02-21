package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import br.com.vostre.circular.databinding.LinhaAcessosBinding;
import br.com.vostre.circular.databinding.LinhaAcessosDetalheBinding;
import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.model.pojo.AcessoTotal;

public class AcessoDetalheViewHolder extends RecyclerView.ViewHolder {

    private final LinhaAcessosDetalheBinding binding;
    AppCompatActivity ctx;

    public AcessoDetalheViewHolder(LinhaAcessosDetalheBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Acesso acesso) {
        binding.setAcesso(acesso);

        binding.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx, "Clicou", Toast.LENGTH_SHORT).show();
            }
        });

        binding.executePendingBindings();
    }
}
