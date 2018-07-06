package br.com.vostre.circular.view.viewHolder;

import android.os.Bundle;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import br.com.vostre.circular.databinding.LinhaCidadesBinding;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.form.FormBairro;

public class CidadeViewHolder extends RecyclerView.ViewHolder {

    private final LinhaCidadesBinding binding;
    AppCompatActivity ctx;

    public CidadeViewHolder(LinhaCidadesBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final CidadeEstado cidade) {
        binding.setCidade(cidade);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormBairro formBairro = new FormBairro();

                Bundle bundle = new Bundle();
                bundle.putString("cidade", cidade.getCidade().getId());

                formBairro.setArguments(bundle);
                formBairro.setCtx(ctx.getApplication());
                formBairro.show(ctx.getSupportFragmentManager(), "formBairro");

            }
        };

        binding.circleView2.setOnClickListener(listener);
        binding.textViewNome.setOnClickListener(listener);

        binding.executePendingBindings();
    }
}
