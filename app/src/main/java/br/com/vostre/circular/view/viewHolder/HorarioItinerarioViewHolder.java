package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaHorariosItinerariosBinding;
import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.form.FormHorarioItinerario;
import br.com.vostre.circular.view.form.FormPais;

public class HorarioItinerarioViewHolder extends RecyclerView.ViewHolder {

    private final LinhaHorariosItinerariosBinding binding;
    AppCompatActivity ctx;

    public HorarioItinerarioViewHolder(LinhaHorariosItinerariosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final HorarioItinerario horarioItinerario) {
        binding.setHorario(horarioItinerario);

        if(horarioItinerario.getProgramadoPara() != null && horarioItinerario.getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(horarioItinerario.getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormHorarioItinerario formHorario = new FormHorarioItinerario();
                formHorario.setHorario(horarioItinerario);
                formHorario.flagInicioEdicao = true;
                formHorario.show(ctx.getSupportFragmentManager(), "formHorario");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
