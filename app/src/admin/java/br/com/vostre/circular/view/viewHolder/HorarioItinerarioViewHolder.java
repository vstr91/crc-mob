package br.com.vostre.circular.view.viewHolder;

import android.arch.lifecycle.ViewModel;
import android.databinding.BindingAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaHorariosItinerariosBinding;
import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.view.form.FormHorarioItinerario;
import br.com.vostre.circular.viewModel.HorariosItinerarioViewModel;

public class HorarioItinerarioViewHolder extends RecyclerView.ViewHolder {

    private final LinhaHorariosItinerariosBinding binding;
    AppCompatActivity ctx;
    HorariosItinerarioViewModel viewModel;
    boolean clickListener = false;

    public HorarioItinerarioViewHolder(LinhaHorariosItinerariosBinding binding, AppCompatActivity context,
                                       HorariosItinerarioViewModel viewModel, boolean clickListener) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.viewModel = viewModel;
        this.clickListener = clickListener;
    }

    public void bind(final HorarioItinerarioNome horarioItinerario) {

        if(horarioItinerario.getHorarioItinerario() == null){
            horarioItinerario.setHorarioItinerario(new HorarioItinerario());
        }

        binding.setHorario(horarioItinerario);

        if(horarioItinerario.getHorarioItinerario() != null && horarioItinerario.getHorarioItinerario().getProgramadoPara() != null
                && horarioItinerario.getHorarioItinerario().getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(horarioItinerario.getHorarioItinerario().getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        if(horarioItinerario.getHorarioItinerario() == null
                || horarioItinerario.getHorarioItinerario().getObservacao() == null
                || horarioItinerario.getHorarioItinerario().getObservacao().isEmpty()){
            binding.textViewObservacao.setVisibility(View.GONE);
        } else{
            binding.textViewObservacao.setVisibility(View.VISIBLE);
        }

        if(clickListener){
            binding.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(horarioItinerario.isAtivo()){
                        horarioItinerario.reseta();
                        viewModel.setHorario(horarioItinerario);
                        viewModel.editarHorario();
                    } else{
                        FormHorarioItinerario formHorario = new FormHorarioItinerario();
                        formHorario.setHorario(horarioItinerario);
                        formHorario.setCtx(ctx.getApplication());
                        formHorario.flagInicioEdicao = true;
                        formHorario.show(ctx.getSupportFragmentManager(), "formHorario");
                    }

                }
            });

            binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    FormHorarioItinerario formHorario = new FormHorarioItinerario();
                    formHorario.setHorario(horarioItinerario);
                    formHorario.setCtx(ctx.getApplication());
                    formHorario.flagInicioEdicao = true;
                    formHorario.show(ctx.getSupportFragmentManager(), "formHorario");
                    return false;
                }
            });
        }

        binding.executePendingBindings();
    }

    @BindingAdapter("text")
    public static void setText(TextView view, Long date) {
        String formatted = DateTimeFormat.forPattern("HH:mm").print(date);
        view.setText(formatted);
    }

}
