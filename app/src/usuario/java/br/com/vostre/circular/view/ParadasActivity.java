package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.util.ArrayList;
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
import br.com.vostre.circular.utils.DestaqueUtils;
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

    Bundle bundle;
    boolean exibindoTour = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ctx = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_paradas);
        binding.setView(this);
        super.onCreate(savedInstanceState);
        setTitle("Paradas");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        viewModel = ViewModelProviders.of(this).get(ParadasViewModel.class);
        viewModel.cidades.observe(this, cidadesObserver);
        viewModel.paradas.observe(this, paradasObserver);
        viewModel.cidade.observe(this, cidadeObserver);

        binding.setViewModel(viewModel);

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
            final File brasao = new File(view.getContext().getApplicationContext().getFilesDir(),  imagem);

            if(brasao.exists() && brasao.canRead()){
                final Drawable drawable = Drawable.createFromPath(brasao.getAbsolutePath());
                view.setImageDrawable(drawable);
            }
        } else{

            view.setImageDrawable(view.getContext().getApplicationContext().getResources().getDrawable(R.drawable.imagem_nao_disponivel_quadrada));
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

            binding.textView14.getViewTreeObserver().addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    YoYo.with(Techniques.Bounce)
                            .duration(700)
                            .repeat(1)
                            .playOn(findViewById(R.id.textView14));

//                    View v = findViewById(R.id.textView66);
//
//                    YoYo.with(Techniques.Flash)
//                            .delay(500)
//                            .duration(500)
//                            .playOn(findViewById(R.id.textView66));
                }
            });

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

                bundle = new Bundle();
                bundle.putString("cidade", cidade.getCidade().getNome());
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

                if(bundle != null){
                    bundle.putInt("qtd_paradas", paradas.size());

                    mFirebaseAnalytics.logEvent("paradas_consultadas", bundle);
                }


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

    @Override
    public void onToolbarItemSelected(View v) {
        onClickBtnEditarPartida(v);
        criaTour();
        exibindoTour = true;
    }

    TapTargetView.Listener l2 = new TapTargetView.Listener(){
        @Override
        public void onTargetClick(TapTargetView view) {
            super.onTargetClick(view);

            binding.listParadas.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.circleView2).performClick();

            exibindoTour = false;
        }
    };

    @Override
    public List<TapTarget> criaTour() {

        DestaqueUtils.geraDestaqueUnico(this, binding.listCidades.getChildAt(1).findViewById(R.id.circleView2), "Escolha a cidade",
                "Escolha a cidade para que seja mostrada a lista com as paradas cadastradas!", new TapTargetView.Listener(){
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);

                        binding.listCidades.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.circleView2).performClick();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                DestaqueUtils.geraDestaqueUnico(ctx, binding.listParadas.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.circleView2),
                                        "Escolha a parada", "Escolha então a parada e veja os detalhes, como próximas saídas e localização no mapa!", l2, false, true);
                            }
                        }, 300);

                    }
                }, false, true);

        List<TapTarget> targets = new ArrayList<>();

        return targets;
    }

}
