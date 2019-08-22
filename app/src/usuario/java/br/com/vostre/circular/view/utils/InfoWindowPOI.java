package br.com.vostre.circular.view.utils;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.InfowindowBinding;
import br.com.vostre.circular.databinding.InfowindowPoiParadaBinding;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.DetalhePontoInteresseActivity;
import br.com.vostre.circular.view.adapter.ItinerarioFavoritoAdapter;
import br.com.vostre.circular.view.form.FormBase;
import br.com.vostre.circular.viewModel.InfoWindowPOIViewModel;
import br.com.vostre.circular.viewModel.InfoWindowViewModel;

public class InfoWindowPOI extends FormBase {

    InfowindowPoiParadaBinding binding;
    AppCompatActivity ctx;
    InfoWindowPOIViewModel viewModel;
    PontoInteresse pontoInteresse;

    public Boolean exibeBotaoEditar = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.infowindow_poi_parada, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(InfoWindowPOIViewModel.class);
        viewModel.setPontoInteresse(pontoInteresse);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(pontoInteresse.isPermanente()){
            binding.textView54.setVisibility(View.GONE);
            binding.textView55.setVisibility(View.GONE);
            binding.textViewDataInicial.setVisibility(View.GONE);
            binding.textViewDataFinal.setVisibility(View.GONE);
        } else{
            binding.textView54.setVisibility(View.VISIBLE);
            binding.textView55.setVisibility(View.VISIBLE);
            binding.textViewDataInicial.setVisibility(View.VISIBLE);
            binding.textViewDataFinal.setVisibility(View.VISIBLE);
        }

        if(!exibeBotaoEditar){
            binding.btnEditar.setVisibility(View.GONE);
        }

        Location l = new Location(LocationManager.NETWORK_PROVIDER);
        l.setLatitude(pontoInteresse.getLatitude());
        l.setLongitude(pontoInteresse.getLongitude());

        viewModel.buscarParadasProximas(getCtx(), l);
        viewModel.paradas.observe(this, paradasObserver);

        return binding.getRoot();

    }

    public PontoInteresse getPontoInteresse() {
        return pontoInteresse;
    }

    public void setPontoInteresse(PontoInteresse pontoInteresse) {
        this.pontoInteresse = pontoInteresse;
    }

    public AppCompatActivity getCtx() {
        return ctx;
    }

    public void setCtx(AppCompatActivity ctx) {
        this.ctx = ctx;
    }

    public InfoWindowPOIViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(InfoWindowPOIViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void onClickEditar(View v){

//        FormParada formParada = new FormParada();
//        formParada.setParada(parada);
//        formParada.setLatitude(parada.getParada().getLatitude());
//        formParada.setLongitude(parada.getParada().getLongitude());
//        formParada.setCtx(ctx.getApplication());
//        formParada.flagInicioEdicao = true;
//        formParada.show(ctx.getSupportFragmentManager(), "formParada");

        dismiss();
    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickVerDetalhes(View v){
        Intent i = new Intent(getActivity().getApplicationContext(), DetalhePontoInteresseActivity.class);
        i.putExtra("poi", pontoInteresse.getId());
        getActivity().getApplicationContext().startActivity(i);
    }

    @BindingAdapter("srcCompat")
    public static void setImagemFoto(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }

    }

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {

            List<String> listParadas = new ArrayList<>();

            for(ParadaBairro p : paradas){

                listParadas.add(p.getParada().getId());

                //System.out.println("PARADAS: "+p.getParada().getId()+" | "+p.getParada().getNome()+" - "+p.getNomeBairroComCidade());
            }

            viewModel.listarTodosAtivosProximosPoi(listParadas);

            if(viewModel.itinerarios != null && ctx != null){
                viewModel.itinerarios.observe(ctx, itinerariosObserver);
                binding.listItinerarios.setVisibility(View.VISIBLE);
                binding.textView66.setVisibility(View.VISIBLE);
                binding.btnVerDetalhes.setVisibility(View.GONE);
            } else{
                binding.listItinerarios.setVisibility(View.GONE);
                binding.textView66.setVisibility(View.GONE);
                binding.btnVerDetalhes.setVisibility(View.VISIBLE);
            }



        }
    };

    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {

            ItinerarioFavoritoAdapter adapter = new ItinerarioFavoritoAdapter(itinerarios, getCtx());
            binding.listItinerarios.setAdapter(adapter);

//            for(ItinerarioPartidaDestino i : itinerarios){
//                System.out.println("ITINERARIOS: "+i.getItinerario().getId()+" | "+i.getNomePartida()+", "+i.getNomeBairroPartida()+" - "+i.getNomeDestino()+", "+i.getNomeBairroDestino());
//            }

        }
    };

}
