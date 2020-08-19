package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.io.File;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaRuasBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.DetalheParadaActivity;

public class ParadaRuaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaRuasBinding binding;
    AppCompatActivity ctx;

    public ParadaRuaViewHolder(LinhaRuasBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ParadaBairro parada, int posicao) {
        binding.setParada(parada);

        switch(posicao){
            case 0: //inicio da lista
                binding.imageView11.setImageResource(R.drawable.parada_rua_fim);
                binding.imageView11.setRotation(180);
                break;
            case 1: // fim da lista
                binding.imageView11.setImageResource(R.drawable.parada_rua_fim);
                binding.imageView11.setRotation(0);
                break;
            default: // outros registros
                binding.imageView11.setImageResource(R.drawable.parada_rua);
                break;
        }


        binding.executePendingBindings();
    }

}
