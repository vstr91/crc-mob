package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.databinding.LinhaParadasSugeridasBinding;
import br.com.vostre.circular.listener.ParadaSugestaoListener;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.view.form.FormParada;

public class ParadaSugestaoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaParadasSugeridasBinding binding;
    AppCompatActivity ctx;
    ParadaSugestaoListener listener;

    public ParadaSugestaoViewHolder(LinhaParadasSugeridasBinding binding, AppCompatActivity context, ParadaSugestaoListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.listener = listener;
    }

    public void bind(final ParadaSugestaoBairro parada) {
        binding.setParada(parada);

        binding.btnAceitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelected(parada.getParada().getId(), 1);
            }
        });

        binding.btnRejeitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelected(parada.getParada().getId(), 0);
            }
        });

        binding.btnVerNoMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelected(parada.getParada().getId(), 2);
            }
        });

        binding.executePendingBindings();
    }
}
