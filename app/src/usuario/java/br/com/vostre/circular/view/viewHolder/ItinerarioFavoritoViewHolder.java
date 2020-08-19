package br.com.vostre.circular.view.viewHolder;

import android.Manifest;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaItinerariosFavoritosBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.DetalheItinerarioActivity;

public class ItinerarioFavoritoViewHolder extends RecyclerView.ViewHolder {

    private final LinhaItinerariosFavoritosBinding binding;
    AppCompatActivity ctx;

    public ItinerarioFavoritoViewHolder(LinhaItinerariosFavoritosBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ItinerarioPartidaDestino itinerario) {
        binding.setItinerario(itinerario);

//        if(!itinerario.getItinerario().getAcessivel()){
//            binding.imageView12.setVisibility(View.GONE);
//        }

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
                            ctx.startActivity(i);
                        } else{
                            Toast.makeText(ctx.getApplicationContext(), "Acesso ao armazenamento externo é utilizado para " +
                                    "salvar partes do mapa e permitir o acesso offline. O mapa não funcionará corretamente sem essa permissão.", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(ctx, DetalheItinerarioActivity.class);
                            i.putExtra("itinerario", itinerario.getItinerario().getId());
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

}
