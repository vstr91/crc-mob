package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaItinerariosBinding;
import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.DetalheItinerarioActivity;
import br.com.vostre.circular.view.DetalheParadaActivity;

public class ItinerarioViewHolder extends RecyclerView.ViewHolder {

    private final LinhaItinerariosBinding binding;
    AppCompatActivity ctx;

    public ItinerarioViewHolder(LinhaItinerariosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ItinerarioPartidaDestino itinerario) {
        binding.setItinerario(itinerario);

//        if(!itinerario.getItinerario().getAcessivel()){
//            binding.imageView12.setVisibility(View.GONE);
//        }

        if(itinerario.getItinerario().getObservacao() == null || (itinerario.getItinerario().getObservacao().isEmpty() ||
                itinerario.getItinerario().getObservacao().equals("null") || itinerario.getItinerario().getObservacao().equals(""))){
            binding.textView24.setVisibility(View.GONE);
        } else{
            binding.textView24.setVisibility(View.VISIBLE);
        }

        if(itinerario.getTempoAcumulado() == null || (itinerario.getTempoAcumulado().getHourOfDay() == 0 && itinerario.getTempoAcumulado().getMinuteOfHour() == 0)){
            binding.textViewEstimativa.setVisibility(View.GONE);
        } else{
            binding.textViewEstimativa.setVisibility(View.VISIBLE);
        }

        binding.setDestaca(false);
        binding.cardView2.setCardBackgroundColor(ctx.getResources().getColor(R.color.branco));

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, DetalheItinerarioActivity.class);
                i.putExtra("itinerario", itinerario.getItinerario().getId());
                i.putExtra("horario", itinerario.getIdProximoHorario());
                ctx.startActivity(i);
            }
        };
//
        binding.cardView2.setOnClickListener(listener);
//        binding.textViewNome.setOnClickListener(listener);

        binding.executePendingBindings();
    }

    public void bind(final ItinerarioPartidaDestino itinerario, boolean destaca) {
        binding.setItinerario(itinerario);

        if(itinerario.getItinerario().getObservacao() == null || (itinerario.getItinerario().getObservacao().isEmpty() ||
                itinerario.getItinerario().getObservacao().equals("null") || itinerario.getItinerario().getObservacao().equals(""))){
            binding.textView24.setVisibility(View.GONE);
        } else{
            binding.textView24.setVisibility(View.VISIBLE);
        }

        if(itinerario.getTempoAcumulado() == null || (itinerario.getTempoAcumulado().getHourOfDay() == 0 && itinerario.getTempoAcumulado().getMinuteOfHour() == 0)){
            binding.textViewEstimativa.setVisibility(View.GONE);
        } else{
            binding.textViewEstimativa.setVisibility(View.VISIBLE);
        }

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, DetalheItinerarioActivity.class);
                i.putExtra("itinerario", itinerario.getItinerario().getId());
                i.putExtra("horario", itinerario.getIdProximoHorario());
                ctx.startActivity(i);
            }
        };
//
        binding.cardView2.setOnClickListener(listener);
//        binding.textViewNome.setOnClickListener(listener);
        binding.setDestaca(destaca);

        if(destaca){
            binding.cardView2.setCardBackgroundColor(ctx.getResources().getColor(R.color.cianoClaro));
//            binding.textView12.setTextColor(ctx.getResources().getColor(R.color.branco));
//            binding.textView15.setTextColor(ctx.getResources().getColor(R.color.branco));
//            binding.textView18.setTextColor(ctx.getResources().getColor(R.color.branco));
//            binding.textView19.setTextColor(ctx.getResources().getColor(R.color.branco));
        } else{
            binding.cardView2.setCardBackgroundColor(ctx.getResources().getColor(R.color.branco));
//            binding.textView12.setTextColor(ctx.getResources().getColor(R.color.cinzaEscuro));
//            binding.textView15.setTextColor(ctx.getResources().getColor(R.color.secondary_text_material_light));
//            binding.textView18.setTextColor(ctx.getResources().getColor(R.color.cinzaEscuro));
//            binding.textView19.setTextColor(ctx.getResources().getColor(R.color.secondary_text_material_light));
        }

        binding.executePendingBindings();
    }

}
