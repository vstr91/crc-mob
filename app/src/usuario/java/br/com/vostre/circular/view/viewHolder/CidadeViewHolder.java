package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaCidadesBinding;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.form.FormCidade;

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
        binding.executePendingBindings();
    }
}
