package br.com.vostre.circular.view.viewHolder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaUsuariosBinding;
import br.com.vostre.circular.model.Usuario;

public class UsuarioViewHolder extends RecyclerView.ViewHolder {

    private final LinhaUsuariosBinding binding;
    AppCompatActivity ctx;

    public UsuarioViewHolder(LinhaUsuariosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Usuario usuario) {
        binding.setUsuario(usuario);

        if(usuario.getProgramadoPara() != null && usuario.getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(usuario.getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormUsuario formUsuario = new FormUsuario();
                formUsuario.setUsuario(usuario);
                formUsuario.flagInicioEdicao = true;
                formUsuario.show(ctx.getSupportFragmentManager(), "formUsuario");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
