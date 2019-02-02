package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaCidadesBinding;
import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.listener.SelectListener;
import br.com.vostre.circular.view.viewHolder.CidadeViewHolder;
import br.com.vostre.circular.view.viewHolder.ParadaViewHolder;

public class ParadaAdapter extends RecyclerView.Adapter<ParadaViewHolder> {

    public List<ParadaBairro> paradas;
    AppCompatActivity ctx;
    public String bairroAtual;

    public ParadaAdapter(List<ParadaBairro> paradas, AppCompatActivity context){
        this.paradas = paradas;
        ctx = context;
        bairroAtual = "";
    }

    @Override
    public ParadaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaParadasBinding itemBinding =
                LinhaParadasBinding.inflate(layoutInflater, parent, false);
        return new ParadaViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ParadaViewHolder holder, int position) {
        int i = position-1;
        ParadaBairro paradaAnterior = null;

        if(i >= 0){
            paradaAnterior = this.paradas.get(i);
        }

        final ParadaBairro parada = this.paradas.get(position);

        if(paradaAnterior != null){
            holder.bind(parada, paradaAnterior.getNomeBairro());
        } else{
            holder.bind(parada, "");
        }


        //bairroAtual = parada.getNomeBairro();


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
