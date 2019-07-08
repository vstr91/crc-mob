package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaHorariosItinerariosBinding;
import br.com.vostre.circular.databinding.LinhaHorariosItinerariosReduzidaBinding;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.view.viewHolder.HorarioItinerarioReduzidoViewHolder;
import br.com.vostre.circular.view.viewHolder.HorarioItinerarioViewHolder;
import br.com.vostre.circular.viewModel.HorariosItinerarioViewModel;

public class HorarioItinerarioReduzidoAdapter extends RecyclerView.Adapter<HorarioItinerarioReduzidoViewHolder> {

    public List<HorarioItinerarioNome> horarios;
    AppCompatActivity ctx;
    HorariosItinerarioViewModel viewModel;
    boolean clickListener = false;
    public List<HorarioItinerarioNome> horariosAtuais;

    public HorarioItinerarioReduzidoAdapter(List<HorarioItinerarioNome> horarios, AppCompatActivity context,
                                            HorariosItinerarioViewModel viewModel, boolean clickListener,
                                            List<HorarioItinerarioNome> horariosAtuais){
        this.horarios = horarios;
        ctx = context;
        this.viewModel = viewModel;
        this.clickListener = clickListener;
        this.horariosAtuais = horariosAtuais;
    }

    @Override
    public HorarioItinerarioReduzidoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaHorariosItinerariosReduzidaBinding itemBinding =
                LinhaHorariosItinerariosReduzidaBinding.inflate(layoutInflater, parent, false);
        return new HorarioItinerarioReduzidoViewHolder(itemBinding, ctx, viewModel, this.clickListener);
    }

    @Override
    public void onBindViewHolder(HorarioItinerarioReduzidoViewHolder holder, int position) {
        HorarioItinerarioNome horario = horarios.get(position);

        if(horariosAtuais != null && horariosAtuais.indexOf(horario) > -1){
            holder.bind(horario, true);
        } else{
            holder.bind(horario, false);
        }

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
