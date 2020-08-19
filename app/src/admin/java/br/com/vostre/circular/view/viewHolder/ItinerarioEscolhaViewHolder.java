package br.com.vostre.circular.view.viewHolder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaItinerariosBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.DetalhesItinerarioActivity;

public class ItinerarioEscolhaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaItinerariosBinding binding;
    AppCompatActivity ctx;
    static int FLAG;

    public ItinerarioEscolhaViewHolder(LinhaItinerariosBinding binding, AppCompatActivity context) {
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

        if(itinerario.getItinerario().getProgramadoPara() != null && itinerario.getItinerario().getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(itinerario.getItinerario().getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("itinerario", itinerario.getItinerario().getId());
                ctx.setResult(Activity.RESULT_OK, i);
                ctx.finish();
            }
        });

        binding.executePendingBindings();
    }
}
