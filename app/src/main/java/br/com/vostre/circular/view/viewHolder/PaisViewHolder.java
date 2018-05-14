package br.com.vostre.circular.view.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.model.Pais;

public class PaisViewHolder extends RecyclerView.ViewHolder {

    private final LinhaPaisesBinding binding;

    public PaisViewHolder(LinhaPaisesBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Pais pais) {
        binding.setPais(pais);

        if(pais.getProgramadoPara() != null && pais.getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.executePendingBindings();
    }
}
