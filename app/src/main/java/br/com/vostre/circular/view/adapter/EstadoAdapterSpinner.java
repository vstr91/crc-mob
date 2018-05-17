package br.com.vostre.circular.view.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaEstadosSpinnerBinding;
import br.com.vostre.circular.databinding.LinhaPaisesSpinnerBinding;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Pais;

public class EstadoAdapterSpinner extends ArrayAdapter<Estado> {

    public List<Estado> estados;
    Application ctx;

    public EstadoAdapterSpinner(Application context, int resouceId, int viewId, List<Estado> estados){
        super(context, resouceId, viewId, estados);
        this.estados = estados;
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaEstadosSpinnerBinding itemBinding =
                LinhaEstadosSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setEstado(estados.get(position));
        return itemBinding.getRoot();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaEstadosSpinnerBinding itemBinding =
                LinhaEstadosSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setEstado(estados.get(position));
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
