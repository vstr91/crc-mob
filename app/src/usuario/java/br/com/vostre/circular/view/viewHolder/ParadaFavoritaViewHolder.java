package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.databinding.LinhaParadasFavoritasBinding;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.DetalheParadaActivity;

public class ParadaFavoritaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaParadasFavoritasBinding binding;
    AppCompatActivity ctx;

    public ParadaFavoritaViewHolder(LinhaParadasFavoritasBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ParadaBairro parada, boolean mostraBairro) {
        binding.setParada(parada);

        if(parada.getParada().getImagem() == null){
            binding.circleView2.setBackgroundColor(Color.parseColor("#000088"));
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
