package br.com.vostre.circular.view.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaEmpresasSpinnerBinding;
import br.com.vostre.circular.databinding.LinhaPaisesSpinnerBinding;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Pais;

public class EmpresaAdapterSpinner extends ArrayAdapter<Empresa> {

    public List<Empresa> empresas;
    Application ctx;

    public EmpresaAdapterSpinner(Application context, int resouceId, int viewId, List<Empresa> empresas){
        super(context, resouceId, viewId, empresas);
        this.empresas = empresas;
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaEmpresasSpinnerBinding itemBinding =
                LinhaEmpresasSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setEmpresa(empresas.get(position));
        return itemBinding.getRoot();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaEmpresasSpinnerBinding itemBinding =
                LinhaEmpresasSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setEmpresa(empresas.get(position));
        return itemBinding.getRoot();
    }

//    @Override
//    public void onBindViewHolder(PaisViewHolder holder, int position) {
//        Pais pais = paises.get(position);
//        holder.bind(pais);
//    }
//
//    @Override
//    public int getItemCount() {
//
//        if(paises == null){
//            return 0;
//        } else{
//            return paises.size();
//        }
//
//    }
}
