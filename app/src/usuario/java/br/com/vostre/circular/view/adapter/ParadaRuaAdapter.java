package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.databinding.LinhaRuasBinding;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.viewHolder.ParadaRuaViewHolder;
import br.com.vostre.circular.view.viewHolder.ParadaViewHolder;

public class ParadaRuaAdapter extends RecyclerView.Adapter<ParadaRuaViewHolder> {

    public List<ParadaBairro> paradas;
    AppCompatActivity ctx;

    public ParadaRuaAdapter(List<ParadaBairro> paradas, AppCompatActivity context){
        this.paradas = paradas;
        ctx = context;
    }

    @Override
    public ParadaRuaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaRuasBinding itemBinding =
                LinhaRuasBinding.inflate(layoutInflater, parent, false);
        return new ParadaRuaViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ParadaRuaViewHolder holder, int position) {

        ParadaBairro p = paradas.get(position);

        if(position == 0){
            holder.bind(p, position);
        } else if(position+1 == paradas.size()){
            holder.bind(p, 1);
        } else{
            holder.bind(p, 2);
        }

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
