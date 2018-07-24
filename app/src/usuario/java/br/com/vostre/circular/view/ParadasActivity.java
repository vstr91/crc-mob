package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.util.List;

import br.com.vostre.circleview.CircleView;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityItinerariosBinding;
import br.com.vostre.circular.databinding.ActivityParadasBinding;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.adapter.CidadeAdapter;
import br.com.vostre.circular.view.adapter.ParadaAdapter;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.view.listener.ItemListener;
import br.com.vostre.circular.view.listener.SelectListener;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;
import br.com.vostre.circular.viewModel.ParadasViewModel;

public class ParadasActivity extends BaseActivity implements SelectListener {

    ActivityParadasBinding binding;

    RecyclerView listCidades;
    CidadeAdapter adapter;

    RecyclerView listParadas;
    ParadaAdapter adapterParadas;

    static AppCompatActivity ctx;

    ParadasViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_paradas);
        binding.setView(this);
        super.onCreate(savedInstanceState);
        setTitle("Paradas");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        viewModel = ViewModelProviders.of(this).get(ParadasViewModel.class);
        viewModel.cidades.observe(this, cidadesObserver);
        viewModel.paradas.observe(this, paradasObserver);
        viewModel.cidade.observe(this, cidadeObserver);

        binding.setViewModel(viewModel);

        ctx = this;

        listCidades = binding.listCidades;
        adapter = new CidadeAdapter(viewModel.cidades.getValue(), this);
        adapter.setListener(this);

        listCidades.setAdapter(adapter);

        listParadas = binding.listParadas;
        adapterParadas = new ParadaAdapter(viewModel.paradas.getValue(), this);

        listParadas.setAdapter(adapterParadas);
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
    public static void setimagem(ImageView view, String imagem){

        if(imagem != null){
            final File brasao = new File(ctx.getApplicationContext().getFilesDir(),  imagem);

            if(brasao.exists() && brasao.canRead()){
                final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
                view.setImageDrawable(drawable);
            }
        } else{
            view.setImageDrawable(null);
        }

    }

    @BindingAdapter("app:imagem")
    public static void setimagem(CircleView view, String imagem){

        if(imagem != null){
            final File brasao = new File(ctx.getApplicationContext().getFilesDir(),  imagem);

            if(brasao.exists() && brasao.canRead()){
                final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
                view.setImagem(null);
                view.setImagem(drawable);
                view.refreshDrawableState();
            }
        }

    }

    public void onClickBtnEditarPartida(View v){
        binding.cardViewCidade.setVisibility(View.GONE);
        binding.listParadas.setVisibility(View.GONE);
        binding.cardViewListCidade.setVisibility(View.VISIBLE);
    }

    Observer<List<CidadeEstado>> cidadesObserver = new Observer<List<CidadeEstado>>() {
        @Override
        public void onChanged(List<CidadeEstado> cidades) {
            adapter.cidades = cidades;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<CidadeEstado> cidadeObserver = new Observer<CidadeEstado>() {
        @Override
        public void onChanged(CidadeEstado cidade) {

            if(cidade != null){
                binding.setCidade(cidade);
                binding.cardViewCidade.setVisibility(View.VISIBLE);
                //setimagem(binding.circleViewPartida, cidade.getCidade().getBrasao());
                binding.cardViewListCidade.setVisibility(View.GONE);
            }

        }
    };

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {

            if(paradas != null){
                adapterParadas.paradas = paradas;
                adapterParadas.bairroAtual = "";
                adapterParadas.notifyDataSetChanged();
                binding.listParadas.scheduleLayoutAnimation();
                binding.listParadas.setVisibility(View.VISIBLE);
            }

        }
    };

    @Override
    public String onSelected(String id) {
        viewModel.setCidade(id);
        viewModel.cidade.observe(this, cidadeObserver);
        viewModel.paradas.observe(this, paradasObserver);
        return id;
    }
}
