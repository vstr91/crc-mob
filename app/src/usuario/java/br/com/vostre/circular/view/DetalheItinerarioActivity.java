package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalheItinerarioBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.Legenda;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.view.adapter.LegendaAdapter;
import br.com.vostre.circular.view.adapter.SecaoItinerarioAdapter;
import br.com.vostre.circular.view.listener.LegendaListener;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.view.viewHolder.LegendaViewHolder;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DetalheItinerarioActivity extends BaseActivity {

    ActivityDetalheItinerarioBinding binding;
    DetalhesItinerarioViewModel viewModel;
    HorarioItinerarioAdapter adapterHorarios;
    SecaoItinerarioAdapter adapterSecoes;

    RecyclerView listHorarios;
    RecyclerView listSecoes;
    AppCompatActivity ctx;

    MapView map;
    IMapController mapController;
    MyLocationNewOverlay mLocationOverlay;

    BottomSheetDialog bsd;
    boolean flagFavorito = false;
    RecyclerView listLegenda;

    String horario;
    boolean mapaOculto = false;
    int tamanhoOriginalMapa = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhe_itinerario);
        binding.getRoot().setDrawingCacheEnabled(true);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        super.onCreate(savedInstanceState);
        setTitle("Detalhe Itinerário");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listLegenda = binding.listLegenda;

        ctx = this;

        viewModel = ViewModelProviders.of(this).get(DetalhesItinerarioViewModel.class);

        binding.setViewModel(viewModel);

        viewModel.setItinerario(getIntent().getStringExtra("itinerario"));
        horario = getIntent().getStringExtra("horario");

        viewModel.itinerario.observe(this, itinerarioObserver);

        listHorarios = binding.listHorarios;
        adapterHorarios = new HorarioItinerarioAdapter(viewModel.horarios.getValue(), this);
        listHorarios.setAdapter(adapterHorarios);

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

        configuraMapa();

    }

    private void configuraMapa() {
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

        int preferenciaMapa = PreferenceUtils.carregarPreferenciaInt(ctx, "mapa");

        if(preferenciaMapa == 0){
            tamanhoOriginalMapa = binding.cardViewItinerario.getLayoutParams().height;
            binding.map.setVisibility(View.GONE);
            binding.cardViewItinerario.getLayoutParams().height = WRAP_CONTENT;
            mapaOculto = true;
        }

    }

    public void onClickBtnFavorito(View v){
        List<String> lstItinerarios = PreferenceUtils.carregaItinerariosFavoritos(getApplicationContext());

        List<ParadaBairro> paradas = viewModel.paradas.getValue();

        if(!flagFavorito){
            SnackbarHelper.notifica(v, "Itinerário adicionado aos favoritos!", Snackbar.LENGTH_LONG);
            binding.imageButton4.setImageResource(R.drawable.ic_star_white_24dp);
            flagFavorito = true;

            lstItinerarios.add(paradas.get(0).getIdBairro()+"|"+paradas.get(paradas.size()-1).getIdBairro());

        } else{
            SnackbarHelper.notifica(v, "Itinerário removido dos favoritos!", Snackbar.LENGTH_LONG);
            binding.imageButton4.setImageResource(R.drawable.ic_star_border_white_24dp);
            flagFavorito = false;

            lstItinerarios.remove(paradas.get(0).getIdBairro()+"|"+paradas.get(paradas.size()-1).getIdBairro());

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

    @BindingAdapter("app:textDinheiro")
    public static void setTextDinheiro(TextView view, Double val){

        if(val != null){
            view.setText(NumberFormat.getCurrencyInstance().format(val));
        } else{
            view.setText("-");
        }

    }

    @BindingAdapter("app:textDistancia")
    public static void setTextDistancia(TextView view, Double val){

        if(val != null){
            DecimalFormat format = new DecimalFormat();
            format.setMinimumFractionDigits(1);
            format.setMaximumFractionDigits(1);
            view.setText(format.format(val)+" Km");
        } else{
            view.setText("-");
        }

    }

    @BindingAdapter("app:textData")
    public static void setText(TextView view, DateTime val){

        if(val != null){
            view.setText(DateTimeFormat.forPattern("HH:mm").print(val));
        } else{
            view.setText("-");
        }

    }

    public void ocultarMapa(View v){

        if(mapaOculto){
            binding.map.setVisibility(View.VISIBLE);
            binding.cardViewItinerario.getLayoutParams().height = tamanhoOriginalMapa;
            PreferenceUtils.salvarPreferencia(ctx, "mapa", 1);
            mapaOculto = false;
        } else{
            tamanhoOriginalMapa = binding.cardViewItinerario.getLayoutParams().height;
            binding.map.setVisibility(View.GONE);
            binding.cardViewItinerario.getLayoutParams().height = WRAP_CONTENT;
            PreferenceUtils.salvarPreferencia(ctx, "mapa", 0);
            mapaOculto = true;
        }

    }

    public void btnSecoesClick(View v){
        bsd.show();
    }

    Observer<List<HorarioItinerarioNome>> horariosObserver = new Observer<List<HorarioItinerarioNome>>() {
        @Override
        public void onChanged(List<HorarioItinerarioNome> horarios) {

            List<Legenda> dados = null;

            if(viewModel.qtdItinerarios.size() > 1){
                binding.textView37.setVisibility(View.GONE);
                binding.textViewLegenda.setVisibility(View.VISIBLE);
                binding.listLegenda.setVisibility(View.VISIBLE);

                List<ItinerarioPartidaDestino> itinerarios = viewModel.qtdItinerarios;
                dados = new ArrayList<>();

                int[] cores = ctx.getResources().getIntArray(R.array.cores_legenda);

                int cont = 0;

                for(ItinerarioPartidaDestino itinerario : itinerarios){
                    Legenda legenda = new Legenda();
                    legenda.setItinerario(itinerario.getItinerario().getId());
                    legenda.setTexto(itinerario.getItinerario().getObservacao());
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

            } else{
                binding.textView37.setVisibility(View.VISIBLE);
                binding.textViewLegenda.setVisibility(View.GONE);
                binding.listLegenda.setVisibility(View.GONE);
            }

            adapterHorarios.horarios = horarios;
            adapterHorarios.legenda = dados;
            adapterHorarios.notifyDataSetChanged();

            HorarioItinerarioNome h = adapterHorarios.buscaPosicaoHorario(horario, viewModel.itinerario.getValue().getItinerario().getId());
            int posicao = -1;

            if(h != null){
                posicao = adapterHorarios.horarios.indexOf(h);
            }

            //binding.listHorarios.smoothScrollToPosition(15);
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

            int i = lstItinerarios.indexOf(paradas.get(0).getIdBairro()+"|"+paradas.get(paradas.size()-1).getIdBairro());

            if(i >= 0){
                binding.imageButton4.setImageResource(R.drawable.ic_star_white_24dp);
                flagFavorito = true;
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

                if(itinerario.getItinerario().getSigla() == null || itinerario.getItinerario().getSigla().isEmpty() || itinerario.getItinerario().getSigla().equals(null)){
                    binding.textView37.setVisibility(View.GONE);
                } else{
                    binding.textView37.setVisibility(View.VISIBLE);
                }

                viewModel.paradas.observe(ctx, paradasObserver);
                viewModel.secoes.observe(ctx, secoesObserver);
                //viewModel.carregarItinerarios(parada.getParada().getId());
                //viewModel.itinerarios.observe(ctx, itinerariosObserver);
            }

        }
    };

    Observer<List<SecaoItinerario>> secoesObserver = new Observer<List<SecaoItinerario>>() {
        @Override
        public void onChanged(List<SecaoItinerario> secoes) {
            adapterSecoes.secoes = secoes;
            adapterSecoes.notifyDataSetChanged();
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
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setTitle(p.getParada().getNome());
                m.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.marker));
                m.setDraggable(true);
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

}
