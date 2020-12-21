package br.com.vostre.circular.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaParadasBottomSheetBinding;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.viewHolder.ParadaBottomSheetViewHolder;

public class ParadaBottomSheetAdapter extends RecyclerView.Adapter<ParadaBottomSheetViewHolder> {

    public List<ParadaBairro> paradas;
    AppCompatActivity ctx;
    public String bairroAtual;

    public ParadaBottomSheetAdapter(List<ParadaBairro> paradas, AppCompatActivity context){
        this.paradas = paradas;
        ctx = context;
        bairroAtual = "";
    }

    @Override
    public ParadaBottomSheetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaParadasBottomSheetBinding itemBinding =
                LinhaParadasBottomSheetBinding.inflate(layoutInflater, parent, false);
        return new ParadaBottomSheetViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ParadaBottomSheetViewHolder holder, int position) {
        int i = position-1;
        ParadaBairro paradaAnterior = null;

        if(i >= 0){
            paradaAnterior = this.paradas.get(i);
        }

        final ParadaBairro parada = this.paradas.get(position);

        if(paradaAnterior != null){
            holder.bind(parada, paradaAnterior.getNomeBairro());
        } else{
            holder.bind(parada, "");
        }


        //bairroAtual = parada.getNomeBairro();


    }

    @Override
    public int getItemCount() {

        if(paradas == null){
            return 0;
        } else{
            return paradas.size();
        }


    }


}
