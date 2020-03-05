package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaItinerariosCompactaBinding;
import br.com.vostre.circular.databinding.LinhaItinerariosInfosBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.viewHolder.ItinerarioCompactoViewHolder;
import br.com.vostre.circular.view.viewHolder.ItinerarioInfoViewHolder;

public class ItinerarioInfoAdapter extends RecyclerView.Adapter<ItinerarioInfoViewHolder> {

    public List<ItinerarioPartidaDestino> itinerarios;
    AppCompatActivity ctx;
    int[] cores;

    public ItinerarioInfoAdapter(List<ItinerarioPartidaDestino> itinerarios, AppCompatActivity context, int[] cores){
        this.itinerarios = itinerarios;
        ctx = context;
        this.cores = cores;
    }

    @Override
    public ItinerarioInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaItinerariosInfosBinding itemBinding =
                LinhaItinerariosInfosBinding.inflate(layoutInflater, parent, false);
        return new ItinerarioInfoViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ItinerarioInfoViewHolder holder, int position) {
        final ItinerarioPartidaDestino itinerario = this.itinerarios.get(position);

        if(cores != null){
            holder.bind(itinerario, cores[position]);
        } else{
            holder.bind(itinerario, -1);
        }




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
