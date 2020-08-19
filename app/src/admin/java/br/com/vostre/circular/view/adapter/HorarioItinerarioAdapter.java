package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaHorariosItinerariosBinding;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.view.viewHolder.HorarioItinerarioViewHolder;
import br.com.vostre.circular.viewModel.HorariosItinerarioViewModel;

public class HorarioItinerarioAdapter extends RecyclerView.Adapter<HorarioItinerarioViewHolder> {

    public List<HorarioItinerarioNome> horarios;
    AppCompatActivity ctx;
    HorariosItinerarioViewModel viewModel;
    boolean clickListener = false;

    public HorarioItinerarioAdapter(List<HorarioItinerarioNome> horarios, AppCompatActivity context,
                                    HorariosItinerarioViewModel viewModel, boolean clickListener){
        this.horarios = horarios;
        ctx = context;
        this.viewModel = viewModel;
        this.clickListener = clickListener;
    }

    @Override
    public HorarioItinerarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaHorariosItinerariosBinding itemBinding =
                LinhaHorariosItinerariosBinding.inflate(layoutInflater, parent, false);
        return new HorarioItinerarioViewHolder(itemBinding, ctx, viewModel, this.clickListener);
    }

    @Override
    public void onBindViewHolder(HorarioItinerarioViewHolder holder, int position) {
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
