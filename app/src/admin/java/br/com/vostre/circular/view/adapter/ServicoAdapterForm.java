package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaServicosFormBinding;
import br.com.vostre.circular.model.Servico;
import br.com.vostre.circular.view.viewHolder.ServicoFormViewHolder;

public class ServicoAdapterForm extends RecyclerView.Adapter<ServicoFormViewHolder> {

    public List<Servico> servicos;
    AppCompatActivity ctx;

    public ServicoAdapterForm(List<Servico> servicos, AppCompatActivity context){
        this.servicos = servicos;
        ctx = context;
    }

    @Override
    public ServicoFormViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaServicosFormBinding itemBinding =
                LinhaServicosFormBinding.inflate(layoutInflater, parent, false);
        return new ServicoFormViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ServicoFormViewHolder holder, int position) {
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
