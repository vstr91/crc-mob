package br.com.vostre.circular.view.adapter;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import br.com.vostre.circular.databinding.LinhaParadasItinerariosBinding;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.view.viewHolder.ParadaItinerarioViewHolder;

public class ParadaItinerarioAdapter extends RecyclerView.Adapter<ParadaItinerarioViewHolder> implements ItemTouchHelperAdapter {

    public List<ParadaItinerarioBairro> paradas;
    AppCompatActivity ctx;
    public Boolean edicaoItinerario = false;

    public ParadaItinerarioAdapter(List<ParadaItinerarioBairro> paradas, AppCompatActivity context,
                                   @Nullable boolean edicaoItinerario){
        this.paradas = paradas;
        ctx = context;
        this.edicaoItinerario = edicaoItinerario;
    }

    @Override
    public ParadaItinerarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaParadasItinerariosBinding itemBinding =
                LinhaParadasItinerariosBinding.inflate(layoutInflater, parent, false);

        return new ParadaItinerarioViewHolder(itemBinding, ctx, this.edicaoItinerario);
    }

    @Override
    public void onBindViewHolder(ParadaItinerarioViewHolder holder, int position) {
        ParadaItinerarioBairro parada = paradas.get(position);
        holder.bind(parada);
    }

    @Override
    public int getItemCount() {

        if(paradas == null){
            return 0;
        } else{
            return paradas.size();
        }


    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(paradas, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(paradas, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onItemDismiss(int position) {
        paradas.remove(position);
        notifyItemRemoved(position);
    }
}
