package br.com.vostre.circular.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaPoisBinding;
import br.com.vostre.circular.databinding.LinhaPoisSugeridosBinding;
import br.com.vostre.circular.listener.PontoInteresseSugestaoListener;
import br.com.vostre.circular.model.pojo.PontoInteresseBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;
import br.com.vostre.circular.view.viewHolder.PontoInteresseEscolhaViewHolder;
import br.com.vostre.circular.view.viewHolder.PontoInteresseSugestaoViewHolder;

public class PontoInteresseEscolhaAdapter extends RecyclerView.Adapter<PontoInteresseEscolhaViewHolder> implements PontoInteresseSugestaoListener {

    public List<PontoInteresseBairro> pois;
    AppCompatActivity ctx;
    PontoInteresseSugestaoListener listener;

    public PontoInteresseSugestaoListener getListener() {
        return listener;
    }

    public void setListener(PontoInteresseSugestaoListener listener) {
        this.listener = listener;
    }

    public PontoInteresseEscolhaAdapter(List<PontoInteresseBairro> paradas, AppCompatActivity context){
        this.pois = paradas;
        ctx = context;
    }

    @Override
    public PontoInteresseEscolhaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaPoisBinding itemBinding =
                LinhaPoisBinding.inflate(layoutInflater, parent, false);
        return new PontoInteresseEscolhaViewHolder(itemBinding, ctx, listener);
    }

    @Override
    public void onBindViewHolder(PontoInteresseEscolhaViewHolder holder, int position) {
        PontoInteresseBairro parada = pois.get(position);
        holder.bind(parada);
    }

    @Override
    public int getItemCount() {

        if(pois == null){
            return 0;
        } else{
            return pois.size();
        }


    }

    @Override
    public void onSelectedPoi(String id, int acao) {
        listener.onSelectedPoi(id, acao);
    }

}
