package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaBairrosBinding;
import br.com.vostre.circular.databinding.LinhaEstadosBinding;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.view.form.FormEstado;

public class BairroViewHolder extends RecyclerView.ViewHolder {

    private final LinhaBairrosBinding binding;
    AppCompatActivity ctx;

    public BairroViewHolder(LinhaBairrosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final BairroCidade bairro) {
        binding.setBairro(bairro);

        if(bairro.getBairro().getProgramadoPara() != null && bairro.getBairro().getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(bairro.getBairro().getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormBairro formBairro = new FormBairro();
                formBairro.setBairro(bairro);
                formBairro.setCtx(ctx.getApplication());
                formBairro.flagInicioEdicao = true;
                formBairro.show(ctx.getSupportFragmentManager(), "formBairro");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
