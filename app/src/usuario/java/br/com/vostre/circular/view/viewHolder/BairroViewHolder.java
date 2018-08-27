package br.com.vostre.circular.view.viewHolder;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaBairrosBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.view.listener.SelectListener;

public class BairroViewHolder extends RecyclerView.ViewHolder {

    private final LinhaBairrosBinding binding;
    Context ctx;
    SelectListener listener;

    public SelectListener getListener() {
        return listener;
    }

    public void setListener(SelectListener listener) {
        this.listener = listener;
    }

    public BairroViewHolder(LinhaBairrosBinding binding, Context context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final BairroCidade bairro) {
        binding.setBairro(bairro);

        binding.textViewNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSelected(bairro.getBairro().getId());
            }
        });

        binding.executePendingBindings();
    }
}
