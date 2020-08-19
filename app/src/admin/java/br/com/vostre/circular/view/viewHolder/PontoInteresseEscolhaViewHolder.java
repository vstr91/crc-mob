package br.com.vostre.circular.view.viewHolder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.format.DateTimeFormat;

import java.io.File;

import br.com.vostre.circular.databinding.LinhaPoisBinding;
import br.com.vostre.circular.databinding.LinhaPoisSugeridosBinding;
import br.com.vostre.circular.listener.PontoInteresseSugestaoListener;
import br.com.vostre.circular.model.pojo.PontoInteresseBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;

public class PontoInteresseEscolhaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaPoisBinding binding;
    AppCompatActivity ctx;
    PontoInteresseSugestaoListener listener;
    Bitmap foto;

    public PontoInteresseEscolhaViewHolder(LinhaPoisBinding binding, AppCompatActivity context, PontoInteresseSugestaoListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.listener = listener;
    }

    public void bind(final PontoInteresseBairro parada) {
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

        binding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("poi", parada.getPontoInteresse().getId());
                ctx.setResult(Activity.RESULT_OK, i);
                ctx.finish();
            }
        });

        binding.btnVerNoMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectedPoi(parada.getPontoInteresse().getId(), 2);
            }
        });

        binding.executePendingBindings();
    }
}
