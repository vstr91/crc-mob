package br.com.vostre.circular.view.viewHolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import br.com.vostre.circular.databinding.LinhaFeriadosBinding;
import br.com.vostre.circular.model.Feriado;

public class FeriadoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaFeriadosBinding binding;
    AppCompatActivity ctx;

    public FeriadoViewHolder(LinhaFeriadosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Feriado feriado, String anoAtual) {
        binding.setFeriado(feriado);

        if(feriado.getAno().equals(anoAtual)){
            binding.textViewAno.setVisibility(View.GONE);
        } else{
            binding.textViewAno.setVisibility(View.VISIBLE);
        }

        binding.executePendingBindings();
    }
}
