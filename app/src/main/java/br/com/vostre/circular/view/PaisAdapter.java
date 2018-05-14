package br.com.vostre.circular.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.viewHolder.PaisViewHolder;

public class PaisAdapter extends RecyclerView.Adapter<PaisViewHolder> {

    List<Pais> paises;

    public PaisAdapter(List<Pais> paises){
        this.paises = paises;
    }

    @Override
    public PaisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaPaisesBinding itemBinding =
                LinhaPaisesBinding.inflate(layoutInflater, parent, false);
        return new PaisViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(PaisViewHolder holder, int position) {
        Pais pais = paises.get(position);
        holder.bind(pais);
    }

    @Override
    public int getItemCount() {

        if(paises == null){
            return 0;
        } else{
            return paises.size();
        }


    }
}
