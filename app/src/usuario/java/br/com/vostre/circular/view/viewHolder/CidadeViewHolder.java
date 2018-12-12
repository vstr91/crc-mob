package br.com.vostre.circular.view.viewHolder;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import br.com.vostre.circleview.CircleView;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaCidadesBinding;
import br.com.vostre.circular.model.adapter.SyncAdapter;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.utils.tasks.ImageDownloadAsyncTask;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.view.listener.SelectListener;

public class CidadeViewHolder extends RecyclerView.ViewHolder {

    private final LinhaCidadesBinding binding;
    AppCompatActivity ctx;
    SelectListener myListener;

    public SelectListener getListener() {
        return myListener;
    }

    public void setListener(SelectListener listener) {
        this.myListener = listener;
    }

    public CidadeViewHolder(LinhaCidadesBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final CidadeEstado cidade) {
        binding.setCidade(cidade);
        //binding.circleView2.setImagem(null);

        if(ctx != null){

            if(cidade.getCidade().getBrasao() != null && !cidade.getCidade().getBrasao().isEmpty()){

                final File brasao = new File(ctx.getApplicationContext().getFilesDir(),  cidade.getCidade().getBrasao());

                if(!brasao.exists() || !brasao.canRead()){
                    binding.circleView2.setImageDrawable(ctx.getResources().getDrawable(R.drawable.imagem_nao_disponivel_quadrada));
                    ImageDownloadAsyncTask imageDownloadAsyncTask = new ImageDownloadAsyncTask(ctx, cidade.getCidade().getBrasao());
                    imageDownloadAsyncTask.execute();
                }

            }

        }

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myListener.onSelected(cidade.getCidade().getId());
            }
        };

        binding.circleView2.setOnClickListener(listener);
        binding.textViewNome.setOnClickListener(listener);

        binding.executePendingBindings();
    }

}
