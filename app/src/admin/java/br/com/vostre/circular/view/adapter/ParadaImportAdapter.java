package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaParadasImportBinding;
import br.com.vostre.circular.listener.ParadaImportListener;
import br.com.vostre.circular.model.pojo.ParadaBairroImport;
import br.com.vostre.circular.view.viewHolder.ParadaImportViewHolder;

public class ParadaImportAdapter extends RecyclerView.Adapter<ParadaImportViewHolder> {

    public List<ParadaBairroImport> paradas;
    AppCompatActivity ctx;

    ParadaImportListener listener;

    public ParadaImportListener getListener() {
        return listener;
    }

    public void setListener(ParadaImportListener listener) {
        this.listener = listener;
    }

    public ParadaImportAdapter(List<ParadaBairroImport> paradas, AppCompatActivity context){
        this.paradas = paradas;
        ctx = context;
    }

    @Override
    public ParadaImportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaParadasImportBinding itemBinding =
                LinhaParadasImportBinding.inflate(layoutInflater, parent, false);

        if(listener != null){
            return new ParadaImportViewHolder(itemBinding, ctx, listener);
        } else{
            return new ParadaImportViewHolder(itemBinding, ctx);
        }


    }

    @Override
    public void onBindViewHolder(ParadaImportViewHolder holder, int position) {
        ParadaBairroImport parada = paradas.get(position);
        holder.bind(parada);
    }

    @Override
    public int getItemCount() {

        if(paradas == null){
            return 0;
        } else{
            return paradas.size();
        }


    }
}
