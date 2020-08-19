package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaFeriadosBinding;
import br.com.vostre.circular.model.Feriado;
import br.com.vostre.circular.view.viewHolder.FeriadoViewHolder;

public class FeriadoAdapter extends RecyclerView.Adapter<FeriadoViewHolder> {

    public List<Feriado> feriados;
    AppCompatActivity ctx;
    String anoAtual;

    public FeriadoAdapter(List<Feriado> feriados, AppCompatActivity context){
        this.feriados = feriados;
        ctx = context;
        anoAtual = "";
    }

    @Override
    public FeriadoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaFeriadosBinding itemBinding =
                LinhaFeriadosBinding.inflate(layoutInflater, parent, false);
        return new FeriadoViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(FeriadoViewHolder holder, int position) {

        int i = position-1;
        Feriado feriadoAnterior = null;

        if(i >= 0){
            feriadoAnterior = feriados.get(i);
        }

        final Feriado feriado = feriados.get(position);

        if(feriadoAnterior != null){
            holder.bind(feriado, feriadoAnterior.getAno());
        } else{
            holder.bind(feriado, "");
        }

    }

    @Override
    public int getItemCount() {

        if(feriados == null){
            return 0;
        } else{
            return feriados.size();
        }


    }
}
