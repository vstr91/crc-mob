package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaItinerariosTarifaBinding;
import br.com.vostre.circular.databinding.LinhaTarifasItinerariosBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.viewHolder.ItinerarioTarifaViewHolder;
import br.com.vostre.circular.view.viewHolder.TarifaItinerarioViewHolder;

public class TarifaItinerarioAdapter extends RecyclerView.Adapter<TarifaItinerarioViewHolder> {

    public List<ItinerarioPartidaDestino> itinerarios;
    AppCompatActivity ctx;

    public TarifaItinerarioAdapter(List<ItinerarioPartidaDestino> itinerarios, AppCompatActivity context){
        this.itinerarios = itinerarios;
        ctx = context;
    }

    @Override
    public TarifaItinerarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaTarifasItinerariosBinding itemBinding =
                LinhaTarifasItinerariosBinding.inflate(layoutInflater, parent, false);
        return new TarifaItinerarioViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(TarifaItinerarioViewHolder holder, int position) {
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
