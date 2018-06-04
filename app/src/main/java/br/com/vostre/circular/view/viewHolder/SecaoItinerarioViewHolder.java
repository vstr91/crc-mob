package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.databinding.LinhaSecoesBinding;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.view.form.FormPais;
import br.com.vostre.circular.view.form.FormSecao;

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

        if(secao.getProgramadoPara() != null && secao.getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(secao.getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormSecao formSecao = new FormSecao();
                formSecao.setSecao(secao);
                formSecao.flagInicioEdicao = true;
                formSecao.show(ctx.getSupportFragmentManager(), "formSecao");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
