package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import br.com.vostre.circular.databinding.LinhaItinerariosTarifaBinding;
import br.com.vostre.circular.databinding.LinhaTarifasSecoesBinding;
import br.com.vostre.circular.model.SecaoItinerario;
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
        binding.setItinerario(itinerario);

        binding.executePendingBindings();
    }
}
