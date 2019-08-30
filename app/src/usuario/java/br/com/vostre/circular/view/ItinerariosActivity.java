package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circleview.CircleView;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityItinerariosBinding;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.utils.DataHoraUtils;
import br.com.vostre.circular.utils.DestaqueUtils;
import br.com.vostre.circular.view.adapter.CidadeAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioResultadoAdapter;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.view.listener.HoraListener;
import br.com.vostre.circular.view.listener.ItemListener;
import br.com.vostre.circular.view.listener.SelectListener;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;

public class ItinerariosActivity extends BaseActivity implements SelectListener, ItemListener, HoraListener {

    ActivityItinerariosBinding binding;

    RecyclerView listCidadesPartida;
    CidadeAdapter adapter;

    RecyclerView listCidadesDestino;
    CidadeAdapter adapterDestino;

    RecyclerView listResultados;
    ItinerarioResultadoAdapter adapterResultado;

    static AppCompatActivity ctx;

    ItinerariosViewModel viewModel;

    String dia = "";
    String diaSeguinte = "";
    boolean consultaDiaSeguinte = false;

    BairroCidade bairroPartida;
    BairroCidade bairroDestino;

    boolean inversao = false;

    Bundle bundle;
    ProgressBar progressBar;

    FormBairro formBairro;

    boolean exibindoTour = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ctx = this;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_itinerarios);
        binding.setView(this);
        super.onCreate(savedInstanceState);
        setTitle("Itinerários");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        viewModel = ViewModelProviders.of(this).get(ItinerariosViewModel.class);
        viewModel.cidades.observe(this, cidadesObserver);
        //viewModel.escolhaAtual = 0;
        viewModel.resultadosItinerarios.observe(this, resultadoItinerarioObserver);

        binding.setViewModel(viewModel);

        ocultaModalLoading();

        listCidadesPartida = binding.listCidadesPartida;
        adapter = new CidadeAdapter(viewModel.cidades.getValue(), this);
        adapter.setListener(this);

        listCidadesPartida.setAdapter(adapter);

        listCidadesDestino = binding.listCidades;
        adapterDestino = new CidadeAdapter(viewModel.cidades.getValue(), this);
        adapterDestino.setListener(this);

        listCidadesDestino.setAdapter(adapterDestino);

        listResultados = binding.listResultados;
        adapterResultado = new ItinerarioResultadoAdapter(viewModel.resultadosItinerarios.getValue(), this, "", "");
        listResultados.setAdapter(adapterResultado);

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
        binding.cardViewPartida.setVisibility(View.GONE);
        binding.cardViewListPartida.setVisibility(View.VISIBLE);
        binding.cardViewListDestino.setVisibility(View.GONE);
        binding.cardViewDestino.setVisibility(View.GONE);
        binding.listResultados.setVisibility(View.GONE);
        binding.btnInverter.setVisibility(View.GONE);
        binding.cardViewResultadoVazio.setVisibility(View.GONE);
        binding.textViewResultado.setVisibility(View.GONE);
        binding.textViewSubResultado.setVisibility(View.GONE);
        //viewModel.escolhaAtual = 0;
        consultaDiaSeguinte = false;
        viewModel.partidaEscolhida = false;
        viewModel.destinoEscolhido = false;

        //log
        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("edicao_partida", bundle);

    }

    public void onClickBtnEditarDestino(View v){
        binding.cardViewDestino.setVisibility(View.GONE);
        binding.cardViewListDestino.setVisibility(View.VISIBLE);
        binding.listResultados.setVisibility(View.GONE);
        binding.btnInverter.setVisibility(View.GONE);
        binding.cardViewResultadoVazio.setVisibility(View.GONE);
        binding.textViewResultado.setVisibility(View.GONE);
        binding.textViewSubResultado.setVisibility(View.GONE);
        //viewModel.escolhaAtual = 1;
        consultaDiaSeguinte = false;
        viewModel.destinoEscolhido = false;

        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("edicao_destino", bundle);
    }

    public void onClickBtnInverter(View v){

        inversao = !inversao;
        //viewModel.escolhaAtual = 0;
        BairroCidade bairro = bairroPartida;
        bairroPartida = bairroDestino;
        bairroDestino = bairro;

        binding.setPartida(bairroPartida);
        binding.setDestino(bairroDestino);

        DateTime dateTime = new DateTime();
        String dia = DataHoraUtils.getDiaAtual();
        String diaSeguinte = DataHoraUtils.getDiaSeguinte();
        String diaAnterior = DataHoraUtils.getDiaAnterior();

        viewModel.carregaResultadoInvertido(DateTimeFormat.forPattern("HH:mm:00").print(dateTime), dia, diaSeguinte, diaAnterior);

        geraModalLoading();

//        BairroCidade bairro = bairroPartida;
//        bairroPartida = bairroDestino;
//        bairroDestino = bairro;
//
//        viewModel.setBairroPartida(bairroPartida);
//        viewModel.bairroPartida.observe(this, bairroObserver);
//
//        viewModel.setBairroDestino(bairroDestino);
//        viewModel.bairroDestino.observe(this, bairroDestinoObserver);
//
//        mostraDadosBairroInversao(bairroPartida, 0);
//        mostraDadosBairroInversao(bairroDestino, 1);

        adapterResultado.setDia(DataHoraUtils.getDiaAtualFormatado());
        adapterResultado.setHora(DataHoraUtils.getHoraAtual());

        //log
        bundle = new Bundle();
        bundle.putString("partida", bairroPartida.getBairro().getNome()+", "+bairroPartida.getNomeCidadeComEstado());
        bundle.putString("destino", bairroDestino.getBairro().getNome()+", "+bairroDestino.getNomeCidadeComEstado());

        mFirebaseAnalytics.logEvent("inversao_consulta", bundle);

    }

    private void geraModalLoading() {
        binding.fundo.setVisibility(View.VISIBLE);
        binding.textViewCarregando.setVisibility(View.VISIBLE);
        binding.progressBar.setIndeterminate(true);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void ocultaModalLoading(){
        binding.fundo.setVisibility(View.GONE);
        binding.textViewCarregando.setVisibility(View.GONE);
        binding.progressBar.setIndeterminate(true);
        binding.progressBar.setVisibility(View.GONE);
    }

    Observer<List<ItinerarioPartidaDestino>> resultadoItinerarioObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(final List<ItinerarioPartidaDestino> itinerarios) {

            if (itinerarios != null && itinerarios.size() > 0) {
                binding.cardViewListDestino.setVisibility(View.GONE);
                binding.listResultados.setVisibility(View.VISIBLE);
                binding.cardViewResultadoVazio.setVisibility(View.GONE);
                binding.btnInverter.setVisibility(View.VISIBLE);
                binding.textViewResultado.setVisibility(View.VISIBLE);

                if (itinerarios.size() == 1) {
                    binding.textViewSubResultado.setVisibility(View.GONE);
                } else {
                    binding.textViewSubResultado.setVisibility(View.VISIBLE);
                }

                adapterResultado.itinerarios = itinerarios;

                bundle.putString("partida", bairroPartida.getBairro().getNome() + ", " + bairroPartida.getNomeCidadeComEstado());
                bundle.putString("destino", bairroDestino.getBairro().getNome() + ", " + bairroDestino.getNomeCidadeComEstado());

                bundle.putString("partida_destino", bairroPartida.getBairro().getNome() + ", "
                        + bairroPartida.getNomeCidadeComEstado() + " x "
                        + bairroDestino.getBairro().getNome() + ", " + bairroDestino.getNomeCidadeComEstado());

                bundle.putInt("itinerarios", itinerarios.size());
                bundle.putString(FirebaseAnalytics.Param.END_DATE, DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").print(DateTime.now()));
                bundle.putBoolean("sucesso", true);

                mFirebaseAnalytics.logEvent("consulta_itinerario", bundle);

            } else {
                binding.listResultados.setVisibility(View.GONE);
                binding.cardViewResultadoVazio.setVisibility(View.VISIBLE);
                binding.btnInverter.setVisibility(View.GONE);
                binding.textViewResultado.setVisibility(View.GONE);
                binding.textViewSubResultado.setVisibility(View.GONE);

                adapterResultado.itinerarios = null;

                bundle.putString("partida", bairroPartida.getBairro().getNome() + ", " + bairroPartida.getNomeCidadeComEstado());
                bundle.putString("destino", bairroDestino.getBairro().getNome() + ", " + bairroDestino.getNomeCidadeComEstado());

                bundle.putString("partida_destino", bairroPartida.getBairro().getNome() + ", "
                        + bairroPartida.getNomeCidadeComEstado() + " x "
                        + bairroDestino.getBairro().getNome() + ", " + bairroDestino.getNomeCidadeComEstado());

                bundle.putInt("itinerarios", 0);
                bundle.putString(FirebaseAnalytics.Param.END_DATE, DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").print(DateTime.now()));
                bundle.putBoolean("sucesso", false);

                mFirebaseAnalytics.logEvent("consulta_itinerario", bundle);
            }

            adapterResultado.notifyDataSetChanged();
            listResultados.scrollToPosition(0);
            ocultaModalLoading();

            if(exibindoTour){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DestaqueUtils.geraDestaqueUnico(ctx, binding.listResultados.getChildAt(0).findViewById(R.id.textViewHorario),
                                "Consulta concluída", "Pronto, você concluiu a consulta! Aqui está o próximo horário de partida do itinerário pesquisado!",
                                l5, false, false);
                    }
                }, 300);

                exibindoTour = false;
            }




        }
    };

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

                    YoYo.with(Techniques.Flash)
                            .delay(500)
                            .duration(500)
                            .playOn(findViewById(R.id.textView67));
                }
            });

        }
    };

    Observer<List<CidadeEstado>> cidadesDestinoObserver = new Observer<List<CidadeEstado>>() {
        @Override
        public void onChanged(List<CidadeEstado> cidades) {
            adapterDestino.cidades = cidades;
            adapterDestino.notifyDataSetChanged();

            binding.textView2.getViewTreeObserver().addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    YoYo.with(Techniques.Bounce)
                            .duration(700)
                            .repeat(1)
                            .playOn(findViewById(R.id.textView2));

                    YoYo.with(Techniques.Flash)
                            .delay(500)
                            .duration(500)
                            .playOn(findViewById(R.id.textView68));
                }
            });
        }
    };

    Observer<BairroCidade> bairroObserver = new Observer<BairroCidade>() {
        @Override
        public void onChanged(BairroCidade bairro) {

                if(bairro != null){
                    mostraDadosBairro(bairro, 0);
                    bairroPartida = bairro;
                    viewModel.escolhaAtual = 1;
                }

        }
    };

    Observer<BairroCidade> bairroDestinoObserver = new Observer<BairroCidade>() {
        @Override
        public void onChanged(BairroCidade bairro) {

                if(bairro != null){
                    mostraDadosBairro(bairro, 1);
                    bairroDestino = bairro;
                    mostraResultado();
                    viewModel.escolhaAtual = 0;
                }

        }
    };

    private void mostraDadosBairro(BairroCidade bairro, int tipo) {

        if(tipo == 0){
            binding.cardViewPartida.setVisibility(View.VISIBLE);
            viewModel.escolhaAtual = 1;
            viewModel.cidadesDestino.observe(this, cidadesDestinoObserver);
            binding.setPartida(bairro);
            binding.cardViewListPartida.setVisibility(View.GONE);
            binding.cardViewListDestino.setVisibility(View.VISIBLE);
        } else{
            binding.cardViewDestino.setVisibility(View.VISIBLE);
            binding.cardViewListDestino.setVisibility(View.GONE);
            binding.setDestino(bairro);
            viewModel.escolhaAtual = 0;
        }

    }

    private void mostraDadosBairroInversao(BairroCidade bairro, int tipo) {

        if(tipo == 0){
            viewModel.escolhaAtual = 1;
            binding.setPartida(bairro);
        } else{
            viewModel.escolhaAtual = 0;
            binding.setDestino(bairro);
        }

        binding.executePendingBindings();

    }

    Observer<HorarioItinerarioNome> itinerarioObserver = new Observer<HorarioItinerarioNome>() {
        @Override
        public void onChanged(HorarioItinerarioNome horario) {

//            if(horario != null && horario.getIdHorario() != null){
//
//                binding.cardViewListDestino.setVisibility(View.GONE);
//                binding.listResultados.setVisibility(View.VISIBLE);
//                binding.cardViewResultadoVazio.setVisibility(View.GONE);
//                binding.btnInverter.setVisibility(View.VISIBLE);
//
//                binding.setHorario(horario);
//
//                exibeDados(horario);
//                viewModel.carregaHorarios(horario);
//                viewModel.horarioAnterior.observe(ctx, horarioAnteriorObserver);
//                viewModel.horarioSeguinte.observe(ctx, horarioSeguinteObserver);
//            } else if(!consultaDiaSeguinte){
//                consultaDiaSeguinte = true;
//                viewModel.carregaResultadoDiaSeguinte(diaSeguinte);
//                viewModel.itinerario.observe(ctx, itinerarioObserver);
//            } else{
//                binding.cardViewResultadoVazio.setVisibility(View.VISIBLE);
//            }
//
//            binding.executePendingBindings();

        }
    };

    Observer<HorarioItinerarioNome> horarioAnteriorObserver = new Observer<HorarioItinerarioNome>() {
        @Override
        public void onChanged(HorarioItinerarioNome horario) {

            if(horario != null && horario.getIdHorario() != null){
                exibeHorarioAnterior(horario);
            } else if(!consultaDiaSeguinte){
//                consultaDiaSeguinte = true;
//                viewModel.carregaResultadoDiaSeguinte(diaSeguinte);
//                viewModel.itinerario.observe(ctx, itinerarioObserver);
            }

        }
    };

    Observer<HorarioItinerarioNome> horarioSeguinteObserver = new Observer<HorarioItinerarioNome>() {
        @Override
        public void onChanged(HorarioItinerarioNome horario) {

            consultaDiaSeguinte = true;

            if(horario != null && horario.getIdHorario() != null){
                exibeHorarioSeguinte(horario);
            }

        }
    };

    Observer<ItinerarioPartidaDestino> itinerarioResultadoObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {

//            if(itinerario != null){
//
//                binding.setItinerario(itinerario);
//
//                exibeDadosResultado();
//                inversao = false;
//                viewModel.escolhaAtual = 0;
//                binding.executePendingBindings();
//            }

        }
    };

    @Override
    public String onSelected(String id) {

        formBairro = new FormBairro();

        Bundle bundle = new Bundle();
        bundle.putString("cidade", id);

        formBairro.setArguments(bundle);
        formBairro.setCtx(ctx.getApplication());
        formBairro.setListener(this);
        formBairro.show(ctx.getSupportFragmentManager(), "formBairro");

        this.bundle = new Bundle();
        this.bundle.putString(FirebaseAnalytics.Param.START_DATE, DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").print(DateTime.now()));

        return null;
    }

    @Override
    public String onItemSelected(String id) {
        BairroCidade bairroCidade = new BairroCidade();
        bairroCidade.getBairro().setId(id);

        if(viewModel.partidaEscolhida == false){
            bairroPartida = bairroCidade;
            viewModel.setBairroPartida(bairroCidade);
            viewModel.bairroPartida.observe(this, bairroObserver);
            viewModel.partidaEscolhida = true;
        } else{
            viewModel.setBairroDestino(bairroCidade);
            bairroDestino = bairroCidade;
            viewModel.bairroDestino.observe(this, bairroDestinoObserver);
            viewModel.destinoEscolhido = true;
        }


        return null;
    }

    private void mostraResultado(){

        DateTime dateTime = new DateTime();
        String dia = DataHoraUtils.getDiaAtual();
        String diaSeguinte = DataHoraUtils.getDiaSeguinte();
        String diaAnterior = DataHoraUtils.getDiaAnterior();

        viewModel.escolhaAtual = 0;

//        int idRadio = binding.radioGroupConsulta.getCheckedRadioButtonId();
//
//        viewModel.setTodos(R.id.radioButtonTodos == idRadio);

        viewModel.carregaResultado(DateTimeFormat.forPattern("HH:mm:00").print(dateTime), dia, diaSeguinte, diaAnterior, false);

        viewModel.itinerario.observe(this, itinerarioObserver);

        geraModalLoading();

//        binding.textViewBairroPartidaResultado.setText(viewModel.);

    }

    public void exibeDados(HorarioItinerarioNome horario){
        viewModel.carregaItinerarioResultado();
        viewModel.itinerarioResultado.observe(this, itinerarioResultadoObserver);
    }

    public void exibeHorarioAnterior(HorarioItinerarioNome horario){
//        binding.textViewHorarioAnterior.setText(DateTimeFormat.forPattern("HH:mm").print(horario.getNomeHorario()));
    }

    public void exibeHorarioSeguinte(HorarioItinerarioNome horario){
//        binding.textViewHorarioSeguinte.setText(DateTimeFormat.forPattern("HH:mm").print(horario.getNomeHorario()));
    }

    @BindingAdapter("app:horario")
    public static void setHorario(TextView textView, String s){
        textView.setText(s);
    }

    @BindingAdapter("app:horario")
    public static void setHorario(TextView textView, Long l){

        if(l != null){
            textView.setText(DateTimeFormat.forPattern("HH:mm")
                    .print(l));
        }

    }

    @BindingAdapter("app:tempo")
    public static void setTempo(TextView textView, DateTime dateTime){
        textView.setText(DateTimeFormat.forPattern("HH:mm")
                .print(dateTime));
    }

    @BindingAdapter("app:distancia")
    public static void setDistancia(TextView textView, Double d){

        if(d != null){

            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);

            textView.setText(nf.format(d)+" Km");
        }

    }

    @BindingAdapter("app:tarifa")
    public static void setTarifa(TextView textView, Double d){

        if(d != null){

            NumberFormat nf = NumberFormat.getCurrencyInstance();
            nf.setMaximumFractionDigits(2);

            textView.setText(nf.format(d));
        }

    }

    public void exibeDadosResultado(){

//        if(viewModel.itinerario.getValue().getHorarioItinerario().getObservacao() == null){
//            binding.textViewObservacao.setVisibility(View.GONE);
//        }
//
//        if(!viewModel.itinerarioResultado.getValue().getItinerario().getAcessivel()){
//            binding.imageView8.setVisibility(View.GONE);
//        }

        binding.btnInverter.setVisibility(View.VISIBLE);
        binding.cardViewResultadoVazio.setVisibility(View.GONE);

    }

    @Override
    public void onDataHoraSelected(Calendar data) {
        String dia = DataHoraUtils.getDiaAtual();

        viewModel.escolhaAtual = 0;

        String hora = DateTimeFormat.forPattern("HH:mm:00").print(data.getTimeInMillis());

//        int idRadio = binding.radioGroupConsulta.getCheckedRadioButtonId();
//
//        viewModel.setTodos(R.id.radioButtonTodos == idRadio);

        geraModalLoading();

        viewModel.carregaResultado(hora, DataHoraUtils.getDiaSelecionado(data), DataHoraUtils.getDiaSeguinteSelecionado(data),
                DataHoraUtils.getDiaAnteriorSelecionado(data), inversao);
        adapterResultado.setDia(DataHoraUtils.getDiaSelecionadoFormatado(data));
        adapterResultado.setHora(DateTimeFormat.forPattern("HH:mm:ss").print(data.getTimeInMillis()));

        //log
        bundle = new Bundle();
        bundle.putString("partida", bairroPartida.getBairro().getNome()+", "+bairroPartida.getNomeCidadeComEstado());
        bundle.putString("destino", bairroDestino.getBairro().getNome()+", "+bairroDestino.getNomeCidadeComEstado());

        bundle.putString("partida_destino", bairroPartida.getBairro().getNome()+", "
                +bairroPartida.getNomeCidadeComEstado()+" x "
                +bairroDestino.getBairro().getNome()+", "+bairroDestino.getNomeCidadeComEstado());

        bundle.putString("data_hora", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").print(new DateTime(data)));
        bundle.putBoolean("sucesso", true);

        mFirebaseAnalytics.logEvent("consulta_itinerario_data_mod", bundle);

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

            ((RecyclerView) formBairro.getView().findViewById(R.id.listBairros)).findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.textViewNome).performClick();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    DestaqueUtils.geraDestaqueUnico(ctx, binding.listCidades.getChildAt(1).findViewById(R.id.circleView2),
                            "Escolha a cidade de destino", "Depois, escolha a cidade que será o destino da sua viagem!", l3, false, true);
                }
            }, 300);

        }
    };

    TapTargetView.Listener l3 = new TapTargetView.Listener(){
        @Override
        public void onTargetClick(TapTargetView view) {
            super.onTargetClick(view);

            binding.listCidades.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.circleView2).performClick();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    DestaqueUtils.geraDestaqueUnico(formBairro.getDialog(), ((RecyclerView) formBairro.getView()
                                    .findViewById(R.id.listBairros)).findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.textViewNome),
                            "Escolha o bairro de destino", "Por fim, escolha o bairro de destino!", l4, false, false);
                }
            }, 300);

        }
    };

    TapTargetView.Listener l4 = new TapTargetView.Listener(){
        @Override
        public void onTargetClick(TapTargetView view) {
            super.onTargetClick(view);
            ((RecyclerView) formBairro.getView().findViewById(R.id.listBairros)).findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.textViewNome).performClick();
        }
    };

    TapTargetView.Listener l5 = new TapTargetView.Listener(){
        @Override
        public void onTargetClick(TapTargetView view) {
            super.onTargetClick(view);

//            YoYo.with(Techniques.Swing)
//                    .duration(5000)
//                    .repeat(1)
//                    .playOn(findViewById(R.id.textViewHorario));
        }
    };



    @Override
    public List<TapTarget> criaTour() {

        DestaqueUtils.geraDestaqueUnico(this, binding.listCidadesPartida.getChildAt(1).findViewById(R.id.circleView2), "Escolha a cidade de partida",
                "Escolha primeiro a cidade da qual você vai iniciar a sua viagem!", new TapTargetView.Listener(){
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);

                binding.listCidadesPartida.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.circleView2).performClick();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        DestaqueUtils.geraDestaqueUnico(formBairro.getDialog(), ((RecyclerView) formBairro.getView().findViewById(R.id.listBairros)).findViewHolderForAdapterPosition(1)
                                        .itemView.findViewById(R.id.textViewNome),
                                "Escolha o bairro de partida", "Escolha então o bairro de partida. Se houver apenas uma opção, o sistema escolherá automaticamente!", l2,
                                false, false);
                    }
                }, 300);

            }
        }, false, true);

        List<TapTarget> targets = new ArrayList<>();

        return targets;
    }

}
