package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.databinding.LinhaEmpresasBinding;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.view.viewHolder.EmpresaViewHolder;

public class EmpresaAdapter extends RecyclerView.Adapter<EmpresaViewHolder> implements Filterable {

    public List<Empresa> empresas;
    public List<Empresa> empresasOriginal;
    public List<Empresa> listaFiltrada;
    AppCompatActivity ctx;

    public EmpresaAdapter(List<Empresa> empresas, AppCompatActivity context){
        this.empresas = empresas;
        this.empresasOriginal = empresas;
        ctx = context;
    }

    @Override
    public EmpresaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaEmpresasBinding itemBinding =
                LinhaEmpresasBinding.inflate(layoutInflater, parent, false);
        return new EmpresaViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(EmpresaViewHolder holder, int position) {
        Empresa empresa = empresas.get(position);
        holder.bind(empresa);
    }

    @Override
    public int getItemCount() {

        if(empresas == null){
            return 0;
        } else{
            return empresas.size();
        }


    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                if(charString.isEmpty()){
                    listaFiltrada = empresasOriginal;
                } else{
                    listaFiltrada = new ArrayList<>();

                    for(Empresa e : empresasOriginal){

                        if(e.getNome().toLowerCase().contains(charString.toLowerCase())){
                            listaFiltrada.add(e);
                        }

                    }

                }

                FilterResults results = new FilterResults();
                results.values = listaFiltrada;
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                List emp = (ArrayList<Empresa>) results.values;

//                if(emp.size() > 0){
                    empresas = emp;
//                } else{
//                    empresas = empresasOriginal;
//                }

                notifyDataSetChanged();
            }
        };
    }
}
