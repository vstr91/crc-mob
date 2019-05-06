package br.com.vostre.circular.view.viewHolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.joda.time.format.DateTimeFormat;

import java.io.File;

import br.com.vostre.circular.databinding.LinhaParadasSugeridasBinding;
import br.com.vostre.circular.databinding.LinhaPoisSugeridosBinding;
import br.com.vostre.circular.listener.ParadaSugestaoListener;
import br.com.vostre.circular.listener.PontoInteresseSugestaoListener;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;

public class PontoInteresseSugestaoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaPoisSugeridosBinding binding;
    AppCompatActivity ctx;
    PontoInteresseSugestaoListener listener;
    Bitmap foto;

    public PontoInteresseSugestaoViewHolder(LinhaPoisSugeridosBinding binding, AppCompatActivity context, PontoInteresseSugestaoListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.listener = listener;
    }

    public void bind(final PontoInteresseSugestaoBairro parada) {
        binding.setParada(parada);

        if(parada.getPontoInteresse().getImagem() != null){
            File foto = new File(ctx.getFilesDir(), parada.getPontoInteresse().getImagem());

            if(foto.exists() && foto.canRead()){
                this.foto = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.foto = null;
        }

        binding.setFoto(foto);

        if(parada.getPontoInteresse().getStatus() != 0){
            binding.btnAceitar.setVisibility(View.GONE);
            binding.btnRejeitar.setVisibility(View.GONE);
            binding.btnVerNoMapa.setVisibility(View.GONE);
            binding.textViewStatus.setVisibility(View.VISIBLE);

            binding.textViewStatus.setText(DateTimeFormat.forPattern("dd/MM/YYYY HH:mm:ss").print(parada.getPontoInteresse().getUltimaAlteracao()));
        } else{

            binding.btnAceitar.setVisibility(View.VISIBLE);
            binding.btnRejeitar.setVisibility(View.VISIBLE);
            binding.textViewStatus.setVisibility(View.GONE);
            binding.btnVerNoMapa.setVisibility(View.VISIBLE);
            binding.textViewStatus.setText("");

            binding.btnAceitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelectedPoi(parada.getPontoInteresse().getId(), 1);
                }
            });

            binding.btnRejeitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelectedPoi(parada.getPontoInteresse().getId(), 0);
                }
            });

        }

        binding.btnVerNoMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectedPoi(parada.getPontoInteresse().getId(), 2);
            }
        });

        binding.executePendingBindings();
    }
}
