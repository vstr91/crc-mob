package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaBairrosBinding;
import br.com.vostre.circular.databinding.LinhaTrechosBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.TrechoPartidaDestino;
import br.com.vostre.circular.view.DetalhesBairroActivity;
import br.com.vostre.circular.view.form.FormBairro;

public class TrechoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaTrechosBinding binding;
    AppCompatActivity ctx;

    public TrechoViewHolder(LinhaTrechosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final TrechoPartidaDestino trecho) {
        binding.setTrecho(trecho);
        binding.executePendingBindings();
    }
}
