package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaItinerariosBinding;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.viewHolder.ItinerarioViewHolder;
import br.com.vostre.circular.view.viewHolder.ParadaViewHolder;

public class ItinerarioAdapter extends RecyclerView.Adapter<ItinerarioViewHolder> {

    public List<ItinerarioPartidaDestino> itinerarios;
    AppCompatActivity ctx;
    public boolean destaca = false;

    public ItinerarioAdapter(List<ItinerarioPartidaDestino> itinerarios, AppCompatActivity context){
        this.itinerarios = itinerarios;
        ctx = context;
    }

    public ItinerarioAdapter(List<ItinerarioPartidaDestino> itinerarios, AppCompatActivity context, boolean destaca){
        this.itinerarios = itinerarios;
        ctx = context;
        this.destaca = destaca;
    }

    @Override
    public ItinerarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaItinerariosBinding itemBinding =
                LinhaItinerariosBinding.inflate(layoutInflater, parent, false);
        return new ItinerarioViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ItinerarioViewHolder holder, int position) {

        final ItinerarioPartidaDestino itinerario = this.itinerarios.get(position);

        if(destaca && position == 0){
            holder.bind(itinerario, destaca);
        } else{
            holder.bind(itinerario);
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
