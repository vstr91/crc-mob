package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaItinerariosResultadoBinding;
import br.com.vostre.circular.listener.ParadaItinerarioListener;
import br.com.vostre.circular.listener.ParadaListener;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.BaseActivity;
import br.com.vostre.circular.view.viewHolder.ItinerarioResultadoViewHolder;
import br.com.vostre.circular.view.viewHolder.ItinerarioViewHolder;

public class ItinerarioResultadoAdapter extends RecyclerView.Adapter<ItinerarioResultadoViewHolder> {

    public List<ItinerarioPartidaDestino> itinerarios;
    AppCompatActivity ctx;
    String dia;
    String hora;
    ParadaItinerarioListener listener;

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public ItinerarioResultadoAdapter(List<ItinerarioPartidaDestino> itinerarios, AppCompatActivity context, String dia, String hora, ParadaItinerarioListener listener){
        this.itinerarios = itinerarios;
        ctx = context;
        this.dia = dia;
        this.hora = hora;
        this.listener = listener;
    }

    @Override
    public ItinerarioResultadoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaItinerariosResultadoBinding itemBinding =
                LinhaItinerariosResultadoBinding.inflate(layoutInflater, parent, false);
        return new ItinerarioResultadoViewHolder(itemBinding, ctx, (BaseActivity) ctx, listener);
    }

    @Override
    public void onBindViewHolder(ItinerarioResultadoViewHolder holder, int position) {
        final ItinerarioPartidaDestino itinerario = this.itinerarios.get(position);
        ItinerarioPartidaDestino itinerarioSeguinte = null;

        if(this.itinerarios.size() > position+1){
            itinerarioSeguinte = this.itinerarios.get(position+1);
        }

        final ItinerarioPartidaDestino iti = itinerarioSeguinte;

        boolean ocultaSeta = position+1 == itinerarios.size();

        holder.bind(itinerario, position+1, ocultaSeta, itinerario.getDia(),
                itinerario.getHora(), itinerarios.size(), iti);


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
