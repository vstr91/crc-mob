package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.viewHolder.PaisViewHolder;

public class PaisAdapterSpinner extends ArrayAdapter<Pais> {

    public List<Pais> paises;
    AppCompatActivity ctx;

    public PaisAdapterSpinner(AppCompatActivity context, int resouceId, int textviewId, List<Pais> paises){
        this.paises = paises;
        ctx = context;
    }

    @Override
    public PaisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaPaisesBinding itemBinding =
                LinhaPaisesBinding.inflate(layoutInflater, parent, false);
        return new PaisViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(PaisViewHolder holder, int position) {
        Pais pais = paises.get(position);
        holder.bind(pais);
    }

    @Override
    public int getItemCount() {

        if(paises == null){
            return 0;
        } else{
            return paises.size();
        }


    }
}
