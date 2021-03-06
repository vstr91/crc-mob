package br.com.vostre.circular.view.viewHolder;

import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import br.com.vostre.circular.databinding.LinhaPontosInteresseBinding;
import br.com.vostre.circular.databinding.LinhaSecoesBinding;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.form.FormMapa;

public class PontoInteresseViewHolder extends RecyclerView.ViewHolder {

    private final LinhaPontosInteresseBinding binding;
    AppCompatActivity ctx;
    ParadaBairro parada;
    BottomSheetDialog bsd;

    public PontoInteresseViewHolder(LinhaPontosInteresseBinding binding, AppCompatActivity context, ParadaBairro parada, BottomSheetDialog bsd) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
        this.parada = parada;
        this.bsd = bsd;
    }

    public void bind(final PontoInteresse poi) {
        binding.setPontoInteresse(poi);

        if(poi.isPermanente()){
            binding.textView41.setVisibility(View.GONE);
        } else{
            binding.textView41.setVisibility(View.VISIBLE);
        }

        if(poi.getDescricao() == null || poi.getDescricao().isEmpty()){
            binding.textView42.setVisibility(View.GONE);
        } else{
            binding.textView42.setVisibility(View.VISIBLE);
        }

        binding.btnVerMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.dismiss();
                FormMapa formMapa = new FormMapa();
                formMapa.setParada(parada);
                formMapa.setPontoInteresse(poi);
                formMapa.setCtx(ctx.getApplication());
                formMapa.show(ctx.getSupportFragmentManager(), "formMapa");
            }
        });

        binding.executePendingBindings();
    }
}
