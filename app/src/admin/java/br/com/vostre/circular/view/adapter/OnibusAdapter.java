package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaOnibusBinding;
import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.viewHolder.OnibusViewHolder;
import br.com.vostre.circular.view.viewHolder.PaisViewHolder;

public class OnibusAdapter extends RecyclerView.Adapter<OnibusViewHolder> {

    public List<Onibus> onibus;
    AppCompatActivity ctx;

    public OnibusAdapter(List<Onibus> onibus, AppCompatActivity context){
        this.onibus = onibus;
        ctx = context;
    }

    @Override
    public OnibusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaOnibusBinding itemBinding =
                LinhaOnibusBinding.inflate(layoutInflater, parent, false);
        return new OnibusViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(OnibusViewHolder holder, int position) {
        Onibus umOnibus = onibus.get(position);
        holder.bind(umOnibus);
    }

    @Override
    public int getItemCount() {

        if(onibus == null){
            return 0;
        } else{
            return onibus.size();
        }


    }
}
