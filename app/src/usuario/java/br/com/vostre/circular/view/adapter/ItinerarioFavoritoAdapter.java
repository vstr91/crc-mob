package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaItinerariosBinding;
import br.com.vostre.circular.databinding.LinhaItinerariosFavoritosBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.viewHolder.ItinerarioFavoritoViewHolder;
import br.com.vostre.circular.view.viewHolder.ItinerarioViewHolder;

public class ItinerarioFavoritoAdapter extends RecyclerView.Adapter<ItinerarioFavoritoViewHolder> {

    public List<ItinerarioPartidaDestino> itinerarios;
    AppCompatActivity ctx;

    public ItinerarioFavoritoAdapter(List<ItinerarioPartidaDestino> itinerarios, AppCompatActivity context){
        this.itinerarios = itinerarios;
        ctx = context;
    }

    @Override
    public ItinerarioFavoritoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaItinerariosFavoritosBinding itemBinding =
                LinhaItinerariosFavoritosBinding.inflate(layoutInflater, parent, false);
        return new ItinerarioFavoritoViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ItinerarioFavoritoViewHolder holder, int position) {
        final ItinerarioPartidaDestino itinerario = this.itinerarios.get(position);

        holder.bind(itinerario);


    }

    @Override
    public int getItemCount() {

        if(itinerarios == null){
            return 0;
        } else{
            return itinerarios.size();
        }


    }


}
