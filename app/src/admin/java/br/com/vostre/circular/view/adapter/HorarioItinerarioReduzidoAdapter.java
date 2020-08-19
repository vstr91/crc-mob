package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaHorariosItinerariosReduzidaBinding;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.view.viewHolder.HorarioItinerarioReduzidoViewHolder;
import br.com.vostre.circular.viewModel.HorariosItinerarioViewModel;

public class HorarioItinerarioReduzidoAdapter extends RecyclerView.Adapter<HorarioItinerarioReduzidoViewHolder> {

    public List<HorarioItinerarioNome> horarios;
    AppCompatActivity ctx;
    HorariosItinerarioViewModel viewModel;
    boolean clickListener = false;
    public List<HorarioItinerarioNome> horariosAtuais;
    boolean isHorariosAtuais = false;

    public HorarioItinerarioReduzidoAdapter(List<HorarioItinerarioNome> horarios, AppCompatActivity context,
                                            HorariosItinerarioViewModel viewModel, boolean clickListener,
                                            List<HorarioItinerarioNome> horariosAtuais, boolean isHorariosAtuais){
        this.horarios = horarios;
        ctx = context;
        this.viewModel = viewModel;
        this.clickListener = clickListener;
        this.horariosAtuais = horariosAtuais;
        this.isHorariosAtuais = isHorariosAtuais;
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
            holder.bind(horario, true, isHorariosAtuais);
        } else{
            holder.bind(horario, false, isHorariosAtuais);
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
