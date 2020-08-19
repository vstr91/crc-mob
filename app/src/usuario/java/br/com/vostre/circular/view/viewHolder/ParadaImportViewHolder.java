package br.com.vostre.circular.view.viewHolder;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.databinding.LinhaParadasImportBinding;
import br.com.vostre.circular.listener.ParadaImportListener;
import br.com.vostre.circular.listener.ParadaListener;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaBairroImport;
import br.com.vostre.circular.view.form.FormParada;

public class ParadaImportViewHolder extends RecyclerView.ViewHolder {

    private final LinhaParadasImportBinding binding;
    AppCompatActivity ctx;
    ParadaImportListener listener;

    public ParadaImportViewHolder(LinhaParadasImportBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public ParadaImportViewHolder(LinhaParadasImportBinding binding, AppCompatActivity context, ParadaImportListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.listener = listener;
    }

    public void bind(final ParadaBairroImport parada) {
        binding.setParada(parada);

        if(parada.getDistancia() != null && parada.getDistancia() < 0){
            binding.cardview.setCardBackgroundColor(Color.RED);
        } else{
            binding.cardview.setCardBackgroundColor(Color.WHITE);
        }

        if(parada.getParada().getSentido() == 0){
            binding.imageViewSentido.setBackgroundColor(Color.RED);
        } else if(parada.getParada().getSentido() == 1){
            binding.imageViewSentido.setBackgroundColor(Color.BLUE);
        } else{
            binding.imageViewSentido.setBackgroundColor(Color.WHITE);
        }

        if(listener != null){

            binding.btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // FORM

                    parada.getParada().setCep("");
                    ParadaBairro pb = new ParadaBairro();
                    pb.setParada(parada.getParada());

//                    FormParadaImport formParada = new FormParadaImport();
//                    formParada.setParada(pb);
//                    formParada.setLatitude(parada.getParada().getLatitude());
//                    formParada.setLongitude(parada.getParada().getLongitude());
//                    formParada.setCtx(ctx.getApplication());
//                    formParada.show(ctx.getSupportFragmentManager(), "formParadaImport");

                        // FORM
                }
            });

            binding.btnLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelected(parada.getParada().getId(), 0);
                }
            });

            binding.btnEndereco.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelected(parada.getParada().getId(), 1);
                }
            });

            binding.btnExcluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelected(parada.getParada().getId(), 2);
                }
            });

            binding.btnStreetView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // STREET VIEW

                        // Create a Uri from an intent string. Use the result to create an Intent.
                        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+parada.getParada().getLatitude()+","+parada.getParada().getLongitude());

                        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        // Make the Intent explicit by setting the Google Maps package
                        mapIntent.setPackage("com.google.android.apps.maps");

                        // Attempt to start an activity that can handle the Intent
                        ctx.startActivity(mapIntent);

                        // STREET VIEW
                }
            });

        }

        binding.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ParadaBairro pb = new ParadaBairro();
                pb.setParada(parada.getParada());

                FormParada formParada = new FormParada();
//                formParada.setParada(pb);
                formParada.setLatitude(parada.getParada().getLatitude());
                formParada.setLongitude(parada.getParada().getLongitude());
                formParada.setCtx(ctx.getApplication());
                formParada.flagInicioEdicao = true;
                formParada.show(ctx.getSupportFragmentManager(), "formParada");
                return false;
            }
        });

        binding.executePendingBindings();
    }
}
