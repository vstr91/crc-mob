package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaViagensBinding;
import br.com.vostre.circular.listener.ViagemListener;
import br.com.vostre.circular.model.ViagemItinerario;
import br.com.vostre.circular.view.viewHolder.ViagemItinerarioViewHolder;

public class ViagemItinerarioAdapter extends RecyclerView.Adapter<ViagemItinerarioViewHolder> {

    public List<ViagemItinerario> viagens;
    AppCompatActivity ctx;
    ViagemListener listener;

    public ViagemItinerarioAdapter(List<ViagemItinerario> viagens, AppCompatActivity context, ViagemListener listener){
        this.viagens = viagens;
        ctx = context;
        this.listener = listener;
    }

    @Override
    public ViagemItinerarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaViagensBinding itemBinding =
                LinhaViagensBinding.inflate(layoutInflater, parent, false);

        return new ViagemItinerarioViewHolder(itemBinding, ctx, listener);


    }

    @Override
    public void onBindViewHolder(ViagemItinerarioViewHolder holder, int position) {
        ViagemItinerario viagem = viagens.get(position);

        if(viagem.getTotalPontos(ctx) > 0){
            holder.bind(viagem);
        }

    }

    @Override
    public int getItemCount() {

        if(viagens == null){
            return 0;
        } else{
            return viagens.size();
        }


    }
}
