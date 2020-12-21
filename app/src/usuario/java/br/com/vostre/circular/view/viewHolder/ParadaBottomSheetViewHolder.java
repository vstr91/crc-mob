package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaParadasBottomSheetBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.DetalheParadaActivity;

public class ParadaBottomSheetViewHolder extends RecyclerView.ViewHolder {

    private final LinhaParadasBottomSheetBinding binding;
    AppCompatActivity ctx;

    public ParadaBottomSheetViewHolder(LinhaParadasBottomSheetBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ParadaBairro parada, String bairroAtual) {
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

        binding.circleView2.setContentDescription("Imagem "+parada.getParada().getNome()+", "+parada.getNomeBairroComCidade());

        binding.executePendingBindings();
    }

}