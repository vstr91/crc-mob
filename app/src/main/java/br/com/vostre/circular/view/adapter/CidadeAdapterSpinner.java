package br.com.vostre.circular.view.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaCidadesSpinnerBinding;
import br.com.vostre.circular.databinding.LinhaEstadosSpinnerBinding;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.pojo.CidadeEstado;

public class CidadeAdapterSpinner extends ArrayAdapter<CidadeEstado> {

    public List<CidadeEstado> cidades;
    Application ctx;

    public CidadeAdapterSpinner(Application context, int resouceId, int viewId, List<CidadeEstado> cidades){
        super(context, resouceId, viewId, cidades);
        this.cidades = cidades;
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaCidadesSpinnerBinding itemBinding =
                LinhaCidadesSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setCidade(cidades.get(position));
        return itemBinding.getRoot();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaCidadesSpinnerBinding itemBinding =
                LinhaCidadesSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setCidade(cidades.get(position));
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
