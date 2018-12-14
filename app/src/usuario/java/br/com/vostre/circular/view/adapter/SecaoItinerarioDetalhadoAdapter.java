package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaSecoesDetalhadaBinding;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.SecaoItinerarioParada;
import br.com.vostre.circular.view.viewHolder.SecaoItinerarioDetalhadoViewHolder;
import br.com.vostre.circular.view.viewHolder.SecaoItinerarioViewHolder;

public class SecaoItinerarioDetalhadoAdapter extends RecyclerView.Adapter<SecaoItinerarioDetalhadoViewHolder> {

    public List<SecaoItinerarioParada> secoes;
    AppCompatActivity ctx;

    public SecaoItinerarioDetalhadoAdapter(List<SecaoItinerarioParada> secoes, AppCompatActivity context){
        this.secoes = secoes;
        ctx = context;
    }

    @Override
    public SecaoItinerarioDetalhadoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaSecoesDetalhadaBinding itemBinding =
                LinhaSecoesDetalhadaBinding.inflate(layoutInflater, parent, false);
        return new SecaoItinerarioDetalhadoViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(SecaoItinerarioDetalhadoViewHolder holder, int position) {
        SecaoItinerarioParada secao = secoes.get(position);
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
