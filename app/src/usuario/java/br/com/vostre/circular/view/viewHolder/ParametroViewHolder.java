package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaParametrosBinding;
import br.com.vostre.circular.model.Parametro;
import br.com.vostre.circular.view.form.FormParametro;

public class ParametroViewHolder extends RecyclerView.ViewHolder {

    private final LinhaParametrosBinding binding;
    AppCompatActivity ctx;

    public ParametroViewHolder(LinhaParametrosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Parametro parametro) {
        binding.setParametro(parametro);

        if(parametro.getProgramadoPara() != null && parametro.getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(parametro.getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormParametro formParametro = new FormParametro();
                formParametro.setParametro(parametro);
                formParametro.flagInicioEdicao = true;
                formParametro.show(ctx.getSupportFragmentManager(), "formParametro");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
