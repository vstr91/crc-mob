package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.databinding.LinhaSecoesBinding;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.view.viewHolder.PaisViewHolder;
import br.com.vostre.circular.view.viewHolder.SecaoItinerarioViewHolder;

public class SecaoItinerarioAdapter extends RecyclerView.Adapter<SecaoItinerarioViewHolder> {

    public List<SecaoItinerario> secoes;
    AppCompatActivity ctx;

    public SecaoItinerarioAdapter(List<SecaoItinerario> secoes, AppCompatActivity context){
        this.secoes = secoes;
        ctx = context;
    }

    @Override
    public SecaoItinerarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaSecoesBinding itemBinding =
                LinhaSecoesBinding.inflate(layoutInflater, parent, false);
        return new SecaoItinerarioViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(SecaoItinerarioViewHolder holder, int position) {
        SecaoItinerario secao = secoes.get(position);
        holder.bind(secao);
    }

    @Override
    public int getItemCount() {

        if(secoes == null){
            return 0;
        } else{
            return secoes.size();
        }


    }
}
