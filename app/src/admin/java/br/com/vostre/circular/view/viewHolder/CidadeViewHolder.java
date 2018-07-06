package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaCidadesBinding;
import br.com.vostre.circular.model.pojo.CidadeEstado;

public class CidadeViewHolder extends RecyclerView.ViewHolder {

    private final LinhaCidadesBinding binding;
    AppCompatActivity ctx;

    public CidadeViewHolder(LinhaCidadesBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final CidadeEstado cidade) {
        binding.setCidade(cidade);

        if(cidade.getCidade().getProgramadoPara() != null && cidade.getCidade().getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(cidade.getCidade().getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormCidade formCidade = new FormCidade();
                formCidade.setCidade(cidade);
                formCidade.setCtx(ctx.getApplication());
                formCidade.flagInicioEdicao = true;
                formCidade.show(ctx.getSupportFragmentManager(), "formCidade");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
