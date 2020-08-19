package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.listener.ParadaListener;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.viewHolder.ParadaViewHolder;

public class ParadaAdapter extends RecyclerView.Adapter<ParadaViewHolder> {

    public List<ParadaBairro> paradas;
    AppCompatActivity ctx;

    ParadaListener listener;

    public ParadaListener getListener() {
        return listener;
    }

    public void setListener(ParadaListener listener) {
        this.listener = listener;
    }

    public ParadaAdapter(List<ParadaBairro> paradas, AppCompatActivity context){
        this.paradas = paradas;
        ctx = context;
    }

    @Override
    public ParadaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaParadasBinding itemBinding =
                LinhaParadasBinding.inflate(layoutInflater, parent, false);

        if(listener != null){
            return new ParadaViewHolder(itemBinding, ctx, listener);
        } else{
            return new ParadaViewHolder(itemBinding, ctx);
        }


    }

    @Override
    public void onBindViewHolder(ParadaViewHolder holder, int position) {
        ParadaBairro parada = paradas.get(position);
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
}
