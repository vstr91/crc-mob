package br.com.vostre.circular.view.viewHolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaPoisSugeridosBinding;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;

public class PontoInteresseSugestaoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaPoisSugeridosBinding binding;
    AppCompatActivity ctx;

    public PontoInteresseSugestaoViewHolder(LinhaPoisSugeridosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final PontoInteresseSugestaoBairro poi) {
        binding.setPoi(poi);

        if(poi.getPontoInteresse().getStatus() != 0){
            binding.textViewStatus.setVisibility(View.VISIBLE);

            binding.textViewStatus.setText(DateTimeFormat.forPattern("dd/MM/YYYY").print(poi.getPontoInteresse().getUltimaAlteracao()));
        } else{
            binding.textViewStatus.setVisibility(View.GONE);
            binding.textViewStatus.setText("");

        }

        binding.executePendingBindings();
    }
}
