package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaEmpresasBinding;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.view.DetalhesEmpresaActivity;
import br.com.vostre.circular.view.form.FormEmpresa;

public class EmpresaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaEmpresasBinding binding;
    AppCompatActivity ctx;

    public EmpresaViewHolder(LinhaEmpresasBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Empresa empresa) {
        binding.setEmpresa(empresa);

        if(empresa.getProgramadoPara() != null && empresa.getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(empresa.getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, DetalhesEmpresaActivity.class);
                i.putExtra("empresa", empresa.getId());
                ctx.startActivity(i);
            }
        });

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormEmpresa formEmpresa = new FormEmpresa();
                formEmpresa.setEmpresa(empresa);
                formEmpresa.flagInicioEdicao = true;
                formEmpresa.setCtx(ctx.getApplication());
                formEmpresa.show(ctx.getSupportFragmentManager(), "formEmpresa");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
