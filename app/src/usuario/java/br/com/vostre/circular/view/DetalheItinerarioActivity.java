package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalheItinerarioBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.Legenda;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.CustomLayoutManager;
import br.com.vostre.circular.utils.DestaqueUtils;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.utils.WidgetUtils;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioInfoAdapter;
import br.com.vostre.circular.view.adapter.LegendaAdapter;
import br.com.vostre.circular.view.adapter.ParadaRuaAdapter;
import br.com.vostre.circular.view.adapter.SecaoItinerarioAdapter;
import br.com.vostre.circular.view.listener.LegendaListener;
import br.com.vostre.circular.view.listener.PartidaEDestinoListener;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.view.viewHolder.LegendaViewHolder;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DetalheItinerarioActivity extends BaseActivity {

    ActivityDetalheItinerarioBinding binding;
    DetalhesItinerarioViewModel viewModel;
    HorarioItinerarioAdapter adapterHorarios;
    SecaoItinerarioAdapter adapterSecoes;
    ParadaRuaAdapter adapterRuas;

    ItinerarioInfoAdapter adapterInfos;

    RecyclerView listHorarios;
    RecyclerView listSecoes;
    RecyclerView listRuas;
    AppCompatActivity ctx;

    MapView map;
    IMapController mapController;
    MyLocationNewOverlay mLocationOverlay;

    BottomSheetDialog bsd;
    BottomSheetDialog bsdRuas;
    boolean flagFavorito = false;
    RecyclerView listLegenda;
    RecyclerView listLegendaInfo;

    RecyclerView listInfos;

    String horario;
    boolean mapaOculto = false;
    int tamanhoOriginalMapa = 0;

    String itinerarioPartida;
    String itinerarioDestino;

    String paradaPartida;
    String paradaDestino;

    Bundle bundle;

    boolean inversao = false;
    String itinerario;

    int contaProcessamento = 0;
    boolean mostraRuas = false;

    boolean trechoIsolado = false;

    TabHost tabHost;

    int currentTab = 0;

    Long iniRuas;
    Long iniPartida;
    Long iniDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhe_itinerario);
        binding.getRoot().setDrawingCacheEnabled(true);
        binding.setVw(this);
        binding.setLifecycleOwner(this);

        contaProcessamento = 0;

        if(!inversao){
            super.onCreate(savedInstanceState);
            setTitle("Detalhes");
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

            ctx = this;

            viewModel = ViewModelProviders.of(this).get(DetalhesItinerarioViewModel.class);

            binding.setViewModel(viewModel);

            itinerario = getIntent().getStringExtra("itinerario");
            horario = getIntent().getStringExtra("horario");
        }

        listLegenda = binding.listLegenda;
        listLegendaInfo = binding.listLegendaInfo;

        listInfos = binding.listInfos;

        //inversao false == primeira entrada;
        if(inversao){
            String tempItinerario = itinerarioPartida;
            itinerarioPartida = itinerarioDestino;
            itinerarioDestino = tempItinerario;

            String tempParada = paradaPartida;
            paradaPartida = paradaDestino;
            paradaDestino = tempParada;

            viewModel.setItinerario(itinerario, paradaPartida, paradaDestino,
                    itinerarioPartida, itinerarioDestino);

            iniPartida = System.nanoTime();
            iniDestino = System.nanoTime();

            viewModel.itinerario.observe(this, itinerarioObserver);

            viewModel.partida.observe(this, partidaObserver);
            viewModel.destino.observe(this, destinoObserver);

            listHorarios = binding.listHorarios;
            adapterHorarios = new HorarioItinerarioAdapter(viewModel.horarios.getValue(), this);
            listHorarios.setAdapter(adapterHorarios);
        } else{
            itinerarioPartida = getIntent().getStringExtra("itinerarioPartida");
            itinerarioDestino = getIntent().getStringExtra("itinerarioDestino");

            paradaPartida = getIntent().getStringExtra("paradaPartida");
            paradaDestino = getIntent().getStringExtra("paradaDestino");

            // nova busca por trecho
            trechoIsolado = getIntent().getBooleanExtra("trechoIsolado", false);
            viewModel.trechoIsolado = trechoIsolado;

            viewModel.partidaConsulta = getIntent().getStringExtra("partidaConsulta");
            viewModel.destinoConsulta = getIntent().getStringExtra("destinoConsulta");

            iniPartida = System.nanoTime();
            iniDestino = System.nanoTime();

            viewModel.setItinerario(getIntent().getStringExtra("itinerario"), paradaPartida, paradaDestino,
                    itinerarioPartida, itinerarioDestino);
            horario = getIntent().getStringExtra("horario");

            viewModel.itinerario.observe(this, itinerarioObserver);

            viewModel.partida.observe(this, partidaObserver);
            viewModel.destino.observe(this, destinoObserver);

            listHorarios = binding.listHorarios;
            adapterHorarios = new HorarioItinerarioAdapter(viewModel.horarios.getValue(), this);
            listHorarios.setAdapter(adapterHorarios);
        }

        bsd = new BottomSheetDialog(ctx);
        bsd.setCanceledOnTouchOutside(true);

        bsd.setContentView(R.layout.bottom_sheet_secoes);

        listSecoes = bsd.findViewById(R.id.listSecoes);
        ImageButton btnFechar = bsd.findViewById(R.id.btnFechar);
        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.dismiss();
            }
        });

        adapterSecoes = new SecaoItinerarioAdapter(viewModel.secoes.getValue(), this);
        listSecoes.setAdapter(adapterSecoes);

        viewModel.horarios.observe(this, horariosObserver);
        viewModel.localAtual.observe(this, localObserver);

        bsdRuas = new BottomSheetDialog(ctx);
        bsdRuas.setCanceledOnTouchOutside(true);

        bsdRuas.setContentView(R.layout.bottom_sheet_ruas);

        listRuas = bsdRuas.findViewById(R.id.listRuas);
        ImageButton btnFecharRuas = bsdRuas.findViewById(R.id.btnFechar);
        btnFecharRuas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsdRuas.dismiss();
            }
        });

        adapterRuas = new ParadaRuaAdapter(viewModel.ruas.getValue(), this);
        binding.listRuas.setAdapter(adapterRuas);

//        binding.btnVerRuas.setVisibility(View.GONE);

        binding.textView37.setVisibility(View.GONE);
        binding.textViewObservacao.setVisibility(View.GONE);

        geraModalLoading();

        configuraMapa();

        tabHost = binding.tabs;
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Horários");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Quadro de Horários");
        tabHost.addTab(spec);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Infos");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Outras Infos");
        tabHost.addTab(spec2);

        final TabWidget tw = (TabWidget) tabHost.findViewById(android.R.id.tabs);
        tw.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        WidgetUtils.formataTabs(tw, this, 11, 0);

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean retorno = super.onCreateOptionsMenu(menu);

        this.menu = menu;

        return retorno;
    }

    private void configuraMapa() {
        Long mapInit = System.nanoTime();

        map = binding.map;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(getApplicationContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        mapController = map.getController();
        mapController.setZoom(10.5d);
//        GeoPoint startPoint = new GeoPoint(-22.470804460339885, -43.82463455200195);
//        mapController.setCenter(startPoint);

        map.setMaxZoomLevel(19d);
        map.setMinZoomLevel(8d);

//        int preferenciaMapa = PreferenceUtils.carregarPreferenciaInt(ctx, "mapa");
//
//        if(preferenciaMapa == 0){
//            tamanhoOriginalMapa = binding.cardViewItinerario.getLayoutParams().height;
//            binding.map.setVisibility(View.GONE);
//            binding.cardViewItinerario.getLayoutParams().height = WRAP_CONTENT;
//            mapaOculto = true;
//        }

        map.setBuiltInZoomControls(false);

        //zoom ao tocar no mapa - inicia modo acompanhamento
        MapEventsReceiver receiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                GeoPoint g = new GeoPoint(viewModel.paradas.getValue().get(0).getParada().getLatitude(), viewModel.paradas.getValue().get(0).getParada().getLongitude());

                mapController.setCenter(g);
                map.getController().animateTo(g, 18d, 1000L);

                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay overlayEvents = new MapEventsOverlay(getBaseContext(), receiver);
        map.getOverlays().add(overlayEvents);

        Long mapFin = System.nanoTime();

        System.out.println("TEMPO TOTAL MAPA: "+TimeUnit.SECONDS.convert(mapFin - mapInit, TimeUnit.NANOSECONDS));

    }

    public void onClickBtnFavorito(View v){
        List<String> lstItinerarios = PreferenceUtils.carregaItinerariosFavoritos(getApplicationContext());

        List<ParadaBairro> paradas = viewModel.paradas.getValue();

        if(!flagFavorito){
            SnackbarHelper.notifica(v, "Itinerário adicionado aos favoritos!", Snackbar.LENGTH_LONG);
            binding.imageButton4.setImageResource(R.drawable.ic_star_white_24dp);
            flagFavorito = true;

            lstItinerarios.add(paradas.get(0).getIdBairro()+"|"+paradas.get(paradas.size()-1).getIdBairro());

            bundle = new Bundle();
            bundle.putString("partida_destino",
                    paradas.get(0).getParada().getNome()+", "+paradas.get(0).getNomeBairroComCidade()+" x "
                            +paradas.get(paradas.size()-1).getParada().getNome()+", "+paradas.get(paradas.size()-1).getNomeBairroComCidade());
            mFirebaseAnalytics.logEvent("fav_itinerario_adicionado", bundle);

        } else{
            SnackbarHelper.notifica(v, "Itinerário removido dos favoritos!", Snackbar.LENGTH_LONG);
            binding.imageButton4.setImageResource(R.drawable.ic_star_border_white_24dp);
            flagFavorito = false;

            lstItinerarios.remove(paradas.get(0).getIdBairro()+"|"+paradas.get(paradas.size()-1).getIdBairro());

            bundle = new Bundle();
            bundle.putString("partida_destino",
                    paradas.get(0).getParada().getNome()+", "+paradas.get(0).getNomeBairroComCidade()+" x "
                            +paradas.get(paradas.size()-1).getParada().getNome()+", "+paradas.get(paradas.size()-1).getNomeBairroComCidade());
            mFirebaseAnalytics.logEvent("fav_itinerario_removido", bundle);

        }

        PreferenceUtils.gravaItinerariosFavoritos(lstItinerarios, getApplicationContext());
    }

    public void onClickBtnShare(View v){

//        try {
//            getScreenViewBitmap(binding.getRoot())
//                    .compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(getApplication().getFilesDir()+"/image.jpg"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        Toast.makeText(getApplicationContext(), "Preparando imagem para compartilhamento.", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(ctx, DetalheItinerarioImpressaoActivity.class);
        i.putExtra("itinerario", viewModel.itinerario.getValue().getItinerario().getId());

        i.putExtra("itinerarioPartida", itinerarioPartida);
        i.putExtra("itinerarioDestino", itinerarioDestino);

        i.putExtra("paradaPartida", paradaPartida);
        i.putExtra("paradaDestino", paradaDestino);

        i.putExtra("trechoIsolado", trechoIsolado);

        i.putExtra("partidaConsulta", viewModel.partidaConsulta);
        i.putExtra("destinoConsulta", viewModel.destinoConsulta);

        if(viewModel.qtdItinerarios.size() <= 1){
            i.putExtra("imprimePorPartidaEDestino", false);
        }

        //log
        bundle = new Bundle();

        ParadaBairro partida = viewModel.partida.getValue();
        ParadaBairro destino = viewModel.destino.getValue();

        if(partida != null && destino != null){
            bundle.putString("itinerario", partida.getParada().getNome()+" - "+partida.getNomeBairroComCidade()+" x "+destino.getParada().getNome()+" - "+destino.getNomeBairroComCidade());
        }

        mFirebaseAnalytics.logEvent("horario_compartilhado", bundle);

        ctx.startActivity(i);

//        Bitmap b = binding.getRoot().getDrawingCache();
//        try {
//            System.out.println("LOCAL::: "+getApplication().getFilesDir()+"/image.jpg");
//            b.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(getApplication().getFilesDir()+"/image.jpg"));
//            Toast.makeText(getApplicationContext(), "Exportado!", Toast.LENGTH_SHORT).show();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    private Bitmap getScreenViewBitmap(View v) {
        v.setDrawingCacheEnabled(true);

        // this is the important code :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        //v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        //        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.buildDrawingCache();
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false); // clear drawing cache
        return b;
    }

    public void ocultarMapa(View v, Boolean automatica){

        if(mapaOculto){
            binding.map.setVisibility(View.VISIBLE);
            binding.cardViewItinerario.getLayoutParams().height = tamanhoOriginalMapa;

            if(!automatica){
                PreferenceUtils.salvarPreferencia(ctx, "mapa", 1);
                //log
                bundle = new Bundle();
                bundle.putBoolean("oculto", false);
                mFirebaseAnalytics.logEvent("mapa_oculto", bundle);
            }

            mapaOculto = false;


        } else{
            tamanhoOriginalMapa = binding.cardViewItinerario.getLayoutParams().height;
            binding.map.setVisibility(View.GONE);
            binding.cardViewItinerario.getLayoutParams().height = WRAP_CONTENT;

            if(!automatica){
                PreferenceUtils.salvarPreferencia(ctx, "mapa", 0);
                //log
                bundle = new Bundle();
                bundle.putBoolean("oculto", true);
                mFirebaseAnalytics.logEvent("mapa_oculto", bundle);
            }

            mapaOculto = true;

        }

    }

    public void inverteConsulta(View v){
        inversao = true;
        this.onCreate(getIntent().getExtras());
    }

    public void btnSecoesClick(View v){
        bsd.show();

        //log
        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("secoes_consulta", bundle);
    }

    public void btnRuasClick(View v){
        bsdRuas.show();

        //log
//        bundle = new Bundle();
//        mFirebaseAnalytics.logEvent("secoes_consulta", bundle);
    }

    Observer<List<HorarioItinerarioNome>> horariosObserver = new Observer<List<HorarioItinerarioNome>>() {
        @Override
        public void onChanged(List<HorarioItinerarioNome> horarios) {

            List<Legenda> dados = null;
            int[] cores = null;

            if(viewModel.qtdItinerarios.size() > 1){
                binding.textView37.setVisibility(View.GONE);
                binding.textViewLegenda.setVisibility(View.VISIBLE);
                binding.textView51.setVisibility(View.VISIBLE);
                binding.listLegenda.setVisibility(View.VISIBLE);
                binding.listLegendaInfo.setVisibility(View.VISIBLE);
                binding.textViewObservacao.setVisibility(View.GONE);

                binding.imageButton5.setVisibility(View.GONE);
                binding.textView37.setVisibility(View.GONE);

                List<ItinerarioPartidaDestino> itinerarios = viewModel.qtdItinerarios;
                dados = new ArrayList<>();

                cores = ctx.getResources().getIntArray(R.array.cores_legenda);

                int cont = 0;

                for(ItinerarioPartidaDestino itinerario : itinerarios){
                    Legenda legenda = new Legenda();
                    legenda.setItinerario(itinerario.getItinerario().getId());

                    if(itinerario.getItinerario().getObservacao() != null && !itinerario.getItinerario().getObservacao().isEmpty()){
                        legenda.setTexto(itinerario.getNomeBairroPartida()+", "+itinerario.getNomeCidadePartida()+" x "
                                +itinerario.getNomeBairroDestino()+", "+itinerario.getNomeCidadeDestino()+" ("
                                +itinerario.getItinerario().getObservacao()+")");
                    } else{
                        legenda.setTexto(itinerario.getNomeBairroPartida()+", "+itinerario.getNomeCidadePartida()+" x "
                                +itinerario.getNomeBairroDestino()+", "+itinerario.getNomeCidadeDestino());
                    }


                    legenda.setCor(cores[cont]);
                    dados.add(legenda);
                    cont++;
                }

                final LegendaAdapter adapter = new LegendaAdapter(dados, ctx);
                adapter.setListener(new LegendaListener() {
                    @Override
                    public void onLegendaSelected(boolean ativa, String itinerario) {

                        if(!ativa){
                            adapterHorarios.filtrarHorarios(itinerario);
                        } else{
                            RecyclerView lista = binding.listLegenda;

                            int registros = lista.getChildCount();

                            for(int i = 0; i < registros; i++){
                                LegendaViewHolder b = (LegendaViewHolder) lista.getChildViewHolder(lista.getChildAt(i));

                                if(!b.ativa){
                                    b.ativa = true;
                                }
                            }

                            adapterHorarios.usarDadosOriginais();
                        }

                    }
                });
                binding.listLegenda.setAdapter(adapter);
                binding.listLegendaInfo.setAdapter(adapter);

//                if(!mapaOculto){
//                    ocultarMapa(null, true);
//                }

            } else{

                if(viewModel.itinerario.getValue() != null && (viewModel.itinerario.getValue().getItinerario().getSigla() != null &&
                         !viewModel.itinerario.getValue().getItinerario().getSigla().isEmpty() &&
                        !viewModel.itinerario.getValue().getItinerario().getSigla().equals("null"))){
                    binding.textView37.setVisibility(View.VISIBLE);
                }

                if(viewModel.itinerario.getValue() != null && (viewModel.itinerario.getValue().getItinerario().getObservacao() != null &&
                        !viewModel.itinerario.getValue().getItinerario().getObservacao().isEmpty() &&
                        !viewModel.itinerario.getValue().getItinerario().getObservacao().equals("null"))){
                    binding.textViewObservacao.setVisibility(View.VISIBLE);
                }

                binding.textViewLegenda.setVisibility(View.GONE);
                binding.listLegenda.setVisibility(View.GONE);
                binding.listLegendaInfo.setVisibility(View.GONE);
                binding.textView51.setVisibility(View.GONE);

                if(viewModel.secoes != null && viewModel.secoes.getValue() != null && viewModel.secoes.getValue().size() > 0){
                    binding.imageButton5.setVisibility(View.VISIBLE);
                } else{
                    binding.imageButton5.setVisibility(View.GONE);
                }


            }

            adapterInfos = new ItinerarioInfoAdapter(viewModel.qtdItinerarios, ctx, cores);
            listInfos.setAdapter(adapterInfos);

            HorarioItinerarioNome horarioItinerarioNome = null;

            if(horarios.size() > 0){
                horarioItinerarioNome = horarios.get(0);
            }

            if(horarioItinerarioNome != null && horarioItinerarioNome.getUltimaAtualizacao() != null){
                DateTime ultimaAtualizacao = new DateTime(horarioItinerarioNome.getUltimaAtualizacao());

                binding.textViewUltimaAtualizacao.setText("Última atualização em "+ DateTimeFormat.forPattern("dd/MM/YYYY").print(ultimaAtualizacao));
            }



            //log
            bundle = new Bundle();

            ParadaBairro partida = viewModel.partida.getValue();
            ParadaBairro destino = viewModel.destino.getValue();

            if(partida != null && destino != null){
                bundle.putString("partida_destino", partida.getParada().getNome()+" - "+partida.getNomeBairroComCidade()+" x "+destino.getParada().getNome()+" - "+destino.getNomeBairroComCidade());
            }

            mFirebaseAnalytics.logEvent("quadro_de_horarios", bundle);

            int posicao = -1;

            for(HorarioItinerarioNome h : horarios){

                if(h.getHorarioItinerario().getHorario().equals(horario)){
                    posicao = horarios.indexOf(h);
                }

            }

            adapterHorarios.setHorario(horario);
            adapterHorarios.setPosicaoAtual(posicao);
            adapterHorarios.horarios = horarios;
            adapterHorarios.legenda = dados;
            adapterHorarios.notifyDataSetChanged();

            contaProcessamento++;

            if(contaProcessamento == 2){
                ocultaModalLoading();
                System.out.println("TEMPO SAIDA HORARIO OBSERVER");
            }

            binding.listHorarios.scheduleLayoutAnimation();

            // scroll lista
            binding.listHorarios.postDelayed(new Runnable() {
                @Override
                public void run() {

                    final int posicao = adapterHorarios.buscaPosicaoHorarioInt(horario);

                    binding.listHorarios.smoothScrollToPosition(posicao+1);
                }
            }, 500);

        }
    };

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            viewModel.atualizaPontoMapa();

            //viewModel.carregaDirections(map, paradas);

            atualizarParadasMapa(paradas);

            List<String> lstItinerarios = PreferenceUtils.carregaItinerariosFavoritos(getApplicationContext());

            List<ParadaBairro> listParadas = viewModel.paradas.getValue();

            if(paradaPartida == null || paradaDestino == null){
                paradaPartida = listParadas.get(0).getParada().getId();
                paradaDestino = listParadas.get(listParadas.size()-1).getParada().getId();

                viewModel.setPartidaEDestino(paradaPartida, paradaDestino);

                viewModel.partida.observe(ctx, partidaObserver);
                viewModel.destino.observe(ctx, destinoObserver);
            }

            if(paradas.size() > 0){
                int i = lstItinerarios.indexOf(paradas.get(0).getIdBairro()+"|"+paradas.get(paradas.size()-1).getIdBairro());

                if(i >= 0){
                    binding.imageButton4.setImageResource(R.drawable.ic_star_white_24dp);
                    flagFavorito = true;
                } else{
                    binding.imageButton4.setImageResource(R.drawable.ic_star_border_white_24dp);
                    flagFavorito = false;
                }
            } else{
                binding.imageButton4.setImageResource(R.drawable.ic_star_border_white_24dp);
                flagFavorito = false;
            }

        }
    };

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {

            if(itinerario != null){
                binding.setItinerario(itinerario);

                if(itinerario.getItinerario().getSigla() == null || itinerario.getItinerario().getSigla().isEmpty() || itinerario.getItinerario().getSigla().equals("null")){
                    binding.textView37.setVisibility(View.GONE);
                } else{
                    binding.textView37.setVisibility(View.VISIBLE);
                }

                if(itinerario.getItinerario().getObservacao() == null || itinerario.getItinerario().getObservacao().isEmpty() || itinerario.getItinerario().getObservacao().equals("null")){
                    binding.textViewObservacao.setVisibility(View.GONE);
                } else{
                    binding.textViewObservacao.setVisibility(View.VISIBLE);
                }

                if(itinerario.getItinerario().getMostraRuas()){
                    mostraRuas = true;
                    viewModel.carregarRuas(itinerario.getItinerario().getId());

                    iniRuas = System.nanoTime();

                    viewModel.ruas.observe(ctx, ruasObserver);
                } else{
                    mostraRuas = false;
                    binding.textViewRuas.setVisibility(View.GONE);
                }

                viewModel.paradas.observe(ctx, paradasObserver);
                viewModel.secoes.observe(ctx, secoesObserver);
                //viewModel.carregarItinerarios(parada.getParada().getId());
                //viewModel.itinerarios.observe(ctx, itinerariosObserver);

                System.out.println("TEMPO ITINERARIO OBSERVER "+contaProcessamento);

                contaProcessamento++;

                if(contaProcessamento == 2){
                    ocultaModalLoading();
                    System.out.println("TEMPO SAIDA ITINERARIO OBSERVER");
                }

            }

        }
    };

    Observer<List<SecaoItinerario>> secoesObserver = new Observer<List<SecaoItinerario>>() {
        @Override
        public void onChanged(List<SecaoItinerario> secoes) {
            adapterSecoes.secoes = secoes;
            adapterSecoes.notifyDataSetChanged();

            if(secoes.size() > 0){
                binding.imageButton5.setVisibility(View.VISIBLE);
            } else{
                binding.imageButton5.setVisibility(View.GONE);
            }

        }
    };

    Observer<Location> localObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location local) {

            if(viewModel.centralizaMapa && local.getLatitude() != 0.0 && local.getLongitude() != 0.0){
                setMapCenter(map, new GeoPoint(local.getLatitude(), local.getLongitude()));
                viewModel.centralizaMapa = false;
            }

        }
    };

    Observer<ParadaBairro> partidaObserver = new Observer<ParadaBairro>() {
        @Override
        public void onChanged(ParadaBairro parada) {

            if(parada != null){
                binding.setPartida(parada);
            }

            Long partidaFin = System.nanoTime();

            System.out.println("TEMPO TOTAL PARTIDA: "+TimeUnit.SECONDS.convert(partidaFin - iniPartida, TimeUnit.NANOSECONDS));

        }
    };

    Observer<ParadaBairro> destinoObserver = new Observer<ParadaBairro>() {
        @Override
        public void onChanged(ParadaBairro parada) {

            if(parada != null){
                binding.setDestino(parada);

//                if(binding.getPartida() != null){
//                    viewModel.setItinerario(itinerario, );
//                }

                Long destinoFin = System.nanoTime();

                System.out.println("TEMPO TOTAL DESTINO: "+TimeUnit.SECONDS.convert(destinoFin - iniDestino, TimeUnit.NANOSECONDS));

            }

        }
    };

    Observer<List<ParadaBairro>> ruasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> ruas) {

            if(ruas.size() > 0 && mostraRuas){
//                binding.btnVerRuas.setVisibility(View.VISIBLE);
                binding.textViewRuas.setVisibility(View.VISIBLE);
                binding.listRuas.setVisibility(View.VISIBLE);
            } else{
//                binding.btnVerRuas.setVisibility(View.GONE);
                binding.textViewRuas.setVisibility(View.GONE);
                binding.listRuas.setVisibility(View.GONE);
            }

            Long finRuas = System.nanoTime();
            Long totRuas = finRuas - iniRuas;

            System.out.println("TEMPO TOTAL IRUAS: "+ TimeUnit.SECONDS.convert(totRuas, TimeUnit.NANOSECONDS));

            adapterRuas.paradas = ruas;
            adapterRuas.notifyDataSetChanged();
        }
    };

    @BindingAdapter("center")
    public static void setMapCenter(MapView map, GeoPoint geoPoint){

        if(geoPoint != null){
            map.getController().setCenter(geoPoint);
        }

    }

    private void atualizarParadasMapa(final List<ParadaBairro> paradas){

        if(paradas != null){

            for(final ParadaBairro p : paradas){

                Marker m = new Marker(map);
                m.setPosition(new GeoPoint(p.getParada().getLatitude(), p.getParada().getLongitude()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                m.setTitle(p.getParada().getNome());
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker_ponto));
                m.setDraggable(false);
                m.setId(p.getParada().getId());
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        ParadaBairro pb = getParadaFromMarker(marker, paradas);

                        InfoWindow infoWindow = new InfoWindow();
                        infoWindow.setParada(pb);
                        infoWindow.setCtx(ctx);
                        infoWindow.show(getSupportFragmentManager(), "infoWindow");
                        mapController.animateTo(marker.getPosition());

                        return true;
                    }
                });
                map.getOverlays().add(m);
            }

        }

    }

    @NonNull
    private ParadaBairro getParadaFromMarker(Marker marker, List<ParadaBairro> paradas) {
        Parada p = new Parada();
        p.setId(marker.getId());

        ParadaBairro parada = new ParadaBairro();
        parada.setParada(p);

        ParadaBairro pb = paradas.get(paradas.indexOf(parada));
        pb.getParada().setLatitude(marker.getPosition().getLatitude());
        pb.getParada().setLongitude(marker.getPosition().getLongitude());
        return pb;
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
        contaProcessamento = 0;
    }

    @Override
    public void onToolbarItemSelected(View v) {
        List<TapTarget> targets = criaTour();
        exibeTour(targets, new TapTargetSequence.Listener(){

            @Override
            public void onSequenceFinish() {
                //Toast.makeText(getApplicationContext(), "Tour finalizado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {
                Toast.makeText(getApplicationContext(), "Tour cancelado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public List<TapTarget> criaTour() {

        List<TapTarget> targets = new ArrayList<>();

        targets.add(DestaqueUtils.geraTapTarget(binding.imageButton4, "Favoritos", "Aqui você pode adicionar ou remover o itinerário dos favoritos!",
                false, true, 1));

        if(binding.imageButton5.getVisibility() == View.VISIBLE){
            targets.add(DestaqueUtils.geraTapTarget(binding.imageButton5, "Seções", "Lista as seções do itinerário e suas respectivas tarifas!",
                    false, true, 2));
        }

        targets.add(DestaqueUtils.geraTapTarget(binding.imageButton6, "Compartilhar", "Aqui você pode compartilhar o quadro de horários com seus contatos!",
                false, true, 3));

//        targets.add(DestaqueUtils.geraTapTarget(binding.listHorarios.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.textViewNome),
//                "Horários", "Aqui você verá o quadro de horários do itinerário escolhido!", false, true, 4));

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
