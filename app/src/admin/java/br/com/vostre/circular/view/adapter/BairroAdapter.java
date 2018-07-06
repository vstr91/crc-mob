package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaBairrosBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;

public class BairroAdapter extends RecyclerView.Adapter<BairroViewHolder> {

    public List<BairroCidade> bairros;
    AppCompatActivity ctx;

    public BairroAdapter(List<BairroCidade> bairros, AppCompatActivity context){
        this.bairros = bairros;
        ctx = context;
    }

    @Override
    public BairroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaBairrosBinding itemBinding =
                LinhaBairrosBinding.inflate(layoutInflater, parent, false);
        return new BairroViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(BairroViewHolder holder, int position) {
        BairroCidade bairro = bairros.get(position);
        holder.bind(bairro);
    }

    @Override
    public int getItemCount() {

        if(bairros == null){
            return 0;
        } else{
            return bairros.size();
        }


    }
}
