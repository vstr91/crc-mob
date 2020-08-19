package br.com.vostre.circular.view.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaLegendaCompartilhamentoBinding;
import br.com.vostre.circular.model.pojo.Legenda;
import br.com.vostre.circular.view.listener.LegendaListener;
import br.com.vostre.circular.view.viewHolder.LegendaCompartilhamentoViewHolder;
import br.com.vostre.circular.view.viewHolder.LegendaViewHolder;

public class LegendaCompartilhamentoAdapter extends RecyclerView.Adapter<LegendaCompartilhamentoViewHolder> {

    public List<Legenda> dados;
    Context ctx;
    LegendaListener listener;

    public LegendaListener getListener() {
        return listener;
    }

    public void setListener(LegendaListener listener) {
        this.listener = listener;
    }

    public LegendaCompartilhamentoAdapter(List<Legenda> dados, Context context){
        this.dados = dados;
        ctx = context;
    }

    @Override
    public LegendaCompartilhamentoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaLegendaCompartilhamentoBinding itemBinding =
                LinhaLegendaCompartilhamentoBinding.inflate(layoutInflater, parent, false);
        return new LegendaCompartilhamentoViewHolder(itemBinding, ctx, listener);
    }

    @Override
    public void onBindViewHolder(LegendaCompartilhamentoViewHolder holder, int position) {
        Legenda legenda = dados.get(position);
        holder.bind(legenda);
    }

    @Override
    public int getItemCount() {

        if(dados == null){
            return 0;
        } else{
            return dados.size();
        }


    }

}
