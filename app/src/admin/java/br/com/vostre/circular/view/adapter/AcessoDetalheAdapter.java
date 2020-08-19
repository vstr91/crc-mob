package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaAcessosDetalheBinding;
import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.view.viewHolder.AcessoDetalheViewHolder;

public class AcessoDetalheAdapter extends RecyclerView.Adapter<AcessoDetalheViewHolder> {

    public List<Acesso> acessos;
    AppCompatActivity ctx;

    public AcessoDetalheAdapter(List<Acesso> acessos, AppCompatActivity context){
        this.acessos = acessos;
        ctx = context;
    }

    @Override
    public AcessoDetalheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaAcessosDetalheBinding itemBinding =
                LinhaAcessosDetalheBinding.inflate(layoutInflater, parent, false);
        return new AcessoDetalheViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(AcessoDetalheViewHolder holder, int position) {
        Acesso acesso = acessos.get(position);
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
