package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaItinerariosCompactaBinding;
import br.com.vostre.circular.databinding.LinhaItinerariosFavoritosBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.viewHolder.ItinerarioCompactoViewHolder;
import br.com.vostre.circular.view.viewHolder.ItinerarioFavoritoViewHolder;

public class ItinerarioCompactoAdapter extends RecyclerView.Adapter<ItinerarioCompactoViewHolder> {

    public List<ItinerarioPartidaDestino> itinerarios;
    AppCompatActivity ctx;

    public ItinerarioCompactoAdapter(List<ItinerarioPartidaDestino> itinerarios, AppCompatActivity context){
        this.itinerarios = itinerarios;
        ctx = context;
    }

    @Override
    public ItinerarioCompactoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaItinerariosCompactaBinding itemBinding =
                LinhaItinerariosCompactaBinding.inflate(layoutInflater, parent, false);
        return new ItinerarioCompactoViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ItinerarioCompactoViewHolder holder, int position) {
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
