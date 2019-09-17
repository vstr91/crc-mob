package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.databinding.LinhaServicosBinding;
import br.com.vostre.circular.model.Servico;
import br.com.vostre.circular.view.viewHolder.PaisViewHolder;
import br.com.vostre.circular.view.viewHolder.ServicoViewHolder;

public class ServicoAdapter extends RecyclerView.Adapter<ServicoViewHolder> {

    public List<Servico> servicos;
    AppCompatActivity ctx;

    public ServicoAdapter(List<Servico> servicos, AppCompatActivity context){
        this.servicos = servicos;
        ctx = context;
    }

    @Override
    public ServicoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaServicosBinding itemBinding =
                LinhaServicosBinding.inflate(layoutInflater, parent, false);
        return new ServicoViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ServicoViewHolder holder, int position) {
        Servico servico = servicos.get(position);
        holder.bind(servico);
    }

    @Override
    public int getItemCount() {

        if(servicos == null){
            return 0;
        } else{
            return servicos.size();
        }


    }
}
