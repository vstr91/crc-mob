package br.com.vostre.circular.view;

import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputFilter;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.vision.clearcut.LogUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.vostre.circleview.CircleView;
import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityItinerariosBinding;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.log.LogConsulta;
import br.com.vostre.circular.model.log.LogItinerario;
import br.com.vostre.circular.model.log.TiposLog;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.DBUtils;
import br.com.vostre.circular.utils.DataHoraUtils;
import br.com.vostre.circular.utils.DestaqueUtils;
import br.com.vostre.circular.utils.LogConsultaUtils;
import br.com.vostre.circular.utils.WidgetUtils;
import br.com.vostre.circular.view.adapter.CidadeAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioResultadoAdapter;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.view.listener.FeriadoListener;
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

    RecyclerView listItinerariosPorLinha;
    ItinerarioAdapter adapterItinerariosPorLinha;

    RecyclerView listItinerariosPorDestino;
    ItinerarioAdapter adapterItinerariosPorDestino;

    static AppCompatActivity ctx;

    ItinerariosViewModel viewModel;

    String dia = "";
    String diaSeguinte = "";
    boolean consultaDiaSeguinte = false;

    BairroCidade bairroPartida;
    BairroCidade bairroDestino;

    BairroCidade bairroDestinoConsulta;
    CidadeAdapter adapterDestinoConsulta;

    boolean inversao = false;

    Bundle bundle;
    ProgressBar progressBar;

    FormBairro formBairro;

    boolean exibindoTour = false;

    // Para consulta com data modificada
    Calendar dataEscolhida;
    String diaEscolhido;
    String horaEscolhida;

    TabHost tabHost;

    RecyclerView listCidadesDestinoConsulta;

    int currentTab = 0;
    Location ultimoLocal;

    Bairro bairroAtual;

//    LogItinerario log;

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
        viewModel.isFeriado.observe(this, feriadoObserver);
        viewModel.localAtual.observe(this, localObserver);

        ultimoLocal = new Location(LocationManager.GPS_PROVIDER);

        viewModel.iniciarAtualizacoesPosicao();

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            startLocationUpdates();
        }

        viewModel.cidadesDestinoConsulta.observe(this, cidadesDestinoConsultaObserver);

        binding.setViewModel(viewModel);

        viewModel.checaFeriado(Calendar.getInstance());

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

        listItinerariosPorLinha = binding.listItinerarios;
        adapterItinerariosPorLinha = new ItinerarioAdapter(viewModel.itinerariosPorLinha.getValue(), this);
        listItinerariosPorLinha.setAdapter(adapterItinerariosPorLinha);

        listItinerariosPorDestino = binding.listItinerariosDestino;
        adapterItinerariosPorDestino = new ItinerarioAdapter(viewModel.itinerariosPorDestino.getValue(), this);
        listItinerariosPorDestino.setAdapter(adapterItinerariosPorDestino);

        listCidadesDestinoConsulta = binding.listCidadesDestinoConsulta;
        adapterDestinoConsulta = new CidadeAdapter(viewModel.cidadesDestinoConsulta.getValue(), this);
        adapterDestinoConsulta.setListener(this);

        listCidadesDestinoConsulta.setAdapter(adapterDestinoConsulta);

        tabHost = binding.tabs;
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Partida/Destino");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Tradicional");
        tabHost.addTab(spec);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Linha");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Por Linha");
        tabHost.addTab(spec2);

        TabHost.TabSpec spec3 = tabHost.newTabSpec("Destino");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Por Destino");
        tabHost.addTab(spec3);

        final TabWidget tw = (TabWidget) tabHost.findViewById(android.R.id.tabs);
        tw.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        WidgetUtils.formataTabs(tw, this, 11, 0);


        // Forcando maiusculas
        InputFilter[] editFilters = binding.editTextLinha.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        binding.editTextLinha.setFilters(newFilters);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId)
            {
                View currentView = tabHost.getCurrentView();

                if(tabHost.getCurrentTab() == 0){

                    if(menu != null){
                        menu.getItem(0).setVisible(true);
                    }

                } else{

                    if(menu != null){
                        menu.getItem(0).setVisible(false);
                    }

                }

                if (tabHost.getCurrentTab() > currentTab)
                {
                    currentView.setAnimation( inFromRightAnimation() );
                }
                else
                {
                    currentView.setAnimation( outToRightAnimation() );
                }

                currentTab = tabHost.getCurrentTab();
            }
        });

        // tab por linha

        // fim tab por linha

        //DBUtils.exportDB(ctx);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean retorno = super.onCreateOptionsMenu(menu);

        if(menu != null){

            if(tabHost.getCurrentTab() == 0){
                menu.getItem(0).setVisible(true);
            } else{
                menu.getItem(0).setVisible(false);
            }

        }

        this.menu = menu;

        return retorno;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            startLocationUpdates();
        }

        if(menu != null){

            if(tabHost.getCurrentTab() == 0){
                menu.getItem(0).setVisible(true);
            } else{
                menu.getItem(0).setVisible(false);
            }

        }

    }

    @Override
    protected void onPause() {

        stopLocationUpdates();

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

    // Tab por linha

    public void onClickBtnProcurarPorLinha(View v){
        String linha = binding.editTextLinha.getText().toString().trim().replace(" ", "").replace("-", "").replace("_", "");
        viewModel.buscarPorLinha(linha);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        viewModel.itinerariosPorLinha.observe(this, itinerarioPorLinhaObserver);

//        log = (LogItinerario) iniciaLog(TiposLog.ITINERARIO_POR_LINHA.name());
//        log.setLinha(linha);

        geraModalLoading(1);
    }

    // Fim Tab por linha

    // tab destino

    public void onClickBtnEditarDestinoConsulta(View v){
        binding.cardViewDestinoConsulta.setVisibility(View.GONE);
        binding.cardViewListDestinoConsulta.setVisibility(View.VISIBLE);
//        binding.listResultadosConsulta.setVisibility(View.GONE);
    }

    // fim tab destino

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
        inversao = false;

        bairroPartida = null;
        viewModel.setBairroPartida(null);
        viewModel.setBairroDestino(null);

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

        bairroDestino = null;
        inversao = false;

        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("edicao_destino", bundle);
    }

    public void onClickBtnEditarPartidaConsulta(View v){
        binding.cardViewDestinoConsulta.setVisibility(View.GONE);
        binding.cardViewListDestinoConsulta.setVisibility(View.VISIBLE);
        binding.listItinerariosDestino.setVisibility(View.GONE);

        //log
//        bundle = new Bundle();
//        mFirebaseAnalytics.logEvent("edicao_partida", bundle);

    }

    public void onClickBtnInverter(View v){

        System.out.println("BAIRRO P>> "+bairroPartida.getBairro().getNome()+" - "+bairroPartida.getNomeCidadeComEstado());
        System.out.println("BAIRRO D>> "+bairroDestino.getBairro().getNome()+" - "+bairroDestino.getNomeCidadeComEstado());

        inversao = !inversao;
        //viewModel.escolhaAtual = 0;
        BairroCidade bairro = bairroPartida;
        bairroPartida = bairroDestino;
        bairroDestino = bairro;

        System.out.println("BAIRRO P-D>> "+bairroPartida.getBairro().getNome()+" - "+bairroPartida.getNomeCidadeComEstado());
        System.out.println("BAIRRO D-D>> "+bairroDestino.getBairro().getNome()+" - "+bairroDestino.getNomeCidadeComEstado());

        binding.setPartida(bairroPartida);
        binding.setDestino(bairroDestino);

        DateTime dateTime = new DateTime();
        String dia = DataHoraUtils.getDiaAtual();
        String diaSeguinte = DataHoraUtils.getDiaSeguinte();
        String diaAnterior = DataHoraUtils.getDiaAnterior();

        System.out.println("MYPartida Antes>> "+viewModel.myPartida.getBairro()+" - "+viewModel.myPartida.getNomeCidadeComEstado());
        System.out.println("MYDestino Antes>> "+viewModel.myDestino.getBairro()+" - "+viewModel.myDestino.getNomeCidadeComEstado());

        viewModel.myPartida = bairroPartida;
        viewModel.myDestino = bairroDestino;

        System.out.println("MYPartida>> "+viewModel.myPartida.getBairro()+" - "+viewModel.myPartida.getNomeCidadeComEstado());
        System.out.println("MYDestino>> "+viewModel.myDestino.getBairro()+" - "+viewModel.myDestino.getNomeCidadeComEstado());

//        log = (LogItinerario) iniciaLog(TiposLog.ITINERARIO_INVERSAO.name());

        viewModel.carregaResultadoNovo(DateTimeFormat.forPattern("HH:mm:00").print(dateTime), dia, diaSeguinte, diaAnterior, inversao);

        geraModalLoading(0);

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

    // 0 = versao anterior | 1 = busca por linha
    private void geraModalLoading(int tipo) {
        binding.fundo.setVisibility(View.VISIBLE);

        switch(tipo){
            case 0:
                binding.textViewCarregando.setText(R.string.texto_buscando_rota);
                break;
            case 1:
                binding.textViewCarregando.setText(R.string.texto_buscando_por_linha);
                break;
            case 2:
                binding.textViewCarregando.setText(R.string.texto_buscando_por_destino);
                break;
        }

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

                if(bairroPartida != null && bairroDestino != null){
                    bundle.putString("partida", bairroPartida.getBairro().getNome() + ", " + bairroPartida.getNomeCidadeComEstado());
                    bundle.putString("destino", bairroDestino.getBairro().getNome() + ", " + bairroDestino.getNomeCidadeComEstado());

                    bundle.putString("partida_destino", bairroPartida.getBairro().getNome() + ", "
                            + bairroPartida.getNomeCidadeComEstado() + " x "
                            + bairroDestino.getBairro().getNome() + ", " + bairroDestino.getNomeCidadeComEstado());

                    bundle.putInt("itinerarios", itinerarios.size());
                    bundle.putString(FirebaseAnalytics.Param.END_DATE, DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").print(DateTime.now()));
                    bundle.putBoolean("sucesso", true);

//                    if(log != null && bairroPartida != null && bairroDestino != null){
//                        log.setPartida(bairroPartida.getBairro().getId());
//                        log.setDestino(bairroDestino.getBairro().getId());
//                        finalizaLog();
//                    }

                    mFirebaseAnalytics.logEvent("consulta_itinerario", bundle);
                }



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

//                if(log != null && bairroPartida != null && bairroDestino != null){
//                    log.setPartida(bairroPartida.getBairro().getId());
//                    log.setDestino(bairroDestino.getBairro().getId());
//                    finalizaLog();
//                }

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

    Observer<Location> localObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location local) {

            if(local.getLatitude() != 0.0 && local.getLongitude() != 0.0 && local.distanceTo(ultimoLocal) > 100){
                ultimoLocal = local;

                viewModel.buscarParadasProximas(getApplicationContext(), local);
                viewModel.paradasProximas.observe(ctx, paradasProximasObserver);

            }

        }
    };

    Observer<List<ParadaBairro>> paradasProximasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {

            if(paradas.size() > 0){

                Location poiLocation = new Location(LocationManager.GPS_PROVIDER);
                poiLocation.setLatitude(ultimoLocal.getLatitude());
                poiLocation.setLongitude(ultimoLocal.getLongitude());

                for(ParadaBairro i : paradas){
                    Location paradaLocation = new Location(LocationManager.GPS_PROVIDER);
                    paradaLocation.setLatitude(i.getParada().getLatitude());
                    paradaLocation.setLongitude(i.getParada().getLongitude());

                    i.setDistancia(paradaLocation.distanceTo(poiLocation));
                }

                Collections.sort(paradas, new Comparator<ParadaBairro>() {
                    @Override
                    public int compare(ParadaBairro paradaBairro, ParadaBairro t1) {
                        return paradaBairro.getDistancia() > t1.getDistancia() ? 1 : -1;
                    }
                });

                ParadaBairro parada = paradas.get(0);

                bairroAtual = new Bairro();
                bairroAtual.setId(parada.getIdBairro());
                bairroAtual.setNome(parada.getNomeBairro());
                bairroAtual.setCidade(parada.getIdCidade());

                //Toast.makeText(ctx, parada.getNomeBairroComCidade(), Toast.LENGTH_SHORT).show();
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

    Observer<List<CidadeEstado>> cidadesDestinoConsultaObserver = new Observer<List<CidadeEstado>>() {
        @Override
        public void onChanged(List<CidadeEstado> cidades) {
            adapterDestinoConsulta.cidades = cidades;
            adapterDestinoConsulta.notifyDataSetChanged();
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

    Observer<BairroCidade> bairroConsultaObserver = new Observer<BairroCidade>() {
        @Override
        public void onChanged(BairroCidade bairro) {

            if(bairro != null){
                mostraDadosBairro(bairro, 0);
                bairroDestinoConsulta = bairro;

                if(bairroAtual != null && gpsAtivo){
                    viewModel.buscarPorDestino(bairro.getBairro().getId(), bairroAtual.getId());
                } else{
                    viewModel.buscarPorDestino(bairro.getBairro().getId(), "");
                }

                viewModel.itinerariosPorDestino.observe(ctx, itinerarioPorDestinoObserver);

                geraModalLoading(2);

            }

        }
    };

    Observer<List<ItinerarioPartidaDestino>> itinerarioPorLinhaObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {


            if(itinerarios.size() > 0){
                adapterItinerariosPorLinha.itinerarios = itinerarios;
                adapterItinerariosPorLinha.notifyDataSetChanged();
            } else{
                adapterItinerariosPorLinha.itinerarios = new ArrayList<>();
                adapterItinerariosPorLinha.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Nenhum itinerário encontrado...", Toast.LENGTH_SHORT).show();
            }

//            if(log != null){
//                finalizaLog();
//            }

            ocultaModalLoading();

        }
    };

//    private LogConsulta iniciaLog(String tipo){
//        log = (LogItinerario) LogConsultaUtils.iniciaLog(getApplicationContext());
//        log.setTipo(tipo);
//        return log;
//    }

//    private void finalizaLog() {
//        LogConsultaUtils.finalizaLog(ctx, log, ctx.getLocalClassName());
//    }

    Observer<List<ItinerarioPartidaDestino>> itinerarioPorDestinoObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {


            if(itinerarios.size() > 0){

                binding.listItinerariosDestino.setVisibility(View.VISIBLE);

                adapterItinerariosPorDestino.itinerarios = itinerarios;
                adapterItinerariosPorDestino.destaca = viewModel.destacaItinerario;
                adapterItinerariosPorDestino.notifyDataSetChanged();
            } else{
                Toast.makeText(getApplicationContext(), "Nenhum itinerário encontrado...", Toast.LENGTH_SHORT).show();
            }

//            if(log != null){
//                log.setTipo(TiposLog.ITINERARIO_POR_DESTINO.name());
//            }
//
//            finalizaLog();

            ocultaModalLoading();

        }
    };

    private void mostraDadosBairro(BairroCidade bairro, int tipo) {

        switch(tabHost.getCurrentTab()){
            case 0:

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

                break;
            case 2:

                binding.cardViewDestinoConsulta.setVisibility(View.VISIBLE);
                binding.setDestinoConsulta(bairro);
                binding.cardViewListDestinoConsulta.setVisibility(View.GONE);

                break;
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

    Observer<Boolean> feriadoObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean isFeriado) {

            if(bairroPartida != null && bairroDestino != null){

                if(isFeriado){
                    viewModel.carregaResultadoNovo(horaEscolhida, "domingo", DataHoraUtils.getDiaSeguinteSelecionado(dataEscolhida),
                            DataHoraUtils.getDiaAnteriorSelecionado(dataEscolhida), inversao);
                    binding.textViewFeriado.setVisibility(View.VISIBLE);
                } else{
                    viewModel.carregaResultadoNovo(horaEscolhida, DataHoraUtils.getDiaSelecionado(dataEscolhida), DataHoraUtils.getDiaSeguinteSelecionado(dataEscolhida),
                            DataHoraUtils.getDiaAnteriorSelecionado(dataEscolhida), inversao);
                    binding.textViewFeriado.setVisibility(View.GONE);
                }

                if(dataEscolhida != null){
                    adapterResultado.setDia(DataHoraUtils.getDiaSelecionadoFormatado(dataEscolhida));
                    adapterResultado.setHora(DateTimeFormat.forPattern("HH:mm:ss").print(dataEscolhida.getTimeInMillis()));
                }

                //log
                bundle = new Bundle();
                bundle.putString("partida", bairroPartida.getBairro().getNome()+", "+bairroPartida.getNomeCidadeComEstado());
                bundle.putString("destino", bairroDestino.getBairro().getNome()+", "+bairroDestino.getNomeCidadeComEstado());

                bundle.putString("partida_destino", bairroPartida.getBairro().getNome()+", "
                        +bairroPartida.getNomeCidadeComEstado()+" x "
                        +bairroDestino.getBairro().getNome()+", "+bairroDestino.getNomeCidadeComEstado());

                bundle.putString("data_hora", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").print(new DateTime(dataEscolhida)));
                bundle.putBoolean("sucesso", true);

//                if(log != null){
//                    log.setPartida(bairroPartida.getBairro().getId());
//                    log.setDestino(bairroDestino.getBairro().getId());
//                    log.setTipo(TiposLog.ITINERARIO_INVERSAO.name());
//                    LogConsultaUtils.finalizaLog(getApplicationContext(), log, ctx.getLocalClassName());
//                }

                mFirebaseAnalytics.logEvent("consulta_itinerario_data_mod", bundle);
            }

        }

    };

    @Override
    public String onSelected(String id) {

        switch(tabHost.getCurrentTab()) {
            case 0:
                // consulta tradicional



//                log = (LogItinerario) iniciaLog(TiposLog.ITINERARIO_TRADICIONAL.name());

                formBairro = new FormBairro();

                Bundle bundle = new Bundle();
                bundle.putString("cidade", id);

                if (bairroPartida != null) {
                    formBairro.setBairroPartida(bairroPartida);
                }

                formBairro.setArguments(bundle);
                formBairro.setCtx(ctx.getApplication());
                formBairro.setListener(this);
                formBairro.show(ctx.getSupportFragmentManager(), "formBairro");

                this.bundle = new Bundle();
                this.bundle.putString(FirebaseAnalytics.Param.START_DATE, DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").print(DateTime.now()));

                break;
            case 2:
                // consulta nova

//                log = (LogItinerario) LogConsultaUtils.iniciaLog(getApplicationContext());
//                log.setDataInicio(DateTime.now());

                formBairro = new FormBairro();

                Bundle bundleConsulta = new Bundle();
                bundleConsulta.putString("cidade", id);

                formBairro.setArguments(bundleConsulta);
                formBairro.setCtx(ctx.getApplication());
                formBairro.setListener(this);
                formBairro.show(ctx.getSupportFragmentManager(), "formBairro");

                break;
        }



        return null;
    }

    @Override
    public String onItemSelected(String id) {

        switch(tabHost.getCurrentTab()){
            case 0:
                // consulta tradicional

                BairroCidade bairroCidade = new BairroCidade();
                bairroCidade.getBairro().setId(id);

                if(viewModel.partidaEscolhida == false){

//                    if(log != null){
//                        log.setPartida(id);
//                    }

                    bairroPartida = bairroCidade;
                    viewModel.setBairroPartida(bairroCidade);
                    viewModel.bairroPartida.observe(this, bairroObserver);
                    viewModel.partidaEscolhida = true;
                } else{

//                    if(log != null){
//                        log.setDestino(id);
//                    }

                    //viewModel.setBairroPartida(bairroPartida);
                    viewModel.setBairroDestino(bairroCidade);
                    bairroDestino = bairroCidade;
                    viewModel.bairroDestino.observe(this, bairroDestinoObserver);
                    viewModel.destinoEscolhido = true;
                }

//                if(log != null){
//                    log.setTipo(TiposLog.ITINERARIO_TRADICIONAL.name());
//                }

                break;
            case 2:
                // consulta nova
                BairroCidade bairroCidadeConsulta = new BairroCidade();
                bairroCidadeConsulta.getBairro().setId(id);

//                if(log != null){
//                    log.setDestino(id);
//                    log.setTipo(TiposLog.ITINERARIO_POR_DESTINO.name());
//                }

                bairroDestinoConsulta = bairroCidadeConsulta;
                viewModel.setBairroDestinoConsulta(bairroCidadeConsulta);
                viewModel.bairroDestinoConsulta.observe(this, bairroConsultaObserver);
                break;
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

        if(viewModel.isFeriado.getValue()){
            viewModel.carregaResultadoNovo(DateTimeFormat.forPattern("HH:mm:00").print(dateTime), "domingo", diaSeguinte, diaAnterior, inversao);
        } else{
//            viewModel.carregaResultadoNovo(DateTimeFormat.forPattern("10:00:00").print(dateTime), dia, diaSeguinte, diaAnterior, false);
            viewModel.carregaResultadoNovo(DateTimeFormat.forPattern("HH:mm:00").print(dateTime), dia, diaSeguinte, diaAnterior, inversao);
        }

        viewModel.itinerario.observe(this, itinerarioObserver);

        geraModalLoading(0);

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

        geraModalLoading(0);

        viewModel.checaFeriado(data);

        this.dataEscolhida = data;
        this.diaEscolhido = dia;
        this.horaEscolhida = hora;

    }

    private boolean checarPermissoes(){
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onGpsChanged(boolean ativo) {

        if(ativo){
            viewModel.buscarParadasProximas(getApplicationContext(), ultimoLocal);
        } else{

        }

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        if(checarPermissoes()){

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setMaxWaitTime(5000);

            if(viewModel != null){
                viewModel.mFusedLocationClient.requestLocationUpdates(locationRequest,
                        viewModel.mLocationCallback,
                        null);
            }

        }

    }

    private void stopLocationUpdates() {

        if(viewModel != null && viewModel.mFusedLocationClient != null){
            viewModel.mFusedLocationClient.removeLocationUpdates(viewModel.mLocationCallback);
        }

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

        View v =  binding.listCidadesPartida.getChildAt(1);

        if(v != null && tabHost.getCurrentTab() == 0){

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
        } else if(tabHost.getCurrentTab() == 1){

        } else{
            Toast.makeText(getApplicationContext(), "Houve um problema ao criar o tour. Locais de partida não encontrados. Por favor tente novamente mais tarde.", Toast.LENGTH_LONG).show();
        }



        List<TapTarget> targets = new ArrayList<>();

        return targets;
    }

    public Animation inFromRightAnimation()
    {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(240);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public Animation outToRightAnimation()
    {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(240);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

}
