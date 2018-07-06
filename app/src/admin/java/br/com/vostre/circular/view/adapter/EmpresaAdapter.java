package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaEmpresasBinding;
import br.com.vostre.circular.model.Empresa;

public class EmpresaAdapter extends RecyclerView.Adapter<EmpresaViewHolder> {

    public List<Empresa> empresas;
    AppCompatActivity ctx;

    public EmpresaAdapter(List<Empresa> empresas, AppCompatActivity context){
        this.empresas = empresas;
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
}
