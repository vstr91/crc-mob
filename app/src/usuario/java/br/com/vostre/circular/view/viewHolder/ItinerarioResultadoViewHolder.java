package br.com.vostre.circular.view.viewHolder;

import android.Manifest;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaItinerariosResultadoBinding;

import br.com.vostre.circular.listener.ParadaItinerarioListener;
import br.com.vostre.circular.listener.ParadaListener;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.utils.DataHoraUtils;
import br.com.vostre.circular.utils.DrawableUtils;
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
    ParadaItinerarioListener paradaListener;

    public ItinerarioResultadoViewHolder(LinhaItinerariosResultadoBinding binding, AppCompatActivity context, BaseActivity parent, ParadaItinerarioListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.parent = parent;
        this.paradaListener = listener;
    }

    public void bind(final ItinerarioPartidaDestino itinerario, int ordem, boolean ocultaSeta, String dia, String hora,
                     int quantidade, final ItinerarioPartidaDestino itinerarioSeguinte) {
        binding.setItinerario(itinerario);

        if(dia == null || dia.equals("") || hora == null || hora.equals("")){
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

        // local de embarque diferente do local inicial do itinerario
        if(itinerario.getNomePartida() != null && !itinerario.getNomePartida().isEmpty()){
            binding.textViewEmbarque.setText("Embarque em\n"+itinerario.getNomePartida());
            binding.textViewEmbarque.setVisibility(View.VISIBLE);
            binding.btnPontosEmbarque.setVisibility(View.VISIBLE);
        } else{
            binding.textViewEmbarque.setText("");
            binding.textViewEmbarque.setVisibility(View.GONE);
            binding.btnPontosEmbarque.setVisibility(View.GONE);
        }

        // local de desembarque diferente do local final do itinerario
        if(itinerario.getNomeDestino() != null && !itinerario.getNomeDestino().isEmpty()){
            binding.textViewDesembarque.setText("Desembarque em\n"+itinerario.getNomeDestino());
            binding.textViewDesembarque.setVisibility(View.VISIBLE);
            binding.btnPontosDesembarque.setVisibility(View.VISIBLE);
        } else{
            binding.textViewDesembarque.setText("");
            binding.textViewDesembarque.setVisibility(View.GONE);
            binding.btnPontosDesembarque.setVisibility(View.GONE);
        }

        // listener dos botoes de pontos de embarque e desembarque
        final View.OnClickListener listenerEmbarque = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(itinerarioSeguinte != null){
                    paradaListener.onSelected(itinerario.getItinerario().getId(), itinerario.getBairroConsultaPartida(),
                            itinerarioSeguinte.getItinerario().getId());
                } else{
                    paradaListener.onSelected(itinerario.getItinerario().getId(), itinerario.getBairroConsultaPartida(),
                            null);
                }

//                Toast.makeText(ctx, itinerario.getBairroConsultaPartida(), Toast.LENGTH_SHORT).show();
            }
        };

        final View.OnClickListener listenerDesembarque = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(itinerarioSeguinte != null){
                    paradaListener.onSelected(itinerario.getItinerario().getId(), itinerario.getBairroConsultaDestino(),
                            itinerarioSeguinte.getItinerario().getId());
                } else{
                    paradaListener.onSelected(itinerario.getItinerario().getId(), itinerario.getBairroConsultaDestino(),
                            null);
                }


//                Toast.makeText(ctx, itinerario.getBairroConsultaDestino(), Toast.LENGTH_SHORT).show();
            }
        };

        binding.textViewEmbarque.setOnClickListener(listenerEmbarque);
        binding.btnPontosEmbarque.setOnClickListener(listenerEmbarque);

        binding.textViewDesembarque.setOnClickListener(listenerDesembarque);
        binding.btnPontosDesembarque.setOnClickListener(listenerDesembarque);

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

                            if(itinerario.isTrechoIsolado()){
                                i.putExtra("itinerarioPartida", itinerario.getIdBairroPartida());
                                i.putExtra("itinerarioDestino", itinerario.getIdBairroDestino());

                                i.putExtra("paradaPartida", itinerario.getParadaPartida());
                                i.putExtra("paradaDestino", itinerario.getParadaDestino());

                                i.putExtra("trechoIsolado", true);

                                i.putExtra("partidaConsulta", itinerario.getBairroConsultaPartida());
                                i.putExtra("destinoConsulta", itinerario.getBairroConsultaDestino());
                            } else{
                                i.putExtra("itinerarioPartida", itinerario.getBairroConsultaPartida());
                                i.putExtra("itinerarioDestino", itinerario.getBairroConsultaDestino());

                                i.putExtra("paradaPartida", itinerario.getParadaPartida());
                                i.putExtra("paradaDestino", itinerario.getParadaDestino());
                            }


                            i.putExtra("horario", itinerario.getIdProximoHorario());
                            ctx.startActivity(i);
                        } else{
                            Toast.makeText(ctx.getApplicationContext(), "Acesso ao armazenamento externo é utilizado para " +
                                    "salvar partes do mapa e permitir o acesso offline. O mapa não funcionará corretamente sem essa permissão.", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(ctx, DetalheItinerarioActivity.class);
                            i.putExtra("itinerario", itinerario.getItinerario().getId());

                            if(itinerario.isTrechoIsolado()){
                                i.putExtra("itinerarioPartida", itinerario.getIdBairroPartida());
                                i.putExtra("itinerarioDestino", itinerario.getIdBairroDestino());
                            } else{
                                i.putExtra("itinerarioPartida", itinerario.getBairroConsultaPartida());
                                i.putExtra("itinerarioDestino", itinerario.getBairroConsultaDestino());
                            }

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
            binding.imageView19.setVisibility(View.VISIBLE);
        } else{
            binding.imageView19.setVisibility(View.GONE);
        }

//        final View.OnClickListener listenerParada = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(ctx, DetalheParadaActivity.class);
//                i.putExtra("parada", itinerario.getIdPartida());
//                ctx.startActivity(i);
//            }
//        };
//
//        binding.textViewParada.setOnClickListener(listenerParada);

//        String paramPartida = PreferenceUtils.carregarPreferencia(ctx, "param_mostrar_partida");

//        if(!paramPartida.equals("1")){
//            binding.textViewParada.setVisibility(View.GONE);
//            binding.imageView.setVisibility(View.GONE);
//        } else{
//            binding.textViewParada.setVisibility(View.VISIBLE);
//            binding.imageView.setVisibility(View.VISIBLE);
//        }

        String obsAnterior = "";
        String obsSeguinte = "";

        final String obsAnteriorFinal;
        final String obsSeguinteFinal;

        if((itinerario.getItinerarioAnterior() != null && !itinerario.isMesmoItinerario(itinerario.getItinerarioAnterior()))
                || (itinerario.getItinerarioSeguinte() != null && !itinerario.isMesmoItinerario(itinerario.getItinerarioSeguinte()))){

            if(itinerario.getObservacaoProximoHorario() == null || (itinerario.getObservacaoProximoHorario().isEmpty() ||
                    itinerario.getObservacaoProximoHorario().equals("null") || itinerario.getObservacaoProximoHorario().equals("")) &&
                    !itinerario.getItinerarioAnterior().getPartidaEDestinoResumido().equals(itinerario.getPartidaEDestinoResumido())){

                binding.textViewObservacao.setText(itinerario.getPartidaEDestinoResumido());
            } else{
                binding.textViewObservacao.setText(itinerario.getPartidaEDestinoResumido()+" ("+itinerario.getObservacaoProximoHorario()+")");
            }

            if(itinerario.getObservacaoHorarioAnterior() == null || (itinerario.getObservacaoHorarioAnterior().isEmpty() ||
                    itinerario.getObservacaoHorarioAnterior().equals("null") || itinerario.getObservacaoHorarioAnterior().equals(""))){
                //binding.textViewObervacaoAnterior.setText(itinerario.getItinerarioAnterior().getPartidaEDestinoResumido());

                if(itinerario.getItinerarioAnterior() != null && itinerario.getItinerarioAnterior().getItinerario().getObservacao() != null
                        && !itinerario.getItinerarioAnterior().getItinerario().getObservacao().isEmpty()){
                    obsAnterior = itinerario.getItinerarioAnterior().getPartidaEDestinoResumido()+" ("
                            +itinerario.getItinerarioAnterior().getItinerario().getObservacao()+")";
                } else if(itinerario.getItinerarioAnterior() != null){
                    obsAnterior = itinerario.getItinerarioAnterior().getPartidaEDestinoResumido();
                }


                binding.btnObsAnterior.setVisibility(View.VISIBLE);
            } else{
//                binding.textViewObervacaoAnterior.setText(itinerario.getItinerarioAnterior().getPartidaEDestinoResumido()
//                        +" ("+itinerario.getObservacaoHorarioAnterior()+")");

                if(itinerario.getItinerarioAnterior() != null && itinerario.getItinerarioAnterior().getItinerario().getObservacao() != null
                        && !itinerario.getItinerarioAnterior().getItinerario().getObservacao().isEmpty()){
                    obsAnterior = itinerario.getItinerarioAnterior().getPartidaEDestinoResumido()
                            +" ("+itinerario.getItinerarioAnterior().getItinerario().getObservacao()+" - "
                            +itinerario.getObservacaoHorarioAnterior()+")";
                } else if(itinerario.getItinerarioAnterior() != null){
                    obsAnterior = itinerario.getItinerarioAnterior().getPartidaEDestinoResumido()
                            +" ("+itinerario.getObservacaoHorarioAnterior()+")";
                }


                binding.btnObsAnterior.setVisibility(View.VISIBLE);
            }

            if(itinerario.getObservacaoHorarioSeguinte() == null || (itinerario.getObservacaoHorarioSeguinte().isEmpty() ||
                    itinerario.getObservacaoHorarioSeguinte().equals("null") || itinerario.getObservacaoHorarioSeguinte().equals(""))){
//                binding.textViewObervacaoSeguinte.setText(itinerario.getItinerarioSeguinte().getPartidaEDestinoResumido());

                if(itinerario.getItinerarioSeguinte() != null && itinerario.getItinerarioSeguinte().getItinerario().getObservacao() != null
                        && !itinerario.getItinerarioSeguinte().getItinerario().getObservacao().isEmpty()){
                    obsSeguinte = itinerario.getItinerarioSeguinte().getPartidaEDestinoResumido()+" ("
                            +itinerario.getItinerarioSeguinte().getItinerario().getObservacao()+")";
                } else if(itinerario.getItinerarioSeguinte() != null){
                    obsSeguinte = itinerario.getItinerarioSeguinte().getPartidaEDestinoResumido();
                }

                binding.btnObsSeguinte.setVisibility(View.VISIBLE);
            } else{
//                binding.textViewObervacaoSeguinte.setText(itinerario.getItinerarioSeguinte().getPartidaEDestinoResumido()
//                        +" ("+itinerario.getObservacaoHorarioSeguinte()+")");

                if(itinerario.getItinerarioSeguinte() != null && itinerario.getItinerarioSeguinte().getItinerario().getObservacao() != null
                        && !itinerario.getItinerarioSeguinte().getItinerario().getObservacao().isEmpty()){
                    obsSeguinte = itinerario.getItinerarioSeguinte().getPartidaEDestinoResumido()
                            +" ("+itinerario.getItinerarioSeguinte().getItinerario().getObservacao()+" - "
                            +itinerario.getObservacaoHorarioSeguinte()+")";
                } else if(itinerario.getItinerarioSeguinte() != null){
                    obsSeguinte = itinerario.getItinerarioSeguinte().getPartidaEDestinoResumido()
                            +" ("+itinerario.getObservacaoHorarioSeguinte()+")";
                }

                binding.btnObsSeguinte.setVisibility(View.VISIBLE);
            }

            binding.textViewObservacao.setVisibility(View.VISIBLE);
//            binding.textViewObervacaoAnterior.setVisibility(View.VISIBLE);
//            binding.textViewObervacaoSeguinte.setVisibility(View.VISIBLE);


        } else{

            // Comum - mesmo itinerario

            if(itinerario.getObservacaoProximoHorario() == null || (itinerario.getObservacaoProximoHorario().isEmpty() ||
                    itinerario.getObservacaoProximoHorario().equals("null") || itinerario.getObservacaoProximoHorario().equals(""))){
                binding.textViewObservacao.setText("");
                binding.textViewObservacao.setVisibility(View.GONE);
            } else{
                binding.textViewObservacao.setText(itinerario.getObservacaoProximoHorario());
                binding.textViewObservacao.setVisibility(View.VISIBLE);
            }

            if(itinerario.getItinerario().getObservacao() == null || (itinerario.getItinerario().getObservacao().isEmpty() ||
                    itinerario.getItinerario().getObservacao().equals("null") || itinerario.getItinerario().getObservacao().equals(""))){
                binding.textViewObservacaoItinerario.setText("");
                binding.textViewObservacaoItinerario.setVisibility(View.GONE);
            } else{
                binding.textViewObservacaoItinerario.setText(itinerario.getItinerario().getObservacao());
                binding.textViewObservacaoItinerario.setVisibility(View.VISIBLE);
            }

            // OBSERVACAO ANTERIOR

            String obsItinerarioAnterior = "";
            String obsHorarioAnterior = "";

            if(itinerario.getObsHorarioAnterior() != null){
                obsItinerarioAnterior = itinerario.getObsHorarioAnterior();
                obsHorarioAnterior = itinerario.getObservacaoHorarioAnterior();

                String obsAnteriorCompleta = "";

                if(obsItinerarioAnterior != null && !obsItinerarioAnterior.isEmpty()){
                    obsAnteriorCompleta = obsItinerarioAnterior;
                }

                if(obsHorarioAnterior != null && !obsHorarioAnterior.isEmpty()){

                    if(obsAnteriorCompleta.isEmpty()){
                        obsAnteriorCompleta = obsHorarioAnterior;
                    } else{
                        obsAnteriorCompleta = obsAnteriorCompleta.concat(" ("+obsHorarioAnterior+")");
                    }

                }

                if(!obsAnteriorCompleta.isEmpty()){
                    obsAnterior = obsAnteriorCompleta;
                    binding.btnObsAnterior.setVisibility(View.VISIBLE);
                } else{
                    binding.btnObsAnterior.setVisibility(View.GONE);
                }

            } else{
                binding.btnObsAnterior.setVisibility(View.GONE);
            }

            // FIM OBSERVACAO ANTERIOR

            // OBSERVACAO SEGUINTE

            String obsItinerarioSeguinte = "";
            String obsHorarioSeguinte = "";

            if(itinerario.getObsHorarioSeguinte() != null){
                obsItinerarioSeguinte = itinerario.getObsHorarioSeguinte();
                obsHorarioSeguinte = itinerario.getObservacaoHorarioSeguinte();

                String obsSeguinteCompleta = "";

                if(obsItinerarioSeguinte != null && !obsItinerarioSeguinte.isEmpty()){
                    obsSeguinteCompleta = obsItinerarioSeguinte;
                }

                if(obsHorarioSeguinte != null && !obsHorarioSeguinte.isEmpty()){

                    if(obsSeguinteCompleta.isEmpty()){
                        obsSeguinteCompleta = obsHorarioSeguinte;
                    } else{
                        obsSeguinteCompleta = obsSeguinteCompleta.concat(" ("+obsHorarioSeguinte+")");
                    }

                }

                if(!obsSeguinteCompleta.isEmpty()){
                    obsSeguinte = obsSeguinteCompleta;
                    binding.btnObsSeguinte.setVisibility(View.VISIBLE);
                } else{
                    binding.btnObsSeguinte.setVisibility(View.GONE);
                }

            } else{
                binding.btnObsSeguinte.setVisibility(View.GONE);
            }

            // FIM OBSERVACAO SEGUINTE

//            if(itinerario.getObservacaoHorarioAnterior() == null || (itinerario.getObservacaoHorarioAnterior().isEmpty() ||
//                    itinerario.getObservacaoHorarioAnterior().equals("null") || itinerario.getObservacaoHorarioAnterior().equals(""))){
////                binding.textViewObervacaoAnterior.setText("");
////                binding.textViewObervacaoAnterior.setVisibility(View.GONE);
//
//                binding.btnObsAnterior.setVisibility(View.GONE);
//            } else{
////                binding.textViewObervacaoAnterior.setText(itinerario.getObservacaoHorarioAnterior());
////                binding.textViewObervacaoAnterior.setVisibility(View.VISIBLE);
//                obsAnterior = itinerario.getObservacaoHorarioAnterior();
//                binding.btnObsAnterior.setVisibility(View.VISIBLE);
//            }
//
//            if(itinerario.getObservacaoHorarioSeguinte() == null || (itinerario.getObservacaoHorarioSeguinte().isEmpty() ||
//                    itinerario.getObservacaoHorarioSeguinte().equals("null") || itinerario.getObservacaoHorarioSeguinte().equals(""))){
//                binding.textViewObervacaoSeguinte.setText("");
//                binding.textViewObervacaoSeguinte.setVisibility(View.GONE);
//                binding.btnObsSeguinte.setVisibility(View.GONE);
//            } else{
////                binding.textViewObervacaoSeguinte.setText(itinerario.getObservacaoHorarioSeguinte());
////                binding.textViewObervacaoSeguinte.setVisibility(View.VISIBLE);
//                obsSeguinte = itinerario.getObservacaoHorarioSeguinte();
//                binding.btnObsSeguinte.setVisibility(View.VISIBLE);
//            }

        }

        if(itinerario.getDistanciaTrechoMetros() == null && itinerario.getDistanciaTrecho() != null){
            itinerario.setDistanciaTrechoMetros(itinerario.getDistanciaTrecho()*1000);
        }

        if(itinerario.getItinerario().getDistanciaMetros() == null && itinerario.getItinerario().getDistancia() != null){
            itinerario.getItinerario().setDistanciaMetros(itinerario.getItinerario().getDistancia()*1000);
        }

        // ALIAS ITINERARIO

        if(itinerario.getItinerario().getAliasBairroPartida() != null && !itinerario.getItinerario().getAliasBairroPartida().isEmpty()){
            itinerario.setNomeBairroPartida(itinerario.getItinerario().getAliasBairroPartida());
        }

        if(itinerario.getItinerario().getAliasCidadePartida() != null && !itinerario.getItinerario().getAliasCidadePartida().isEmpty()){
            itinerario.setNomeCidadePartida(itinerario.getItinerario().getAliasCidadePartida());
        }

        if(itinerario.getItinerario().getAliasBairroDestino() != null && !itinerario.getItinerario().getAliasBairroDestino().isEmpty()){
            itinerario.setNomeBairroDestino(itinerario.getItinerario().getAliasBairroDestino());
        }

        if(itinerario.getItinerario().getAliasCidadeDestino() != null && !itinerario.getItinerario().getAliasCidadeDestino().isEmpty()){
            itinerario.setNomeCidadeDestino(itinerario.getItinerario().getAliasCidadeDestino());
        }

        // FIM ALIAS

        obsAnteriorFinal = obsAnterior;
        obsSeguinteFinal = obsSeguinte;

        View.OnClickListener listenerAnterior = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, obsAnteriorFinal, Toast.LENGTH_SHORT).show();
            }
        };

        View.OnClickListener listenerSeguinte = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, obsSeguinteFinal, Toast.LENGTH_SHORT).show();
            }
        };

        binding.btnObsAnterior.setOnClickListener(listenerAnterior);
        binding.btnObsSeguinte.setOnClickListener(listenerSeguinte);

        if(itinerario.getItinerario().getAcessivel()){
            binding.imageView8.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_accessible_blue_24dp));
        } else{
            binding.imageView8.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_baseline_accessible_gray_24));
        }

        binding.executePendingBindings();
    }

}
