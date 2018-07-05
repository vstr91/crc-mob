package br.com.vostre.circular.view.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaBairrosSpinnerBinding;
import br.com.vostre.circular.databinding.LinhaCidadesSpinnerBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;

public class BairroAdapterSpinner extends ArrayAdapter<BairroCidade> {

    public List<BairroCidade> bairros;
    Application ctx;

    public BairroAdapterSpinner(Application context, int resouceId, int viewId, List<BairroCidade> bairros){
        super(context, resouceId, viewId, bairros);
        this.bairros = bairros;
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaBairrosSpinnerBinding itemBinding =
                LinhaBairrosSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setBairro(bairros.get(position));
        return itemBinding.getRoot();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaBairrosSpinnerBinding itemBinding =
                LinhaBairrosSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setBairro(bairros.get(position));
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
