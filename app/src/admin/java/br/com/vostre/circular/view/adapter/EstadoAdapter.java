package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaEstadosBinding;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.view.viewHolder.EstadoViewHolder;

public class EstadoAdapter extends RecyclerView.Adapter<EstadoViewHolder> {

    public List<Estado> estados;
    AppCompatActivity ctx;

    public EstadoAdapter(List<Estado> estados, AppCompatActivity context){
        this.estados = estados;
        ctx = context;
    }

    @Override
    public EstadoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaEstadosBinding itemBinding =
                LinhaEstadosBinding.inflate(layoutInflater, parent, false);
        return new EstadoViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(EstadoViewHolder holder, int position) {
        Estado estado = estados.get(position);
        holder.bind(estado);
    }

    @Override
    public int getItemCount() {

        if(estados == null){
            return 0;
        } else{
            return estados.size();
        }


    }
}
