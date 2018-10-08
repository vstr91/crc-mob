package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.databinding.LinhaHorariosItinerariosBinding;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.Legenda;
import br.com.vostre.circular.view.viewHolder.HorarioItinerarioViewHolder;

public class HorarioItinerarioAdapter extends RecyclerView.Adapter<HorarioItinerarioViewHolder> {

    public List<HorarioItinerarioNome> horarios;
    public List<HorarioItinerarioNome> horariosFiltrados = null;
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

    public void filtrarHorarios(String itinerario){

        horariosFiltrados = new ArrayList<>();

        for(HorarioItinerarioNome horario : horarios){

            if(!horario.getHorarioItinerario().getItinerario().equals(itinerario)){
                horariosFiltrados.add(horario);
            }

        }

        notifyDataSetChanged();

    }

    public void usarDadosOriginais(){

        horariosFiltrados = null;

        notifyDataSetChanged();

    }

    public HorarioItinerarioNome buscaPosicaoHorario(String horario, String itinerario){

        for(HorarioItinerarioNome h : horarios){

            if(h.getHorarioItinerario().getHorario().equals(horario)){
                return h;
            }

        }

        return null;
    }

    @Override
    public void onBindViewHolder(HorarioItinerarioViewHolder holder, int position) {

        HorarioItinerarioNome horario;

        if(horariosFiltrados != null){
            horario = horariosFiltrados.get(position);
        } else{
            horario = horarios.get(position);
        }

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

        if(horariosFiltrados != null){
            return horariosFiltrados.size();
        } else{
            if(horarios == null){
                return 0;
            } else{
                return horarios.size();
            }
        }




    }
}
