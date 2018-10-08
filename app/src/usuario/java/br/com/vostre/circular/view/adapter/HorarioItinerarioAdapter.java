package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaHorariosItinerariosBinding;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.Legenda;
import br.com.vostre.circular.view.viewHolder.HorarioItinerarioViewHolder;

public class HorarioItinerarioAdapter extends RecyclerView.Adapter<HorarioItinerarioViewHolder> {

    public List<HorarioItinerarioNome> horarios;
    public List<Legenda> legenda;
    AppCompatActivity ctx;

    public HorarioItinerarioAdapter(List<HorarioItinerarioNome> horarios, AppCompatActivity context){
        this.horarios = horarios;
        ctx = context;
    }

    @Override
    public HorarioItinerarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaHorariosItinerariosBinding itemBinding =
                LinhaHorariosItinerariosBinding.inflate(layoutInflater, parent, false);
        return new HorarioItinerarioViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(HorarioItinerarioViewHolder holder, int position) {
        HorarioItinerarioNome horario = horarios.get(position);

        Legenda l = null;

        if(legenda != null && legenda.size() > 0){
            l = new Legenda();
            l.setItinerario(horario.getHorarioItinerario().getItinerario());
            l = legenda.get(legenda.indexOf(l));
            holder.bind(horario, l);
        } else{
            holder.bind(horario);
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
