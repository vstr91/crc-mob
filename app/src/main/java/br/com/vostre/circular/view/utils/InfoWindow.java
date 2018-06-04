package br.com.vostre.circular.view.utils;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.io.File;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.InfowindowBinding;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.form.FormBase;
import br.com.vostre.circular.view.form.FormParada;
import br.com.vostre.circular.viewModel.InfoWindowViewModel;

public class InfoWindow extends FormBase {

    InfowindowBinding binding;
    AppCompatActivity ctx;
    InfoWindowViewModel viewModel;
    ParadaBairro parada;

    public Boolean exibeBotaoEditar = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.infowindow, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(InfoWindowViewModel.class);
        viewModel.setParada(parada);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(!exibeBotaoEditar){
            binding.btnEditar.setVisibility(View.GONE);
        }

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

    public InfoWindowViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(InfoWindowViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void onClickEditar(View v){

        FormParada formParada = new FormParada();
        formParada.setParada(parada);
        formParada.setLatitude(parada.getParada().getLatitude());
        formParada.setLongitude(parada.getParada().getLongitude());
        formParada.setCtx(ctx.getApplication());
        formParada.flagInicioEdicao = true;
        formParada.show(ctx.getSupportFragmentManager(), "formParada");

        dismiss();
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

}
