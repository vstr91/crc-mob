package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaItinerariosBinding;
import br.com.vostre.circular.databinding.LinhaItinerariosTarifaBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.DetalhesItinerarioActivity;

public class ItinerarioTarifaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaItinerariosTarifaBinding binding;
    AppCompatActivity ctx;

    public ItinerarioTarifaViewHolder(LinhaItinerariosTarifaBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ItinerarioPartidaDestino itinerario) {
        binding.setItinerario(itinerario);

        if(itinerario.getItinerario().getObservacao() == null || itinerario.getItinerario().getObservacao().isEmpty()){
            binding.textViewObservacao.setVisibility(View.GONE);
        } else{
            binding.textViewObservacao.setVisibility(View.VISIBLE);
        }

        binding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.checkBoxAtivo.setChecked(!binding.checkBoxAtivo.isChecked());
                itinerario.setSelecionado(binding.checkBoxAtivo.isChecked());
            }
        });

        binding.executePendingBindings();
    }
}
