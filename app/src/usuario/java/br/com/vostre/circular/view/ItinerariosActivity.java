package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.List;

import br.com.vostre.circleview.CircleView;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityItinerariosBinding;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.adapter.CidadeAdapter;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;

public class ItinerariosActivity extends BaseActivity {

    ActivityItinerariosBinding binding;

    RecyclerView listCidades;
    CidadeAdapter adapter;

    static AppCompatActivity ctx;

    ItinerariosViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_itinerarios);
        binding.setView(this);
        super.onCreate(savedInstanceState);
        setTitle("Itinerários");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        viewModel = ViewModelProviders.of(this).get(ItinerariosViewModel.class);
        viewModel.cidades.observe(this, cidadesObserver);

        ctx = this;

        listCidades = binding.listCidades;
        adapter = new CidadeAdapter(viewModel.cidades.getValue(), this);

        listCidades.setAdapter(adapter);



    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @BindingAdapter("app:imagem")
    public static void setImagem(CircleView view, String imagem){

        if(imagem != null){
            File brasao = new File(ctx.getApplicationContext().getFilesDir(),  imagem);

            if(brasao.exists() && brasao.canRead()){
                view.setImagem(Drawable.createFromPath(brasao.getAbsolutePath()));
            }

        } else{
            view.setImagem(null);
        }

    }

    Observer<List<CidadeEstado>> cidadesObserver = new Observer<List<CidadeEstado>>() {
        @Override
        public void onChanged(List<CidadeEstado> cidades) {
            adapter.cidades = cidades;
            adapter.notifyDataSetChanged();
        }
    };

}
