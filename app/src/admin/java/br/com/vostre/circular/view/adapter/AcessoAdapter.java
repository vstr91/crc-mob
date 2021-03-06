package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaAcessosBinding;
import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.pojo.AcessoTotal;
import br.com.vostre.circular.view.viewHolder.AcessoViewHolder;
import br.com.vostre.circular.view.viewHolder.PaisViewHolder;

public class AcessoAdapter extends RecyclerView.Adapter<AcessoViewHolder> {

    public List<AcessoTotal> acessos;
    AppCompatActivity ctx;
    String dia;

    public AcessoAdapter(List<AcessoTotal> acessos, AppCompatActivity context, String dia){
        this.acessos = acessos;
        ctx = context;
        this.dia = dia;
    }

    @Override
    public AcessoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaAcessosBinding itemBinding =
                LinhaAcessosBinding.inflate(layoutInflater, parent, false);
        return new AcessoViewHolder(itemBinding, ctx, dia);
    }

    @Override
    public void onBindViewHolder(AcessoViewHolder holder, int position) {
        AcessoTotal acesso = acessos.get(position);
        holder.bind(acesso);
    }

    @Override
    public int getItemCount() {

        if(acessos == null){
            return 0;
        } else{
            return acessos.size();
        }


    }
}
