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
import br.com.vostre.circular.databinding.InfowindowBinding;
import br.com.vostre.circular.databinding.InfowindowPoiParadaBinding;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.pojo.ParadaBairro;
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

    @BindingAdapter("srcCompat")
    public static void setImagemFoto(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }

    }

}
