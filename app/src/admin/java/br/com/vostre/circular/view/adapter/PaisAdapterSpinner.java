package br.com.vostre.circular.view.adapter;

import android.app.Application;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaPaisesSpinnerBinding;
import br.com.vostre.circular.model.Pais;

public class PaisAdapterSpinner extends ArrayAdapter<Pais> {

    public List<Pais> paises;
    Application ctx;

    public PaisAdapterSpinner(Application context, int resouceId, int viewId, List<Pais> paises){
        super(context, resouceId, viewId, paises);
        this.paises = paises;
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaPaisesSpinnerBinding itemBinding =
                LinhaPaisesSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setPais(paises.get(position));
        return itemBinding.getRoot();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater =
                LayoutInflater.from(ctx);
        LinhaPaisesSpinnerBinding itemBinding =
                LinhaPaisesSpinnerBinding.inflate(layoutInflater, parent, false);
        itemBinding.setPais(paises.get(position));
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
