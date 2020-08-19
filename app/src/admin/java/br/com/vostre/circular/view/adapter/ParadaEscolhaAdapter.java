package br.com.vostre.circular.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.listener.ParadaListener;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.viewHolder.ParadaEscolhaViewHolder;
import br.com.vostre.circular.view.viewHolder.ParadaViewHolder;

public class ParadaEscolhaAdapter extends RecyclerView.Adapter<ParadaEscolhaViewHolder> {

    public List<ParadaBairro> paradas;
    AppCompatActivity ctx;

    ParadaListener listener;

    public ParadaListener getListener() {
        return listener;
    }

    public void setListener(ParadaListener listener) {
        this.listener = listener;
    }

    public ParadaEscolhaAdapter(List<ParadaBairro> paradas, AppCompatActivity context){
        this.paradas = paradas;
        ctx = context;
    }

    @Override
    public ParadaEscolhaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaParadasBinding itemBinding =
                LinhaParadasBinding.inflate(layoutInflater, parent, false);

        if(listener != null){
            return new ParadaEscolhaViewHolder(itemBinding, ctx, listener);
        } else{
            return new ParadaEscolhaViewHolder(itemBinding, ctx);
        }


    }

    @Override
    public void onBindViewHolder(ParadaEscolhaViewHolder holder, int position) {
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
