package br.com.vostre.circular.view.viewHolder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.listener.ParadaListener;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.form.FormParada;

public class ParadaEscolhaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaParadasBinding binding;
    AppCompatActivity ctx;
    ParadaListener listener;

    public ParadaEscolhaViewHolder(LinhaParadasBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public ParadaEscolhaViewHolder(LinhaParadasBinding binding, AppCompatActivity context, ParadaListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.listener = listener;
    }

    public void bind(final ParadaBairro parada) {
        binding.setParada(parada);

        if(parada.getParada().getProgramadoPara() != null && parada.getParada().getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(parada.getParada().getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

            binding.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.putExtra("parada", parada.getParada().getId());
                    ctx.setResult(Activity.RESULT_OK, i);
                    ctx.finish();
                }
            });

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormParada formParada = new FormParada();
                formParada.setParada(parada);
                formParada.setLatitude(parada.getParada().getLatitude());
                formParada.setLongitude(parada.getParada().getLongitude());
                formParada.setCtx(ctx.getApplication());
                formParada.flagInicioEdicao = true;
                formParada.show(ctx.getSupportFragmentManager(), "formParada");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
