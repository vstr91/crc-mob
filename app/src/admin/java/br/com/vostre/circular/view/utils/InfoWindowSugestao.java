package br.com.vostre.circular.view.utils;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.InfowindowSugestaoBinding;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.view.form.FormBase;
import br.com.vostre.circular.view.form.FormParada;
import br.com.vostre.circular.viewModel.InfoWindowSugestaoViewModel;
import br.com.vostre.circular.viewModel.InfoWindowViewModel;

public class InfoWindowSugestao extends FormBase {

    InfowindowSugestaoBinding binding;
    AppCompatActivity ctx;
    InfoWindowSugestaoViewModel viewModel;
    ParadaSugestaoBairro parada;

    public Boolean exibeBotaoEditar = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.infowindow, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(InfoWindowSugestaoViewModel.class);
        viewModel.setParada(parada);

        binding.setView(this);
        binding.setViewModel(viewModel);

        return binding.getRoot();

    }

    public ParadaSugestaoBairro getParada() {
        return parada;
    }

    public void setParada(ParadaSugestaoBairro parada) {
        this.parada = parada;
    }

    public AppCompatActivity getCtx() {
        return ctx;
    }

    public void setCtx(AppCompatActivity ctx) {
        this.ctx = ctx;
    }

    public InfoWindowSugestaoViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(InfoWindowSugestaoViewModel viewModel) {
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

}
