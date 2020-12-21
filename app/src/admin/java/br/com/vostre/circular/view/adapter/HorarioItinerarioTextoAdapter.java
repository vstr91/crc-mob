package br.com.vostre.circular.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaHorariosItinerariosBinding;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.view.viewHolder.HorarioItinerarioTextoViewHolder;
import br.com.vostre.circular.view.viewHolder.HorarioItinerarioViewHolder;
import br.com.vostre.circular.viewModel.HorariosItinerarioTextoViewModel;
import br.com.vostre.circular.viewModel.HorariosItinerarioViewModel;

public class HorarioItinerarioTextoAdapter extends RecyclerView.Adapter<HorarioItinerarioTextoViewHolder> {

    public List<HorarioItinerarioNome> horarios;
    AppCompatActivity ctx;
    HorariosItinerarioTextoViewModel viewModel;
    boolean clickListener = false;

    public HorarioItinerarioTextoAdapter(List<HorarioItinerarioNome> horarios, AppCompatActivity context,
                                         HorariosItinerarioTextoViewModel viewModel, boolean clickListener){
        this.horarios = horarios;
        ctx = context;
        this.viewModel = viewModel;
        this.clickListener = clickListener;
    }

    @Override
    public HorarioItinerarioTextoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaHorariosItinerariosBinding itemBinding =
                LinhaHorariosItinerariosBinding.inflate(layoutInflater, parent, false);
        return new HorarioItinerarioTextoViewHolder(itemBinding, ctx, viewModel, this.clickListener);
    }

    @Override
    public void onBindViewHolder(HorarioItinerarioTextoViewHolder holder, int position) {
        HorarioItinerarioNome horario = horarios.get(position);
        holder.bind(horario);
    }

    @Override
    public int getItemCount() {

        if(horarios == null){
            return 0;
        } else{
            return horarios.size();
        }


    }
}
