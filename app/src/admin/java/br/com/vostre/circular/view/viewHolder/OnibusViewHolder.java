package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaOnibusBinding;
import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.view.DetalhesOnibusActivity;
import br.com.vostre.circular.view.form.FormOnibus;

public class OnibusViewHolder extends RecyclerView.ViewHolder {

    private final LinhaOnibusBinding binding;
    AppCompatActivity ctx;

    public OnibusViewHolder(LinhaOnibusBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Onibus onibus) {
        binding.setOnibus(onibus);

        if(onibus.getProgramadoPara() != null && onibus.getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(onibus.getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, DetalhesOnibusActivity.class);
                i.putExtra("onibus", onibus.getId());
                ctx.startActivity(i);
            }
        });

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormOnibus formOnibus = new FormOnibus();
                formOnibus.setOnibus(onibus);
                formOnibus.flagInicioEdicao = true;
                formOnibus.show(ctx.getSupportFragmentManager(), "formOnibus");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
