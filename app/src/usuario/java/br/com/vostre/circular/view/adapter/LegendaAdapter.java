package br.com.vostre.circular.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaBairrosBinding;
import br.com.vostre.circular.databinding.LinhaLegendaBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.Legenda;
import br.com.vostre.circular.view.listener.LegendaListener;
import br.com.vostre.circular.view.listener.SelectListener;
import br.com.vostre.circular.view.viewHolder.BairroViewHolder;
import br.com.vostre.circular.view.viewHolder.LegendaViewHolder;

public class LegendaAdapter extends RecyclerView.Adapter<LegendaViewHolder> {

    public List<Legenda> dados;
    Context ctx;
    LegendaListener listener;

    public LegendaListener getListener() {
        return listener;
    }

    public void setListener(LegendaListener listener) {
        this.listener = listener;
    }

    public LegendaAdapter(List<Legenda> dados, Context context){
        this.dados = dados;
        ctx = context;
    }

    @Override
    public LegendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaLegendaBinding itemBinding =
                LinhaLegendaBinding.inflate(layoutInflater, parent, false);
        return new LegendaViewHolder(itemBinding, ctx, listener);
    }

    @Override
    public void onBindViewHolder(LegendaViewHolder holder, int position) {
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
