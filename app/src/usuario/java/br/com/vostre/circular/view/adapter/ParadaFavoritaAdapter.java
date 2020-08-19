package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaParadasFavoritasBinding;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.viewHolder.ParadaFavoritaViewHolder;
import br.com.vostre.circular.view.viewHolder.ParadaViewHolder;

public class ParadaFavoritaAdapter extends RecyclerView.Adapter<ParadaFavoritaViewHolder> {

    public List<ParadaBairro> paradas;
    AppCompatActivity ctx;
    public String bairroAtual;

    public ParadaFavoritaAdapter(List<ParadaBairro> paradas, AppCompatActivity context){
        this.paradas = paradas;
        ctx = context;
        bairroAtual = "";
    }

    @Override
    public ParadaFavoritaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaParadasFavoritasBinding itemBinding =
                LinhaParadasFavoritasBinding.inflate(layoutInflater, parent, false);
        return new ParadaFavoritaViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ParadaFavoritaViewHolder holder, int position) {
        final ParadaBairro parada = this.paradas.get(position);

        if(parada != null && !parada.getNomeBairro().equals(bairroAtual)){
            bairroAtual = parada.getNomeBairro();
            holder.bind(parada, true);
        } else{
            holder.bind(parada, false);
        }


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
