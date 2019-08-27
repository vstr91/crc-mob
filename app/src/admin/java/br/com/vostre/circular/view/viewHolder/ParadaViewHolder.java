package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaBairrosBinding;
import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.listener.ParadaListener;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.form.FormParada;

public class ParadaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaParadasBinding binding;
    AppCompatActivity ctx;
    ParadaListener listener;

    public ParadaViewHolder(LinhaParadasBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public ParadaViewHolder(LinhaParadasBinding binding, AppCompatActivity context, ParadaListener listener) {
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

        if(listener != null){
            binding.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelected(parada.getParada().getId());
                }
            });
        }

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
