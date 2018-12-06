package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.databinding.LinhaParadasSugeridasBinding;
import br.com.vostre.circular.listener.ParadaSugestaoListener;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.view.viewHolder.ParadaSugestaoViewHolder;
import br.com.vostre.circular.view.viewHolder.ParadaViewHolder;

public class ParadaSugestaoAdapter extends RecyclerView.Adapter<ParadaSugestaoViewHolder> implements ParadaSugestaoListener {

    public List<ParadaSugestaoBairro> paradas;
    AppCompatActivity ctx;
    ParadaSugestaoListener listener;

    public ParadaSugestaoListener getListener() {
        return listener;
    }

    public void setListener(ParadaSugestaoListener listener) {
        this.listener = listener;
    }

    public ParadaSugestaoAdapter(List<ParadaSugestaoBairro> paradas, AppCompatActivity context){
        this.paradas = paradas;
        ctx = context;
    }

    @Override
    public ParadaSugestaoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaParadasSugeridasBinding itemBinding =
                LinhaParadasSugeridasBinding.inflate(layoutInflater, parent, false);
        return new ParadaSugestaoViewHolder(itemBinding, ctx, listener);
    }

    @Override
    public void onBindViewHolder(ParadaSugestaoViewHolder holder, int position) {
        ParadaSugestaoBairro parada = paradas.get(position);
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
    public void onSelected(String id, int acao) {
        listener.onSelected(id, acao);
    }
}
