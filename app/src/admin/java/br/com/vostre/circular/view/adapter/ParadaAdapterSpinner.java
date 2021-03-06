package br.com.vostre.circular.view.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaBairrosSpinnerBinding;
import br.com.vostre.circular.databinding.LinhaParadasSpinnerBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;

public class ParadaAdapterSpinner extends ArrayAdapter<ParadaBairro> {

    public List<ParadaBairro> paradas;
    Application ctx;

    public ParadaAdapterSpinner(Application context, int resouceId, int viewId, List<ParadaBairro> paradas){
        super(context, resouceId, viewId, paradas);
        this.paradas = paradas;
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaParadasSpinnerBinding itemBinding =
                LinhaParadasSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setParada(paradas.get(position));
        return itemBinding.getRoot();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaParadasSpinnerBinding itemBinding =
                LinhaParadasSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setParada(paradas.get(position));
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
