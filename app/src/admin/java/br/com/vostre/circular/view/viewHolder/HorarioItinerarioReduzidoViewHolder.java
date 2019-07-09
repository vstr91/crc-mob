package br.com.vostre.circular.view.viewHolder;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaHorariosItinerariosBinding;
import br.com.vostre.circular.databinding.LinhaHorariosItinerariosReduzidaBinding;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.view.form.FormHorarioItinerario;
import br.com.vostre.circular.viewModel.HorariosItinerarioViewModel;

public class HorarioItinerarioReduzidoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaHorariosItinerariosReduzidaBinding binding;
    AppCompatActivity ctx;
    HorariosItinerarioViewModel viewModel;
    boolean clickListener = false;

    public HorarioItinerarioReduzidoViewHolder(LinhaHorariosItinerariosReduzidaBinding binding, AppCompatActivity context,
                                               HorariosItinerarioViewModel viewModel, boolean clickListener) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.viewModel = viewModel;
        this.clickListener = clickListener;
    }

    public void bind(final HorarioItinerarioNome horarioItinerario, boolean existe, boolean isHorariosAtuais) {

        if(horarioItinerario.getHorarioItinerario() == null){
            horarioItinerario.setHorarioItinerario(new HorarioItinerario());
        }

        binding.setHorario(horarioItinerario);

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

                        horarioItinerario.getHorarioItinerario().setDomingo(true);
                        horarioItinerario.getHorarioItinerario().setSegunda(true);
                        horarioItinerario.getHorarioItinerario().setTerca(true);
                        horarioItinerario.getHorarioItinerario().setQuarta(true);
                        horarioItinerario.getHorarioItinerario().setQuinta(true);
                        horarioItinerario.getHorarioItinerario().setSexta(true);
                        horarioItinerario.getHorarioItinerario().setSabado(true);

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

        // destaca com vermelhos os atuais nao existentes nos processados, com verde os processados ja existentes nos atuais e vice-versa e com amarelo os processados nao existentes nos atuais
        if(isHorariosAtuais){
            if(existe){
                binding.cardview.setCardBackgroundColor(ctx.getResources().getColor(R.color.verde));
            } else{
                binding.cardview.setCardBackgroundColor(ctx.getResources().getColor(R.color.vermelho));
            }
        } else{
            if(existe){
                binding.cardview.setCardBackgroundColor(ctx.getResources().getColor(R.color.verde));
            } else{
                binding.cardview.setCardBackgroundColor(ctx.getResources().getColor(R.color.amarelo));
            }
        }



        binding.executePendingBindings();
    }

    @BindingAdapter("text")
    public static void setText(TextView view, Long date) {

        if(date != null){
            String formatted = DateTimeFormat.forPattern("HH:mm").print(date);
            view.setText(formatted);
        }

    }

}
