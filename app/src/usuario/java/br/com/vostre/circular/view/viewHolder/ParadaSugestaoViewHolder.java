package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.databinding.LinhaParadasSugeridasBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.view.form.FormParada;

public class ParadaSugestaoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaParadasSugeridasBinding binding;
    AppCompatActivity ctx;

    public ParadaSugestaoViewHolder(LinhaParadasSugeridasBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ParadaSugestaoBairro parada) {
        binding.setParada(parada);

        if(parada.getParada().getStatus() != 0){
            binding.textViewStatus.setVisibility(View.VISIBLE);

            binding.textViewStatus.setText(DateTimeFormat.forPattern("dd/MM/YYYY").print(parada.getParada().getUltimaAlteracao()));
        } else{
            binding.textViewStatus.setVisibility(View.GONE);
            binding.textViewStatus.setText("");

        }

        binding.executePendingBindings();
    }
}
