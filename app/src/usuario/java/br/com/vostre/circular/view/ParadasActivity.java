package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ExpandableListView;
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
import br.com.vostre.circular.databinding.ActivityParadasBinding;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.DestaqueUtils;
import br.com.vostre.circular.view.adapter.CidadeAdapter;
import br.com.vostre.circular.view.adapter.ParadaAdapter;
import br.com.vostre.circular.view.adapter.ParadaCollapseAdapter;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.view.listener.ItemListener;
import br.com.vostre.circular.view.listener.SelectListener;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;
import br.com.vostre.circular.viewModel.ParadasViewModel;

public class ParadasActivity extends BaseActivity implements SelectListener {

    ActivityParadasBinding binding;

    RecyclerView listCidades;
    CidadeAdapter adapter;

    ExpandableListView listParadas;
    ParadaCollapseAdapter adapterParadas;

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
        viewModel.bairros.observe(this, bairrosObserver);
        viewModel.cidade.observe(this, cidadeObserver);

        binding.setViewModel(viewModel);

        listCidades = binding.listCidades;
        adapter = new CidadeAdapter(viewModel.cidades.getValue(), this);
        adapter.setListener(this);

        listCidades.setAdapter(adapter);

        listParadas = binding.listParadas;
        adapterParadas = new ParadaCollapseAdapter(viewModel.bairros.getValue(), this);

        listParadas.setAdapter(adapterParadas);

        binding.textViewDica.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean retorno = super.onCreateOptionsMenu(menu);

        if(menu != null){
            menu.getItem(1).setVisible(false);
        }

        return retorno;
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
        binding.textViewDica.setVisibility(View.GONE);
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

                binding.imageButton.setVisibility(View.VISIBLE);
                binding.imageButton.setImageResource(R.drawable.ic_edit_blue_24dp);
            }

        }
    };

    Observer<List<BairroCidade>> bairrosObserver = new Observer<List<BairroCidade>>() {
        @Override
        public void onChanged(List<BairroCidade> bairros) {

            if(bairros != null){
                adapterParadas.bairros = bairros;
                adapterParadas.bairroAtual = "";

                carregarLista(bairros);

                if(bundle != null){
                    //bundle.putInt("qtd_paradas", bairros.size());

                    mFirebaseAnalytics.logEvent("paradas_consultadas", bundle);
                }

            }

        }
    };

    @Override
    public String onSelected(String id) {
        viewModel.setCidade(id);
        viewModel.cidade.observe(this, cidadeObserver);
        viewModel.bairros.observe(this, bairrosObserver);

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

//            binding.listParadas.getExpandableListAdapter().getChildView(0, 1).findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.circleView2).performClick();

            exibindoTour = false;
        }
    };

    private void carregarLista(final List<BairroCidade> bairros) {

        if(bairros != null){
            final List<String> titulos = new ArrayList<>();

            for(BairroCidade b : bairros){
                titulos.add(b.getBairro().getNome());
            }

            adapterParadas = new ParadaCollapseAdapter(bairros,this);

            listParadas.setAdapter(adapterParadas);

            listParadas.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
//                    Toast.makeText(getApplicationContext(),
//                            titulos.get(groupPosition) + " List Expanded.",
//                            Toast.LENGTH_SHORT).show();
                }
            });

            listParadas.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                @Override
                public void onGroupCollapse(int groupPosition) {
//                    Toast.makeText(getApplicationContext(),
//                            titulos.get(groupPosition) + " List Collapsed.",
//                            Toast.LENGTH_SHORT).show();

                }
            });

            listParadas.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    Intent i = new Intent(ctx, DetalheParadaActivity.class);
                    i.putExtra("parada", bairros.get(groupPosition).getParadas().get(childPosition).getParada().getId());
                    ctx.startActivity(i);
                    return false;
                }
            });

        }

        adapterParadas.notifyDataSetChanged();
        binding.listParadas.scheduleLayoutAnimation();
        binding.listParadas.setVisibility(View.VISIBLE);
        binding.textViewDica.setVisibility(View.VISIBLE);

    }

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

//                                DestaqueUtils.geraDestaqueUnico(ctx, binding.listParadas.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.circleView2),
//                                        "Escolha a parada", "Escolha então a parada e veja os detalhes, como próximas saídas e localização no mapa!", l2, false, true);
                            }
                        }, 300);

                    }
                }, true, true);

        List<TapTarget> targets = new ArrayList<>();

        return targets;
    }

}
