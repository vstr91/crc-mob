package br.com.vostre.circular.view.viewHolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import java.io.File;

import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.databinding.LinhaServicosBinding;
import br.com.vostre.circular.model.Servico;
import br.com.vostre.circular.view.form.FormPais;
import br.com.vostre.circular.view.form.FormServico;

public class ServicoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaServicosBinding binding;
    AppCompatActivity ctx;

    public ServicoViewHolder(LinhaServicosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Servico servico) {
        binding.setServico(servico);

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FormServico formServico = new FormServico();
                formServico.setServico(servico);
                formServico.flagInicioEdicao = true;
                formServico.setCtx(ctx.getApplication());
                formServico.show(ctx.getSupportFragmentManager(), "formServico");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
