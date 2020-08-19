package br.com.vostre.circular.view.adapter;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaPontosInteresseBinding;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.viewHolder.PontoInteresseViewHolder;
import br.com.vostre.circular.view.viewHolder.SecaoItinerarioViewHolder;

public class PontosInteresseAdapter extends RecyclerView.Adapter<PontoInteresseViewHolder> {

    public List<PontoInteresse> pois;
    AppCompatActivity ctx;
    ParadaBairro parada;
    BottomSheetDialog bsd;

    public PontosInteresseAdapter(List<PontoInteresse> pois, AppCompatActivity context, ParadaBairro parada, BottomSheetDialog bsd){
        this.pois = pois;
        ctx = context;
        this.parada = parada;
        this.bsd = bsd;
    }

    @Override
    public PontoInteresseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaPontosInteresseBinding itemBinding =
                LinhaPontosInteresseBinding.inflate(layoutInflater, parent, false);
        return new PontoInteresseViewHolder(itemBinding, ctx, parada, bsd);
    }

    @Override
    public void onBindViewHolder(PontoInteresseViewHolder holder, int position) {
        PontoInteresse poi = pois.get(position);
        holder.bind(poi);
    }

    @Override
    public int getItemCount() {

        if(pois == null){
            return 0;
        } else{
            return pois.size();
        }


    }
}
