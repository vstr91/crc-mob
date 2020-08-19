package br.com.vostre.circular.view.viewHolder;

import android.Manifest;
import android.content.Intent;
import androidx.databinding.BindingAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaItinerariosBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.DetalheItinerarioActivity;
import br.com.vostre.circular.view.DetalheParadaActivity;

public class ItinerarioViewHolder extends RecyclerView.ViewHolder {

    private final LinhaItinerariosBinding binding;
    AppCompatActivity ctx;

    public ItinerarioViewHolder(LinhaItinerariosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ItinerarioPartidaDestino itinerario) {
        binding.setItinerario(itinerario);

//        if(!itinerario.getItinerario().getAcessivel()){
//            binding.imageView12.setVisibility(View.GONE);
//        }

        if(itinerario == null || itinerario.getItinerario().getObservacao() == null || (itinerario.getItinerario().getObservacao().isEmpty() ||
                itinerario.getItinerario().getObservacao().equals("null") || itinerario.getItinerario().getObservacao().equals(""))){
            binding.textView24.setVisibility(View.GONE);
        } else{
            binding.textView24.setVisibility(View.VISIBLE);
        }

        if(itinerario == null || itinerario.getTempoAcumulado() == null || (itinerario.getTempoAcumulado().getHourOfDay() == 0 && itinerario.getTempoAcumulado().getMinuteOfHour() == 0)){
            binding.textViewEstimativa.setVisibility(View.GONE);
        } else{
            binding.textViewEstimativa.setVisibility(View.VISIBLE);
        }

        binding.setDestaca(false);
        binding.cardView2.setCardBackgroundColor(ctx.getResources().getColor(R.color.branco));

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
                            i.putExtra("horario", itinerario.getIdProximoHorario());
                            ctx.startActivity(i);
                            Log.d("TEMPO IN", DateTime.now().toString());
                        } else{
                            Toast.makeText(ctx.getApplicationContext(), "Acesso ao armazenamento externo é utilizado para " +
                                    "salvar partes do mapa e permitir o acesso offline. O mapa não funcionará corretamente sem essa permissão.", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(ctx, DetalheItinerarioActivity.class);
                            i.putExtra("itinerario", itinerario.getItinerario().getId());
                            i.putExtra("horario", itinerario.getIdProximoHorario());
                            ctx.startActivity(i);
                            Log.d("TEMPO IN", DateTime.now().toString());
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
        binding.cardView2.setOnClickListener(listener);
//        binding.textViewNome.setOnClickListener(listener);

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

        binding.executePendingBindings();
    }

    public void bind(final ItinerarioPartidaDestino itinerario, boolean destaca) {
        binding.setItinerario(itinerario);

        if(itinerario.getItinerario().getObservacao() == null || (itinerario.getItinerario().getObservacao().isEmpty() ||
                itinerario.getItinerario().getObservacao().equals("null") || itinerario.getItinerario().getObservacao().equals(""))){
            binding.textView24.setVisibility(View.GONE);
        } else{
            binding.textView24.setVisibility(View.VISIBLE);
        }

        if(itinerario.getTempoAcumulado() == null || (itinerario.getTempoAcumulado().getHourOfDay() == 0 && itinerario.getTempoAcumulado().getMinuteOfHour() == 0)){
            binding.textViewEstimativa.setVisibility(View.GONE);
        } else{
            binding.textViewEstimativa.setVisibility(View.VISIBLE);
        }

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, DetalheItinerarioActivity.class);
                i.putExtra("itinerario", itinerario.getItinerario().getId());
                i.putExtra("horario", itinerario.getIdProximoHorario());
                ctx.startActivity(i);
            }
        };
//
        binding.cardView2.setOnClickListener(listener);
//        binding.textViewNome.setOnClickListener(listener);
        binding.setDestaca(destaca);

        if(destaca){
            binding.cardView2.setCardBackgroundColor(ctx.getResources().getColor(R.color.cianoClaro));
//            binding.textView12.setTextColor(ctx.getResources().getColor(R.color.branco));
//            binding.textView15.setTextColor(ctx.getResources().getColor(R.color.branco));
//            binding.textView18.setTextColor(ctx.getResources().getColor(R.color.branco));
//            binding.textView19.setTextColor(ctx.getResources().getColor(R.color.branco));
        } else{
            binding.cardView2.setCardBackgroundColor(ctx.getResources().getColor(R.color.branco));
//            binding.textView12.setTextColor(ctx.getResources().getColor(R.color.cinzaEscuro));
//            binding.textView15.setTextColor(ctx.getResources().getColor(R.color.secondary_text_material_light));
//            binding.textView18.setTextColor(ctx.getResources().getColor(R.color.cinzaEscuro));
//            binding.textView19.setTextColor(ctx.getResources().getColor(R.color.secondary_text_material_light));
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

        binding.executePendingBindings();
    }

}
