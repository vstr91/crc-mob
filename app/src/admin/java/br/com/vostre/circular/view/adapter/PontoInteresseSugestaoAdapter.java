package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaParadasSugeridasBinding;
import br.com.vostre.circular.databinding.LinhaPoisSugeridosBinding;
import br.com.vostre.circular.listener.ParadaSugestaoListener;
import br.com.vostre.circular.listener.PontoInteresseSugestaoListener;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.PontoInteresseSugestao;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;
import br.com.vostre.circular.view.viewHolder.ParadaSugestaoViewHolder;
import br.com.vostre.circular.view.viewHolder.PontoInteresseSugestaoViewHolder;

public class PontoInteresseSugestaoAdapter extends RecyclerView.Adapter<PontoInteresseSugestaoViewHolder> implements PontoInteresseSugestaoListener {

    public List<PontoInteresseSugestaoBairro> paradas;
    AppCompatActivity ctx;
    PontoInteresseSugestaoListener listener;

    public PontoInteresseSugestaoListener getListener() {
        return listener;
    }

    public void setListener(PontoInteresseSugestaoListener listener) {
        this.listener = listener;
    }

    public PontoInteresseSugestaoAdapter(List<PontoInteresseSugestaoBairro> paradas, AppCompatActivity context){
        this.paradas = paradas;
        ctx = context;
    }

    @Override
    public PontoInteresseSugestaoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaPoisSugeridosBinding itemBinding =
                LinhaPoisSugeridosBinding.inflate(layoutInflater, parent, false);
        return new PontoInteresseSugestaoViewHolder(itemBinding, ctx, listener);
    }

    @Override
    public void onBindViewHolder(PontoInteresseSugestaoViewHolder holder, int position) {
        PontoInteresseSugestaoBairro parada = paradas.get(position);
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
    public void onSelectedPoi(String id, int acao) {
        listener.onSelectedPoi(id, acao);
    }

}
