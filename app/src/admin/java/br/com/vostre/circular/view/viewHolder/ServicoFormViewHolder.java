package br.com.vostre.circular.view.viewHolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import br.com.vostre.circular.databinding.LinhaServicosFormBinding;
import br.com.vostre.circular.model.Servico;

public class ServicoFormViewHolder extends RecyclerView.ViewHolder {

    private final LinhaServicosFormBinding binding;
    AppCompatActivity ctx;
    Bitmap foto;

    public ServicoFormViewHolder(LinhaServicosFormBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Servico servico) {
        binding.setServico(servico);

        if(servico.getIcone() != null){
            File foto = new File(ctx.getFilesDir(), servico.getIcone());

            if(foto.exists() && foto.canRead()){
                this.foto = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.foto = null;
        }

        binding.setFoto(foto);

        binding.executePendingBindings();
    }
}
