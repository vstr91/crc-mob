package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaCidadesBinding;
import br.com.vostre.circular.databinding.LinhaEstadosBinding;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.viewHolder.CidadeViewHolder;
import br.com.vostre.circular.view.viewHolder.EstadoViewHolder;

public class CidadeAdapter extends RecyclerView.Adapter<CidadeViewHolder> {

    public List<CidadeEstado> cidades;
    AppCompatActivity ctx;

    public CidadeAdapter(List<CidadeEstado> cidades, AppCompatActivity context){
        this.cidades = cidades;
        ctx = context;
    }

    @Override
    public CidadeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaCidadesBinding itemBinding =
                LinhaCidadesBinding.inflate(layoutInflater, parent, false);
        return new CidadeViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(CidadeViewHolder holder, int position) {
        CidadeEstado cidade = cidades.get(position);
        holder.bind(cidade);
    }

    @Override
    public int getItemCount() {

        if(cidades == null){
            return 0;
        } else{
            return cidades.size();
        }


    }
}
