package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaEstadosBinding;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.form.FormEstado;

public class EstadoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaEstadosBinding binding;
    AppCompatActivity ctx;

    public EstadoViewHolder(LinhaEstadosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Estado estado) {
        binding.setEstado(estado);

        if(estado.getProgramadoPara() != null && estado.getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(estado.getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormEstado formEstado = new FormEstado();
                formEstado.setEstado(estado);
                formEstado.setCtx(ctx.getApplication());
                formEstado.flagInicioEdicao = true;
                formEstado.show(ctx.getSupportFragmentManager(), "formEstado");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
