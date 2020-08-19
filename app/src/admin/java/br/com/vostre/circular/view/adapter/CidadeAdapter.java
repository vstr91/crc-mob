package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.databinding.LinhaCidadesBinding;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.viewHolder.CidadeViewHolder;

public class CidadeAdapter extends RecyclerView.Adapter<CidadeViewHolder> implements Filterable {

    public List<CidadeEstado> cidades;
    AppCompatActivity ctx;
    public List<CidadeEstado> cidadesOriginal;
    public List<CidadeEstado> listaFiltrada;

    public CidadeAdapter(List<CidadeEstado> cidades, AppCompatActivity context){
        this.cidades = cidades;
        this.cidadesOriginal = cidades;
        ctx = context;
    }

    @Override
    public CidadeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaCidadesBinding itemBinding =
                LinhaCidadesBinding.inflate(layoutInflater, parent, false);
        return new CidadeViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(CidadeViewHolder holder, int position) {
        CidadeEstado cidade = cidades.get(position);
        holder.bind(cidade);
    }

    @Override
    public int getItemCount() {

        if(cidades == null){
            return 0;
        } else{
            return cidades.size();
        }


    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                if(charString.isEmpty()){
                    listaFiltrada = cidadesOriginal;
                } else{
                    listaFiltrada = new ArrayList<>();

                    for(CidadeEstado c : cidadesOriginal){

                        if(c.getCidade().getNome().toLowerCase().contains(charString.toLowerCase())){
                            listaFiltrada.add(c);
                        }

                    }

                }

                FilterResults results = new FilterResults();
                results.values = listaFiltrada;
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                List cid = (ArrayList<CidadeEstado>) results.values;

//                if(cid.size() > 0){
                    cidades = cid;
//                } else{
//                    cidades = cidadesOriginal;
//                }

                notifyDataSetChanged();
            }
        };
    }

}
