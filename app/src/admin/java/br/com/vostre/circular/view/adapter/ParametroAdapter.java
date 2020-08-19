package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaParametrosBinding;
import br.com.vostre.circular.model.Parametro;
import br.com.vostre.circular.view.viewHolder.ParametroViewHolder;

public class ParametroAdapter extends RecyclerView.Adapter<ParametroViewHolder> {

    public List<Parametro> parametros;
    AppCompatActivity ctx;

    public ParametroAdapter(List<Parametro> parametros, AppCompatActivity context){
        this.parametros = parametros;
        ctx = context;
    }

    @Override
    public ParametroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaParametrosBinding itemBinding =
                LinhaParametrosBinding.inflate(layoutInflater, parent, false);
        return new ParametroViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ParametroViewHolder holder, int position) {
        Parametro parametro = parametros.get(position);
        holder.bind(parametro);
    }

    @Override
    public int getItemCount() {

        if(parametros == null){
            return 0;
        } else{
            return parametros.size();
        }


    }
}
