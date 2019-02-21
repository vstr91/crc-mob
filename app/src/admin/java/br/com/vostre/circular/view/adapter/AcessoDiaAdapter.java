package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaAcessosBinding;
import br.com.vostre.circular.databinding.LinhaAcessosDiaBinding;
import br.com.vostre.circular.model.pojo.AcessoDia;
import br.com.vostre.circular.model.pojo.AcessoTotal;
import br.com.vostre.circular.view.viewHolder.AcessoDiaViewHolder;
import br.com.vostre.circular.view.viewHolder.AcessoViewHolder;

public class AcessoDiaAdapter extends RecyclerView.Adapter<AcessoDiaViewHolder> {

    public List<AcessoDia> acessos;
    AppCompatActivity ctx;

    public AcessoDiaAdapter(List<AcessoDia> acessos, AppCompatActivity context){
        this.acessos = acessos;
        ctx = context;
    }

    @Override
    public AcessoDiaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaAcessosDiaBinding itemBinding =
                LinhaAcessosDiaBinding.inflate(layoutInflater, parent, false);
        return new AcessoDiaViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(AcessoDiaViewHolder holder, int position) {
        AcessoDia acesso = acessos.get(position);
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
