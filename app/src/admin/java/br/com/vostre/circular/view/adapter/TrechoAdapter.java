package br.com.vostre.circular.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaEstadosBinding;
import br.com.vostre.circular.databinding.LinhaTrechosBinding;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.pojo.TrechoPartidaDestino;
import br.com.vostre.circular.view.viewHolder.EstadoViewHolder;
import br.com.vostre.circular.view.viewHolder.TrechoViewHolder;

public class TrechoAdapter extends RecyclerView.Adapter<TrechoViewHolder> {

    public List<TrechoPartidaDestino> trechos;
    AppCompatActivity ctx;

    public TrechoAdapter(List<TrechoPartidaDestino> trechos, AppCompatActivity context){
        this.trechos = trechos;
        ctx = context;
    }

    @Override
    public TrechoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaTrechosBinding itemBinding =
                LinhaTrechosBinding.inflate(layoutInflater, parent, false);
        return new TrechoViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(TrechoViewHolder holder, int position) {
        TrechoPartidaDestino trecho = trechos.get(position);
        holder.bind(trecho);
    }

    @Override
    public int getItemCount() {

        if(trechos == null){
            return 0;
        } else{
            return trechos.size();
        }


    }
}
