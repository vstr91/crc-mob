package br.com.vostre.circular.view.viewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import br.com.vostre.circular.databinding.LinhaBairrosBinding;
import br.com.vostre.circular.databinding.LinhaLegendaBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.Legenda;
import br.com.vostre.circular.view.listener.SelectListener;

public class LegendaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaLegendaBinding binding;
    Context ctx;

    public LegendaViewHolder(LinhaLegendaBinding binding, Context context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Legenda legenda) {
        binding.setLegenda(legenda);

        binding.textViewNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //nada ainda
            }
        });

        binding.executePendingBindings();
    }
}
