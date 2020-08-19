package br.com.vostre.circular.view.viewHolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import org.joda.time.format.DateTimeFormat;

import java.io.File;

import br.com.vostre.circular.databinding.LinhaParadasSugeridasBinding;
import br.com.vostre.circular.listener.ParadaSugestaoListener;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;

public class ParadaSugestaoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaParadasSugeridasBinding binding;
    AppCompatActivity ctx;
    ParadaSugestaoListener listener;
    Bitmap foto;

    public ParadaSugestaoViewHolder(LinhaParadasSugeridasBinding binding, AppCompatActivity context, ParadaSugestaoListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.listener = listener;
    }

    public void bind(final ParadaSugestaoBairro parada) {
        binding.setParada(parada);

        if(parada.getParada().getImagem() != null){
            File foto = new File(ctx.getFilesDir(), parada.getParada().getImagem());

            if(foto.exists() && foto.canRead()){
                this.foto = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.foto = null;
        }

        binding.setFoto(foto);

        if(parada.getParada().getStatus() != 0){
            binding.btnAceitar.setVisibility(View.GONE);
            binding.btnRejeitar.setVisibility(View.GONE);
            binding.btnVerNoMapa.setVisibility(View.GONE);
            binding.textViewStatus.setVisibility(View.VISIBLE);

            binding.textViewStatus.setText(DateTimeFormat.forPattern("dd/MM/YYYY HH:mm:ss").print(parada.getParada().getUltimaAlteracao()));
        } else{

            binding.btnAceitar.setVisibility(View.VISIBLE);
            binding.btnRejeitar.setVisibility(View.VISIBLE);
            binding.textViewStatus.setVisibility(View.GONE);
            binding.btnVerNoMapa.setVisibility(View.VISIBLE);
            binding.textViewStatus.setText("");

            binding.btnAceitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelected(parada.getParada().getId(), 1);
                }
            });

            binding.btnRejeitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelected(parada.getParada().getId(), 0);
                }
            });

        }

        binding.btnVerNoMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelected(parada.getParada().getId(), 2);
            }
        });

        binding.executePendingBindings();
    }
}
