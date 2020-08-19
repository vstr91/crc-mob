package br.com.vostre.circular.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.databinding.LinhaItinerariosBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.viewHolder.ItinerarioEscolhaViewHolder;
import br.com.vostre.circular.view.viewHolder.ItinerarioViewHolder;

public class ItinerarioEscolhaAdapter extends RecyclerView.Adapter<ItinerarioEscolhaViewHolder> implements Filterable {

    public List<ItinerarioPartidaDestino> itinerarios;
    public List<ItinerarioPartidaDestino> itinerariosOriginal;
    public List<ItinerarioPartidaDestino> listaFiltrada;
    AppCompatActivity ctx;
    static int FLAG;

    public ItinerarioEscolhaAdapter(List<ItinerarioPartidaDestino> itinerarios, AppCompatActivity context){
        this.itinerarios = itinerarios;
        this.itinerariosOriginal = itinerarios;
        ctx = context;
    }

    @Override
    public ItinerarioEscolhaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaItinerariosBinding itemBinding =
                LinhaItinerariosBinding.inflate(layoutInflater, parent, false);
        return new ItinerarioEscolhaViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ItinerarioEscolhaViewHolder holder, int position) {
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
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                if(charString.isEmpty()){
                    listaFiltrada = itinerariosOriginal;
                } else{
                    listaFiltrada = new ArrayList<>();

                    for(ItinerarioPartidaDestino i : itinerariosOriginal){

                        if(i.getNomeCidadePartida().toLowerCase().contains(charString.toLowerCase()) ||
                                i.getNomeCidadeDestino().toLowerCase().contains(charString.toLowerCase()) ||
                                (i.getItinerario().getSigla() != null && i.getItinerario().getSigla().toLowerCase().contains(charString.toLowerCase()))){
                            listaFiltrada.add(i);
                        }

                    }

                }

                FilterResults results = new FilterResults();
                results.values = listaFiltrada;
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                List iti = (ArrayList<ItinerarioPartidaDestino>) results.values;

//                if(cid.size() > 0){
                itinerarios = iti;
//                } else{
//                    cidades = cidadesOriginal;
//                }

                notifyDataSetChanged();
            }
        };
    }

}
