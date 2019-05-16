package br.com.vostre.circular.view.viewHolder;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaItinerariosResultadoBinding;

import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.utils.DataHoraUtils;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.view.BaseActivity;
import br.com.vostre.circular.view.DetalheItinerarioActivity;
import br.com.vostre.circular.view.DetalheParadaActivity;
import br.com.vostre.circular.view.MapaActivity;
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

    public void bind(final ItinerarioPartidaDestino itinerario, int ordem, boolean ocultaSeta, String dia, String hora, int quantidade) {
        binding.setItinerario(itinerario);

//        if(!itinerario.getItinerario().getAcessivel()){
//            binding.imageView12.setVisibility(View.GONE);
//        }

//        if(itinerario.getItinerario().getObservacao() == null || (itinerario.getItinerario().getObservacao().isEmpty() ||
//                itinerario.getItinerario().getObservacao().equals("null") || itinerario.getItinerario().getObservacao().equals(""))){
//            binding.textViewObservacao.setVisibility(View.GONE);
//        }

        if(quantidade == 1){
            binding.textViewOrdem.setVisibility(View.GONE);
        } else{
            binding.textViewOrdem.setVisibility(View.VISIBLE);
        }

        if(itinerario.getObservacaoProximoHorario() == null || (itinerario.getObservacaoProximoHorario().isEmpty() ||
                itinerario.getObservacaoProximoHorario().equals("null") || itinerario.getObservacaoProximoHorario().equals(""))){
            binding.textViewObservacao.setVisibility(View.GONE);
        } else{
            binding.textViewObservacao.setVisibility(View.VISIBLE);
        }

        if(itinerario.getObservacaoHorarioAnterior() == null || (itinerario.getObservacaoHorarioAnterior().isEmpty() ||
                itinerario.getObservacaoHorarioAnterior().equals("null") || itinerario.getObservacaoHorarioAnterior().equals(""))){
            binding.textViewObervacaoAnterior.setVisibility(View.GONE);
        } else{
            binding.textViewObervacaoAnterior.setVisibility(View.VISIBLE);
        }

        if(itinerario.getObservacaoHorarioSeguinte() == null || (itinerario.getObservacaoHorarioSeguinte().isEmpty() ||
                itinerario.getObservacaoHorarioSeguinte().equals("null") || itinerario.getObservacaoHorarioSeguinte().equals(""))){
            binding.textViewObervacaoSeguinte.setVisibility(View.GONE);
        } else{
            binding.textViewObervacaoSeguinte.setVisibility(View.VISIBLE);
        }

        binding.textViewOrdem.setText(String.valueOf(ordem));

        if(dia.equals("") || hora.equals("")){
            dia = DataHoraUtils.getDiaAtualFormatado();
            hora = DataHoraUtils.getHoraAtual();
        }

        if(!dia.isEmpty()){
            dia = DataHoraUtils.getDiaFormatado(dia);
        }

        if(!hora.isEmpty() && hora.length() == 8){
            hora = DataHoraUtils.getHoraFormatada(hora);
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

                Dexter.withActivity(ctx)
                        .withPermissions(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted()){
                            Intent i = new Intent(ctx, DetalheItinerarioActivity.class);
                            i.putExtra("itinerario", itinerario.getItinerario().getId());
                            i.putExtra("itinerarioPartida", itinerario.getBairroConsultaPartida());
                            i.putExtra("itinerarioDestino", itinerario.getBairroConsultaDestino());

                            i.putExtra("paradaPartida", itinerario.getParadaPartida());
                            i.putExtra("paradaDestino", itinerario.getParadaDestino());
                            i.putExtra("horario", itinerario.getIdProximoHorario());
                            ctx.startActivity(i);
                        } else{
                            Toast.makeText(ctx.getApplicationContext(), "Acesso ao armazenamento externo é utilizado para " +
                                    "salvar partes do mapa e permitir o acesso offline. O mapa não funcionará corretamente sem essa permissão.", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(ctx, DetalheItinerarioActivity.class);
                            i.putExtra("itinerario", itinerario.getItinerario().getId());
                            i.putExtra("itinerarioPartida", itinerario.getBairroConsultaPartida());
                            i.putExtra("itinerarioDestino", itinerario.getBairroConsultaDestino());

                            i.putExtra("paradaPartida", itinerario.getParadaPartida());
                            i.putExtra("paradaDestino", itinerario.getParadaDestino());
                            i.putExtra("horario", itinerario.getIdProximoHorario());
                            ctx.startActivity(i);
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
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

        String paramPartida = PreferenceUtils.carregarPreferencia(ctx, "param_mostrar_partida");

        if(!paramPartida.equals("1")){
            binding.textViewParada.setVisibility(View.GONE);
            binding.imageView.setVisibility(View.GONE);
        } else{
            binding.textViewParada.setVisibility(View.VISIBLE);
            binding.imageView.setVisibility(View.VISIBLE);
        }

        binding.executePendingBindings();
    }

}
