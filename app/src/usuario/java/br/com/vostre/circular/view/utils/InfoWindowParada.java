package br.com.vostre.circular.view.utils;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.InfowindowBinding;
import br.com.vostre.circular.databinding.InfowindowParadaBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.form.FormBase;
import br.com.vostre.circular.viewModel.InfoWindowParadaViewModel;
import br.com.vostre.circular.viewModel.InfoWindowViewModel;

public class InfoWindowParada extends FormBase {

    InfowindowParadaBinding binding;
    AppCompatActivity ctx;
    InfoWindowParadaViewModel viewModel;
    ParadaBairro parada;

    ItinerarioAdapter adapter;
    RecyclerView listItinerarios;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.infowindow_parada, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(InfoWindowParadaViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        binding.setParada(parada);
        viewModel.setParada(parada);
        viewModel.itinerarios.observe(this, itinerariosObserver);

        listItinerarios = binding.listItinerarios;
        adapter = new ItinerarioAdapter(viewModel.itinerarios.getValue(), ctx);
        listItinerarios.setAdapter(adapter);

        return binding.getRoot();

    }

    public ParadaBairro getParada() {
        return parada;
    }

    public void setParada(ParadaBairro parada) {
        this.parada = parada;
    }

    public AppCompatActivity getCtx() {
        return ctx;
    }

    public void setCtx(AppCompatActivity ctx) {
        this.ctx = ctx;
    }

    public InfoWindowParadaViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(InfoWindowParadaViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void onClickFechar(View v){
        dismiss();
    }

    @BindingAdapter("srcCompat")
    public static void setImagemFoto(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }

    }

    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
            adapter.itinerarios = itinerarios;
            adapter.notifyDataSetChanged();
            binding.invalidateAll();
        }
    };

}
