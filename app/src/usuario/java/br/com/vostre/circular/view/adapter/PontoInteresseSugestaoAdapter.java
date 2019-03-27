package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaParadasSugeridasBinding;
import br.com.vostre.circular.databinding.LinhaPoisSugeridosBinding;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;
import br.com.vostre.circular.view.viewHolder.ParadaSugestaoViewHolder;
import br.com.vostre.circular.view.viewHolder.PontoInteresseSugestaoViewHolder;

public class PontoInteresseSugestaoAdapter extends RecyclerView.Adapter<PontoInteresseSugestaoViewHolder> {

    public List<PontoInteresseSugestaoBairro> pois;
    AppCompatActivity ctx;

    public PontoInteresseSugestaoAdapter(List<PontoInteresseSugestaoBairro> pois, AppCompatActivity context){
        this.pois = pois;
        ctx = context;
    }

    @Override
    public PontoInteresseSugestaoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaPoisSugeridosBinding itemBinding =
                LinhaPoisSugeridosBinding.inflate(layoutInflater, parent, false);
        return new PontoInteresseSugestaoViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(PontoInteresseSugestaoViewHolder holder, int position) {
        PontoInteresseSugestaoBairro parada = pois.get(position);
        holder.bind(parada);
    }

    @Override
    public int getItemCount() {

        if(pois == null){
            return 0;
        } else{
            return pois.size();
        }


    }

}
