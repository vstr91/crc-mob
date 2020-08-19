package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaItinerariosTarifaBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.viewHolder.ItinerarioTarifaViewHolder;

public class ItinerarioTarifaAdapter extends RecyclerView.Adapter<ItinerarioTarifaViewHolder> {

    public List<ItinerarioPartidaDestino> itinerarios;
    AppCompatActivity ctx;

    public ItinerarioTarifaAdapter(List<ItinerarioPartidaDestino> itinerarios, AppCompatActivity context){
        this.itinerarios = itinerarios;
        ctx = context;
    }

    @Override
    public ItinerarioTarifaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaItinerariosTarifaBinding itemBinding =
                LinhaItinerariosTarifaBinding.inflate(layoutInflater, parent, false);
        return new ItinerarioTarifaViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ItinerarioTarifaViewHolder holder, int position) {
        ItinerarioPartidaDestino itinerario = itinerarios.get(position);
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
