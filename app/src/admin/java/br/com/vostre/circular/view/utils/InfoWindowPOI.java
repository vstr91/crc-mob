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
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.InfowindowPoiBinding;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.form.FormBase;

public class InfoWindowPOI extends FormBase {

    InfowindowPoiBinding binding;
    AppCompatActivity ctx;
    InfoWindowPOIViewModel viewModel;
    PontoInteresse pontoInteresse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.infowindow_poi, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(InfoWindowPOIViewModel.class);
        viewModel.setPontoInteresse(pontoInteresse);

        binding.setView(this);
        binding.setViewModel(viewModel);

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

        FormPontoInteresse formPontoInteresse = new FormPontoInteresse();
        formPontoInteresse.setPontoInteresse(pontoInteresse);
        formPontoInteresse.setLatitude(pontoInteresse.getLatitude());
        formPontoInteresse.setLongitude(pontoInteresse.getLongitude());
        formPontoInteresse.setCtx(ctx.getApplication());
        formPontoInteresse.flagInicioEdicao = true;
        formPontoInteresse.show(ctx.getSupportFragmentManager(), "formPontoInteresse");

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

    @BindingAdapter("android:text")
    public static void setText(TextView view, DateTime date) {
        String formatted = DateTimeFormat.forPattern("dd/MM/yyyy à's' HH:mm").print(date);
        view.setText(formatted);
    }

}
