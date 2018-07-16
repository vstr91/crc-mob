package br.com.vostre.circular.view.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaHorariosBinding;
import br.com.vostre.circular.databinding.LinhaPaisesBinding;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.view.viewHolder.HorarioViewHolder;

public class HorarioAdapter extends RecyclerView.Adapter<HorarioViewHolder> {

    public List<Horario> horarios;
    AppCompatActivity ctx;

    public HorarioAdapter(List<Horario> horarios, AppCompatActivity context){
        this.horarios = horarios;
        ctx = context;
    }

    @Override
    public HorarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaHorariosBinding itemBinding =
                LinhaHorariosBinding.inflate(layoutInflater, parent, false);
        return new HorarioViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(HorarioViewHolder holder, int position) {
        Horario horario = horarios.get(position);
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
