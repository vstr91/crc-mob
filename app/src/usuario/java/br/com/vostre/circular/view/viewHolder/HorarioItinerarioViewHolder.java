package br.com.vostre.circular.view.viewHolder;

import android.arch.lifecycle.ViewModel;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaHorariosItinerariosBinding;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.Legenda;

public class HorarioItinerarioViewHolder extends RecyclerView.ViewHolder {

    private final LinhaHorariosItinerariosBinding binding;
    AppCompatActivity ctx;

    public HorarioItinerarioViewHolder(LinhaHorariosItinerariosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final HorarioItinerarioNome horarioItinerario, Legenda l, String proximoHorario, boolean passado) {

        if(horarioItinerario.getHorarioItinerario() == null){
            horarioItinerario.setHorarioItinerario(new HorarioItinerario());
        }

        binding.setHorario(horarioItinerario);

        if(passado){
            binding.cardview.setCardBackgroundColor(ctx.getResources().getColor(R.color.cinzaInativo));
        } else{
            binding.cardview.setCardBackgroundColor(Color.WHITE);
        }

        if(horarioItinerario.getIdHorario().equals(proximoHorario) || horarioItinerario.getHorarioItinerario().getId().equals(proximoHorario)){
            binding.cardview.setCardBackgroundColor(ctx.getResources().getColor(R.color.azulClaro));
        } else{
            binding.cardview.setCardBackgroundColor(Color.WHITE);
        }

        if(horarioItinerario.getHorarioItinerario() == null
                || horarioItinerario.getHorarioItinerario().getObservacao() == null
                || horarioItinerario.getHorarioItinerario().getObservacao().isEmpty()){
            binding.textViewObservacao.setVisibility(View.GONE);
        } else{
            binding.textViewObservacao.setVisibility(View.VISIBLE);
        }

        if(l != null){
            binding.imageViewCor.setBackgroundColor(l.getCor());
            binding.imageViewCor.setVisibility(View.VISIBLE);
        } else{
            binding.imageViewCor.setVisibility(View.GONE);
        }

        binding.executePendingBindings();
    }

    public void bind(final HorarioItinerarioNome horarioItinerario, String proximoHorario, boolean passado) {

        if(horarioItinerario.getHorarioItinerario() == null){
            horarioItinerario.setHorarioItinerario(new HorarioItinerario());
        }

        if(passado){
            binding.cardview.setCardBackgroundColor(ctx.getResources().getColor(R.color.cinzaInativo));
            binding.textViewNome.setTextColor(ctx.getResources().getColor(R.color.cinzaMedio));

            binding.textView18.setTextColor(ctx.getResources().getColor(R.color.cinzaMedio));
            binding.textView19.setTextColor(ctx.getResources().getColor(R.color.cinzaMedio));
            binding.textView20.setTextColor(ctx.getResources().getColor(R.color.cinzaMedio));
            binding.textView21.setTextColor(ctx.getResources().getColor(R.color.cinzaMedio));
            binding.textView22.setTextColor(ctx.getResources().getColor(R.color.cinzaMedio));
            binding.textView23.setTextColor(ctx.getResources().getColor(R.color.cinzaMedio));
            binding.textView24.setTextColor(ctx.getResources().getColor(R.color.cinzaMedio));
            binding.textViewObservacao.setTextColor(ctx.getResources().getColor(R.color.cinzaMedio));

        } else{
            binding.cardview.setCardBackgroundColor(Color.WHITE);
            binding.textViewNome.setTextColor(ctx.getResources().getColor(R.color.cinzaEscuro));

            binding.textView18.setTextColor(ctx.getResources().getColor(R.color.azul));
            binding.textView19.setTextColor(ctx.getResources().getColor(R.color.azul));
            binding.textView20.setTextColor(ctx.getResources().getColor(R.color.azul));
            binding.textView21.setTextColor(ctx.getResources().getColor(R.color.azul));
            binding.textView22.setTextColor(ctx.getResources().getColor(R.color.azul));
            binding.textView23.setTextColor(ctx.getResources().getColor(R.color.azul));
            binding.textView24.setTextColor(ctx.getResources().getColor(R.color.azul));
            binding.textViewObservacao.setTextColor(ctx.getResources().getColor(R.color.azul));
        }

        binding.setHorario(horarioItinerario);

        if(horarioItinerario.getIdHorario().equals(proximoHorario) || horarioItinerario.getHorarioItinerario().getId().equals(proximoHorario)){
            binding.cardview.setCardBackgroundColor(ctx.getResources().getColor(R.color.azulClaro));
        } else if(!passado){
            binding.cardview.setCardBackgroundColor(Color.WHITE);
        }

        if(horarioItinerario.getHorarioItinerario() == null
                || horarioItinerario.getHorarioItinerario().getObservacao() == null
                || horarioItinerario.getHorarioItinerario().getObservacao().isEmpty()){
            binding.textViewObservacao.setVisibility(View.GONE);
        } else{
            binding.textViewObservacao.setVisibility(View.VISIBLE);
        }

        binding.executePendingBindings();
    }

    @BindingAdapter("text")
    public static void setText(TextView view, Long date) {
        String formatted = DateTimeFormat.forPattern("HH:mm").print(date);
        view.setText(formatted);
    }

}
