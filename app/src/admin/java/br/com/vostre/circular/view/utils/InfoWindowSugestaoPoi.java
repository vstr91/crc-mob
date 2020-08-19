package br.com.vostre.circular.view.utils;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.InfowindowSugestaoPoiBinding;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;
import br.com.vostre.circular.view.form.FormBase;
import br.com.vostre.circular.viewModel.InfoWindowSugestaoPoiViewModel;

public class InfoWindowSugestaoPoi extends FormBase {

    InfowindowSugestaoPoiBinding binding;
    AppCompatActivity ctx;
    InfoWindowSugestaoPoiViewModel viewModel;
    PontoInteresseSugestaoBairro parada;

    public Boolean exibeBotaoEditar = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.infowindow_sugestao_poi, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(InfoWindowSugestaoPoiViewModel.class);
        viewModel.setParada(parada);

        binding.setView(this);
        binding.setViewModel(viewModel);

        return binding.getRoot();

    }

    public PontoInteresseSugestaoBairro getParada() {
        return parada;
    }

    public void setParada(PontoInteresseSugestaoBairro parada) {
        this.parada = parada;
    }

    public AppCompatActivity getCtx() {
        return ctx;
    }

    public void setCtx(AppCompatActivity ctx) {
        this.ctx = ctx;
    }

    public InfoWindowSugestaoPoiViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(InfoWindowSugestaoPoiViewModel viewModel) {
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
