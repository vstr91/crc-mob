package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import br.com.vostre.circular.databinding.LinhaItinerariosResultadoBinding;

import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.utils.DataHoraUtils;
import br.com.vostre.circular.view.BaseActivity;
import br.com.vostre.circular.view.DetalheItinerarioActivity;
import br.com.vostre.circular.view.DetalheParadaActivity;
import br.com.vostre.circular.view.form.FormCalendario;

public class ItinerarioResultadoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaItinerariosResultadoBinding binding;
    AppCompatActivity ctx;
    BaseActivity parent;

    public ItinerarioResultadoViewHolder(LinhaItinerariosResultadoBinding binding, AppCompatActivity context, BaseActivity parent) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.parent = parent;
    }

    public void bind(final ItinerarioPartidaDestino itinerario, int ordem, boolean ocultaSeta, String dia, String hora) {
        binding.setItinerario(itinerario);

//        if(!itinerario.getItinerario().getAcessivel()){
//            binding.imageView12.setVisibility(View.GONE);
//        }

        if(itinerario.getItinerario().getObservacao() == null || (itinerario.getItinerario().getObservacao().isEmpty() ||
                itinerario.getItinerario().getObservacao().equals("null") || itinerario.getItinerario().getObservacao().equals(""))){
            binding.textViewObservacao.setVisibility(View.GONE);
        }

        binding.textViewOrdem.setText(String.valueOf(ordem));

        if(dia.equals("") || hora.equals("")){
            dia = DataHoraUtils.getDiaAtualFormatado();
            hora = DataHoraUtils.getHoraAtual();
        }

        binding.setDia(dia);
        binding.setHora(hora);

        if(ocultaSeta){
            binding.imageView12.setVisibility(View.GONE);
        } else{
            binding.imageView12.setVisibility(View.VISIBLE);
        }

        //binding.circleView2.setImagem(null);

//        final File brasao = new File(ctx.getApplicationContext().getFilesDir(),  cidade.getCidade().getBrasao());
//
//        if(brasao.exists() && brasao.canRead()){
//            final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
//            binding.circleView2.setImagem(drawable);
//        }
//
//        binding.textViewNome.setText(cidade.getCidade().getNome());

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, DetalheItinerarioActivity.class);
                i.putExtra("itinerario", itinerario.getItinerario().getId());
                ctx.startActivity(i);
            }
        };
//
        binding.btnVerTodos.setOnClickListener(listener);
//        binding.textViewNome.setOnClickListener(listener);

        if(ordem == 1){
            final View.OnClickListener listenerHora = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FormCalendario formCalendario = new FormCalendario();
                    formCalendario.setListener(parent);
                    formCalendario.show(ctx.getSupportFragmentManager(), "formCalendario");
                }
            };

            binding.linearLayoutHora.setOnClickListener(listenerHora);
        }

        final View.OnClickListener listenerParada = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, DetalheParadaActivity.class);
                i.putExtra("parada", itinerario.getIdPartida());
                ctx.startActivity(i);
            }
        };
//
        binding.textViewParada.setOnClickListener(listenerParada);

        binding.executePendingBindings();
    }

}
