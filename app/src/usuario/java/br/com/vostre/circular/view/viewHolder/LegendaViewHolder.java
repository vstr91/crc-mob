package br.com.vostre.circular.view.viewHolder;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaLegendaBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.Legenda;
import br.com.vostre.circular.view.listener.LegendaListener;
import br.com.vostre.circular.view.listener.SelectListener;

public class LegendaViewHolder extends RecyclerView.ViewHolder {

    private final LinhaLegendaBinding binding;
    Context ctx;
    public boolean ativa = true;
    LegendaListener listener;

    public LegendaListener getListener() {
        return listener;
    }

    public void setListener(LegendaListener listener) {
        this.listener = listener;
    }

    public LegendaViewHolder(LinhaLegendaBinding binding, Context context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public LegendaViewHolder(LinhaLegendaBinding binding, Context context, LegendaListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.listener = listener;
    }

    public void bind(final Legenda legenda) {
        binding.setLegenda(legenda);

        binding.textViewNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RecyclerView parent = (RecyclerView) binding.getRoot().getParent();

                int registros = parent.getChildCount();

                for(int i = 0; i < registros; i++){
                    RecyclerView.ViewHolder b = parent.getChildViewHolder(parent.getChildAt(i));
                    TextView tv = b.itemView.findViewById(R.id.textViewNome);
                    tv.setTextColor(ctx.getResources().getColor(R.color.branco));
                }

                if(ativa){
                    ativa = false;
                    binding.textViewNome.setTextColor(ctx.getResources().getColor(R.color.cinzaEscuro));
                    listener.onLegendaSelected(ativa, legenda.getItinerario());
                } else{
                    ativa = true;
                    binding.textViewNome.setTextColor(ctx.getResources().getColor(R.color.branco));
                    listener.onLegendaSelected(ativa, legenda.getItinerario());
                }

            }
        });

        binding.executePendingBindings();
    }
}
