package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaCidadesBinding;
import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.DetalheParadaActivity;
import br.com.vostre.circular.view.listener.SelectListener;

public class ParadaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaParadasBinding binding;
    AppCompatActivity ctx;

    public ParadaViewHolder(LinhaParadasBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ParadaBairro parada, boolean mostraBairro) {
        binding.setParada(parada);

        Parada p = parada.getParada();
        File f = null;

        if(p != null && p.getImagem() != null){
            f = new File(ctx.getApplicationContext().getFilesDir(),  p.getImagem());
        }

        if(parada.getParada().getImagem() != null && f != null && f.exists() && f.canRead()){
            binding.circleView2.setImageDrawable(Drawable.createFromPath(ctx.getApplicationContext().getFilesDir()+"/"+parada.getParada().getImagem()));
        } else{
            binding.circleView2.setImageDrawable(ctx.getResources().getDrawable(R.drawable.imagem_nao_disponivel_quadrada));
        }

        //binding.circleView2.setImagem(null);

//        final File brasao = new File(ctx.getApplicationContext().getFilesDir(),  cidade.getCidade().getBrasao());
//
//        if(brasao.exists() && brasao.canRead()){
//            final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
//            binding.circleView2.setImagem(drawable);
//        }
//
//        binding.textViewNome.setText(cidade.getCidade().getNome());

        if(!mostraBairro){
            binding.textViewBairro.setVisibility(View.GONE);
        } else{
            binding.textViewBairro.setVisibility(View.VISIBLE);
        }

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, DetalheParadaActivity.class);
                i.putExtra("parada", parada.getParada().getId());
                ctx.startActivity(i);
            }
        };

        binding.circleView2.setOnClickListener(listener);
        binding.textViewNome.setOnClickListener(listener);

        binding.executePendingBindings();
    }

}
