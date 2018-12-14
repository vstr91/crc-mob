package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import br.com.vostre.circular.databinding.LinhaSecoesBinding;
import br.com.vostre.circular.databinding.LinhaSecoesDetalhadaBinding;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.SecaoItinerarioParada;

public class SecaoItinerarioDetalhadoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaSecoesDetalhadaBinding binding;
    AppCompatActivity ctx;

    public SecaoItinerarioDetalhadoViewHolder(LinhaSecoesDetalhadaBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final SecaoItinerarioParada secao) {
        binding.setSecao(secao);

        binding.executePendingBindings();
    }
}
