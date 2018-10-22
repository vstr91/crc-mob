package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.databinding.LinhaBairrosBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.view.viewHolder.BairroViewHolder;

public class BairroAdapter extends RecyclerView.Adapter<BairroViewHolder> implements Filterable {

    public List<BairroCidade> bairros;
    public List<BairroCidade> bairrosOriginal;
    public List<BairroCidade> listaFiltrada;
    AppCompatActivity ctx;

    public BairroAdapter(List<BairroCidade> bairros, AppCompatActivity context){
        this.bairros = bairros;
        this.bairrosOriginal = bairros;
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                if(charString.isEmpty()){
                    listaFiltrada = bairrosOriginal;
                } else{
                    listaFiltrada = new ArrayList<>();

                    for(BairroCidade b : bairrosOriginal){

                        if(b.getBairro().getNome().toLowerCase().contains(charString.toLowerCase())){
                            listaFiltrada.add(b);
                        }

                    }

                }

                FilterResults results = new FilterResults();
                results.values = listaFiltrada;
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                List bairr = (ArrayList<BairroCidade>) results.values;

//                if(emp.size() > 0){
                bairros = bairr;
//                } else{
//                    empresas = empresasOriginal;
//                }

                notifyDataSetChanged();
            }
        };
    }

}
