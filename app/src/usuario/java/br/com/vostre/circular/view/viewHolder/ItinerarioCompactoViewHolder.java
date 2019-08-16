package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.text.NumberFormat;

import br.com.vostre.circular.databinding.LinhaItinerariosCompactaBinding;
import br.com.vostre.circular.databinding.LinhaItinerariosFavoritosBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.DetalheItinerarioActivity;

public class ItinerarioCompactoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaItinerariosCompactaBinding binding;
    AppCompatActivity ctx;

    public ItinerarioCompactoViewHolder(LinhaItinerariosCompactaBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ItinerarioPartidaDestino itinerario) {
        binding.setItinerario(itinerario);

        if(itinerario.getItinerario().getObservacao() != null && !itinerario.getItinerario().getObservacao().equals("")){
            binding.textViewObservacao.setText(itinerario.getItinerario().getObservacao());
            binding.textViewObservacao.setVisibility(View.VISIBLE);
        } else{
            binding.textViewObservacao.setVisibility(View.GONE);
        }

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(0);

        binding.textViewDistancia.setText("a ~"+nf.format(itinerario.getDistanciaPoi())+" metros");

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

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, DetalheItinerarioActivity.class);
                i.putExtra("itinerario", itinerario.getItinerario().getId());
                ctx.startActivity(i);
            }
        };
//
        binding.cardView2.setOnClickListener(listener);
//        binding.textViewNome.setOnClickListener(listener);

        binding.executePendingBindings();
    }

}
