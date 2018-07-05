package br.com.vostre.circular.view.viewHolder;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaParadasItinerariosBinding;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;
import br.com.vostre.circular.view.form.FormParadaDetalheItinerario;
import br.com.vostre.circular.view.form.FormParadaItinerario;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;

public class ParadaItinerarioViewHolder extends RecyclerView.ViewHolder {

    private final LinhaParadasItinerariosBinding binding;
    AppCompatActivity ctx;
    AndroidViewModel viewModel;
    public Boolean edicaoItinerario = false;

    public ParadaItinerarioViewHolder(LinhaParadasItinerariosBinding binding, AppCompatActivity context,
                                      @Nullable Boolean edicaoItinerario) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.edicaoItinerario = edicaoItinerario;

        if(edicaoItinerario){
            viewModel = ViewModelProviders.of(ctx).get(DetalhesItinerarioViewModel.class);
        } else{
            viewModel = ViewModelProviders.of(ctx).get(ItinerariosViewModel.class);
        }


    }

    public void bind(final ParadaItinerarioBairro parada) {
        binding.setParada(parada);

        if(parada.getParadaItinerario().getProgramadoPara() != null && parada.getParadaItinerario().getProgramadoPara().isAfterNow()){
            binding.btnProgramado.setVisibility(View.VISIBLE);
            binding.btnProgramado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
                            .print(parada.getParadaItinerario().getProgramadoPara()), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            binding.btnProgramado.setVisibility(View.GONE);
        }

        if(parada.getParadaItinerario().getValorAnterior() == null){
            binding.textViewAnterior.setText("-");
        }

        if(parada.getParadaItinerario().getValorAnterior() == null){
            binding.textViewProximo.setText("-");
        }

        binding.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edicaoItinerario){
                    FormParadaDetalheItinerario formParada = new FormParadaDetalheItinerario();
                    formParada.setParada(parada);
                    formParada.setCtx(ctx.getApplication());
                    formParada.edicaoItinerario = true;
                    formParada.flagInicioEdicao = true;
                    formParada.show(ctx.getSupportFragmentManager(), "formParada");
                } else{
                    FormParadaItinerario formParada = new FormParadaItinerario();
                    formParada.setParada(parada);
                    formParada.setCtx(ctx.getApplication());
                    formParada.flagInicioEdicao = true;
                    formParada.show(ctx.getSupportFragmentManager(), "formParada");
                }

            }
        });

        binding.executePendingBindings();
    }
}
