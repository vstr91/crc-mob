package br.com.vostre.circular.view.viewHolder;

import androidx.databinding.BindingAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaHorariosBinding;
import br.com.vostre.circular.model.Horario;

public class HorarioViewHolder extends RecyclerView.ViewHolder {

    private final LinhaHorariosBinding binding;
    AppCompatActivity ctx;

    public HorarioViewHolder(LinhaHorariosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final Horario horario) {
        binding.setHorario(horario);

        if(horario.getProgramadoPara() != null && horario.getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(horario.getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        binding.executePendingBindings();
    }

    @BindingAdapter("text")
    public static void setText(TextView view, DateTime date) {
        String formatted = DateTimeFormat.forPattern("HH:mm").print(date);
        view.setText(formatted);
    }

}
