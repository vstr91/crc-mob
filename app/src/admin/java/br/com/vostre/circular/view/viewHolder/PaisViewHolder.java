package br.com.vostre.circular.view.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.form.FormPais;

public class PaisViewHolder extends RecyclerView.ViewHolder {

    private final LinhaPaisesBinding binding;
    AppCompatActivity ctx;

    public PaisViewHolder(LinhaPaisesBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Pais pais) {
        binding.setPais(pais);

        if(pais.getProgramadoPara() != null && pais.getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(pais.getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormPais formPais = new FormPais();
                formPais.setPais(pais);
                formPais.flagInicioEdicao = true;
                formPais.show(ctx.getSupportFragmentManager(), "formPais");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
