package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

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
        }

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
                i.putExtra("horario", itinerario.getIdProximoHorario());
                ctx.startActivity(i);
            }
        };
//
        binding.cardView2.setOnClickListener(listener);
//        binding.textViewNome.setOnClickListener(listener);

        binding.executePendingBindings();
    }

}
