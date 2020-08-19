package br.com.vostre.circular.view.adapter;

import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.File;
import java.util.List;

import br.com.vostre.circular.databinding.LinhaCidadesBinding;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.listener.SelectListener;
import br.com.vostre.circular.view.viewHolder.CidadeViewHolder;

public class CidadeAdapter extends RecyclerView.Adapter<CidadeViewHolder> implements SelectListener {

    public List<CidadeEstado> cidades;
    AppCompatActivity ctx;
    SelectListener listener;

    public SelectListener getListener() {
        return listener;
    }

    public void setListener(SelectListener listener) {
        this.listener = listener;
    }

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
        final CidadeEstado cidade = this.cidades.get(position);
        holder.bind(cidade);
        holder.setListener(this);
    }

    @Override
    public int getItemCount() {

        if(cidades == null){
            return 0;
        } else{
            return cidades.size();
        }


    }

    @Override
    public String onSelected(String id) {
        listener.onSelected(id);
        return id;
    }
}
