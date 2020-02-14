package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import br.com.vostre.circular.databinding.LinhaTarifasItinerariosBinding;
import br.com.vostre.circular.databinding.LinhaTarifasSecoesBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;

public class TarifaItinerarioViewHolder extends RecyclerView.ViewHolder {

    private final LinhaTarifasItinerariosBinding binding;
    AppCompatActivity ctx;

    public TarifaItinerarioViewHolder(LinhaTarifasItinerariosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ItinerarioPartidaDestino itinerario) {

        binding.setItinerario(itinerario);

        binding.executePendingBindings();
    }
}
