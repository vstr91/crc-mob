package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaItinerariosBinding;
import br.com.vostre.circular.databinding.LinhaItinerariosResultadoBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.viewHolder.ItinerarioResultadoViewHolder;
import br.com.vostre.circular.view.viewHolder.ItinerarioViewHolder;

public class ItinerarioResultadoAdapter extends RecyclerView.Adapter<ItinerarioResultadoViewHolder> {

    public List<ItinerarioPartidaDestino> itinerarios;
    AppCompatActivity ctx;

    public ItinerarioResultadoAdapter(List<ItinerarioPartidaDestino> itinerarios, AppCompatActivity context){
        this.itinerarios = itinerarios;
        ctx = context;
    }

    @Override
    public ItinerarioResultadoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaItinerariosResultadoBinding itemBinding =
                LinhaItinerariosResultadoBinding.inflate(layoutInflater, parent, false);
        return new ItinerarioResultadoViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ItinerarioResultadoViewHolder holder, int position) {
        final ItinerarioPartidaDestino itinerario = this.itinerarios.get(position);

        boolean ocultaSeta = position+1 == itinerarios.size();

        holder.bind(itinerario, position+1, ocultaSeta);


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