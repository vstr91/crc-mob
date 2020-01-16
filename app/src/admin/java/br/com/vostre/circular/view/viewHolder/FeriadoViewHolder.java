package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaFeriadosBinding;
import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.model.Feriado;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.form.FormPais;

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
