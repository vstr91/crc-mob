package br.com.vostre.circular.view.viewHolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaSecoesBinding;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.SecaoItinerario;

public class SecaoItinerarioViewHolder extends RecyclerView.ViewHolder {

    private final LinhaSecoesBinding binding;
    AppCompatActivity ctx;

    public SecaoItinerarioViewHolder(LinhaSecoesBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final SecaoItinerario secao) {
        binding.setSecao(secao);

        binding.executePendingBindings();
    }
}
