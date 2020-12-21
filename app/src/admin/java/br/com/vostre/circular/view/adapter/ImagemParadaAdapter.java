package br.com.vostre.circular.view.adapter;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaImagensBinding;
import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.listener.ParadaSugestaoListener;
import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.pojo.ImagemParadaBairro;
import br.com.vostre.circular.view.viewHolder.ImagemParadaViewHolder;
import br.com.vostre.circular.view.viewHolder.PaisViewHolder;

public class ImagemParadaAdapter extends RecyclerView.Adapter<ImagemParadaViewHolder> implements ParadaSugestaoListener {

    public List<ImagemParadaBairro> imagens;
    AppCompatActivity ctx;
    ParadaSugestaoListener listener;

    public ImagemParadaAdapter(List<ImagemParadaBairro> imagens, AppCompatActivity context, ParadaSugestaoListener listener){
        this.imagens = imagens;
        ctx = context;
        this.listener = listener;
    }

    @Override
    public ImagemParadaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaImagensBinding itemBinding =
                LinhaImagensBinding.inflate(layoutInflater, parent, false);
        return new ImagemParadaViewHolder(itemBinding, ctx, listener);
    }

    @Override
    public void onBindViewHolder(ImagemParadaViewHolder holder, int position) {
        ImagemParadaBairro imagemParada = imagens.get(position);
        holder.bind(imagemParada);
    }

    @Override
    public int getItemCount() {

        if(imagens == null){
            return 0;
        } else{
            return imagens.size();
        }


    }

    @Override
    public void onSelected(String id, int acao) {
        listener.onSelected(id, acao);
    }

}
