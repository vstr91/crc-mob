package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import org.joda.time.format.DateTimeFormat;

import java.text.NumberFormat;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaItinerariosCompactaBinding;
import br.com.vostre.circular.databinding.LinhaItinerariosInfosBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.DetalheItinerarioActivity;

public class ItinerarioInfoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaItinerariosInfosBinding binding;
    AppCompatActivity ctx;

    public ItinerarioInfoViewHolder(LinhaItinerariosInfosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ItinerarioPartidaDestino itinerario, int cor) {
        binding.setItinerario(itinerario);

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);

        NumberFormat nfDinheiro = NumberFormat.getCurrencyInstance();
        nfDinheiro.setMaximumFractionDigits(2);

        binding.textViewTarifa.setText(nfDinheiro.format(itinerario.getItinerario().getTarifa()));

        if(itinerario.getItinerario().getDistanciaMetros() != null){
            binding.textViewDistancia.setText(nf.format(itinerario.getItinerario().getDistanciaMetros()/1000)+" Km");
        } else{
            binding.textViewDistancia.setText(nf.format(itinerario.getItinerario().getDistancia())+" Km");
        }


        binding.textViewTempo.setText(DateTimeFormat.forPattern("HH:mm").print(itinerario.getItinerario().getTempo()));

        if(cor != -1){
            binding.setCor(cor);
            binding.imageView22.setVisibility(View.VISIBLE);
        } else{
            binding.setCor(R.color.branco);
            binding.imageView22.setVisibility(View.GONE);
        }



//        if(!itinerario.getItinerario().getAcessivel()){
//            binding.imageView12.setVisibility(View.GONE);
//        }

        //binding.circleView2.setImagem(null);

//        final File brasao = new File(ctx.getApplicationContext().getFilesDir(),  cidade.getCidade().getBrasao());
//
//        if(brasao.exists() && brasao.canRead()){
//            final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
//            binding.circleView2.setImagem(drawable);
//        }
//
//        binding.textViewNome.setText(cidade.getCidade().getNome());

//        final View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(ctx, DetalheItinerarioActivity.class);
//                i.putExtra("itinerario", itinerario.getItinerario().getId());
//                ctx.startActivity(i);
//            }
//        };
//
//        binding.cardView2.setOnClickListener(listener);
//        binding.textViewNome.setOnClickListener(listener);

        // ALIAS ITINERARIO

        if(itinerario.getItinerario().getAliasBairroPartida() != null && !itinerario.getItinerario().getAliasBairroPartida().isEmpty()){
            itinerario.setNomeBairroPartida(itinerario.getItinerario().getAliasBairroPartida());
        }

        if(itinerario.getItinerario().getAliasCidadePartida() != null && !itinerario.getItinerario().getAliasCidadePartida().isEmpty()){
            itinerario.setNomeCidadePartida(itinerario.getItinerario().getAliasCidadePartida());
        }

        if(itinerario.getItinerario().getAliasBairroDestino() != null && !itinerario.getItinerario().getAliasBairroDestino().isEmpty()){
            itinerario.setNomeBairroDestino(itinerario.getItinerario().getAliasBairroDestino());
        }

        if(itinerario.getItinerario().getAliasCidadeDestino() != null && !itinerario.getItinerario().getAliasCidadeDestino().isEmpty()){
            itinerario.setNomeCidadeDestino(itinerario.getItinerario().getAliasCidadeDestino());
        }

        // FIM ALIAS

        binding.executePendingBindings();
    }

}
