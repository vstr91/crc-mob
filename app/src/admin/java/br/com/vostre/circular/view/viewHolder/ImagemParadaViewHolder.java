package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.format.DateTimeFormat;

import java.io.File;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaImagensBinding;
import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.listener.ParadaSugestaoListener;
import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.pojo.ImagemParadaBairro;
import br.com.vostre.circular.view.DetalheParadaActivity;
import br.com.vostre.circular.view.DetalhesItinerarioActivity;
import br.com.vostre.circular.view.form.FormPais;

public class ImagemParadaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaImagensBinding binding;
    AppCompatActivity ctx;
    Bitmap foto;
    ParadaSugestaoListener listener;

    public ImagemParadaViewHolder(LinhaImagensBinding binding, AppCompatActivity context, ParadaSugestaoListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.listener = listener;
    }

    public void bind(final ImagemParadaBairro imagemParada) {
        binding.setImagemParada(imagemParada);

        if(imagemParada.getImagemParada().getImagem() != null){
            File foto = new File(ctx.getFilesDir(), imagemParada.getImagemParada().getImagem());

            if(foto.exists() && foto.canRead()){
                this.foto = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.foto = null;
        }

        binding.setFoto(foto);

//        binding.textViewNome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(ctx, DetalheParadaActivity.class);
//                i.putExtra("parada", imagemParada.getIdParada());
//                ctx.startActivity(i);
//            }
//        });

        switch(Integer.parseInt(imagemParada.getSentidoParada())){
            case 0:
                binding.textView62.setText("Sentido Centro");
                break;
            case 1:
                binding.textView62.setText("Sentido Bairro");
                break;
            case -1:
                binding.textView62.setText("-");
                break;
        }

        switch(imagemParada.getImagemParada().getStatus()){
            case 0:
                binding.textView63.setTextColor(ctx.getResources().getColor(R.color.azul));
                binding.textView63.setText("Pendente");
                break;
            case 1:
                binding.textView63.setTextColor(ctx.getResources().getColor(R.color.verde));
                binding.textView63.setText("Aceito");
                break;
            case 2:
                binding.textView63.setTextColor(ctx.getResources().getColor(R.color.vermelho));
                binding.textView63.setText("Recusado");
                break;
        }

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(imagemParada.getImagemParada().getStatus() != 0){
                    listener.onSelected(imagemParada.getImagemParada().getId(), 0);
                }

                return false;
            }
        });

        if(imagemParada.getImagemParada().getStatus() == 0){

            binding.button13.setVisibility(View.VISIBLE);
            binding.button14.setVisibility(View.VISIBLE);

            // aceitar
            binding.button13.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSelected(imagemParada.getImagemParada().getId(), 1);
                }
            });

            // recusar
            binding.button14.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSelected(imagemParada.getImagemParada().getId(), 2);
                }
            });

        } else{
            binding.button13.setVisibility(View.GONE);
            binding.button14.setVisibility(View.GONE);
        }

        binding.executePendingBindings();
    }
}
