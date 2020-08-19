package br.com.vostre.circular.view.viewHolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import br.com.vostre.circular.databinding.LinhaTarifasSecoesBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;

public class TarifaSecaoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaTarifasSecoesBinding binding;
    AppCompatActivity ctx;

    public TarifaSecaoViewHolder(LinhaTarifasSecoesBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ItinerarioPartidaDestino itinerario) {
        binding.executePendingBindings();
    }
}
