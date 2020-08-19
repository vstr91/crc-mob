package br.com.vostre.circular.view.adapter;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaBairrosBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.listener.SelectListener;
import br.com.vostre.circular.view.viewHolder.BairroViewHolder;
import br.com.vostre.circular.view.viewHolder.CidadeViewHolder;

public class BairroAdapter extends RecyclerView.Adapter<BairroViewHolder> implements SelectListener {

    public List<BairroCidade> bairros;
    Context ctx;
    SelectListener listener;

    public SelectListener getListener() {
        return listener;
    }

    public void setListener(SelectListener listener) {
        this.listener = listener;
    }

    public BairroAdapter(List<BairroCidade> bairros, Context context){
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
        holder.setListener(this);
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

    @Override
    public String onSelected(String id) {
        return listener.onSelected(id);
    }
}
