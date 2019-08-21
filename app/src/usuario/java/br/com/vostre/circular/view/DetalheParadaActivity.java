package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalheParadaBinding;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.DestaqueUtils;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.PontosInteresseAdapter;
import br.com.vostre.circular.view.form.FormMapa;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;
import br.com.vostre.circular.viewModel.DetalhesParadaViewModel;

public class DetalheParadaActivity extends BaseActivity {

    ActivityDetalheParadaBinding binding;
    DetalhesParadaViewModel viewModel;
    ItinerarioAdapter adapter;
    PontosInteresseAdapter adapterPois;

    RecyclerView listItinerarios;
    AppCompatActivity ctx;

    BottomSheetDialog bsd;
    RecyclerView listPois;

    boolean flagFavorito = false;
    String idParada;

    Uri link = null;
    LocationManager locationManager;

    Bundle bundle;
    boolean exibindoTour = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhe_parada);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        super.onCreate(savedInstanceState);
        setTitle("Detalhe Parada");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        ctx = this;

        link = getIntent().getData();

        viewModel = ViewModelProviders.of(this).get(DetalhesParadaViewModel.class);

        binding.setViewModel(viewModel);

        if(link != null){

            String[] parametros = link.toString().split("\\/");

            String uf = parametros[5];
            String local = parametros[6];
            String bairro = parametros[7];
            String slugParada = parametros[8];

            viewModel.carregaParadaQRCode(uf, local, bairro, slugParada);

            viewModel.parada.observe(this, paradaObserver);

            //log
            bundle = new Bundle();
            bundle.putString("parametros", uf+" | "+local+" | "+bairro+" | "+slugParada);
            mFirebaseAnalytics.logEvent("consulta_qr_code", bundle);

        } else{
            idParada = getIntent().getStringExtra("parada");

            viewModel.setParada(idParada);

            viewModel.parada.observe(this, paradaObserver);
        }

        listItinerarios = binding.listItinerarios;
        adapter = new ItinerarioAdapter(viewModel.itinerarios.getValue(), this);
        listItinerarios.setAdapter(adapter);

        bsd = new BottomSheetDialog(ctx);
        bsd.setCanceledOnTouchOutside(true);

        bsd.setContentView(R.layout.bottom_sheet_pois);

        listPois = bsd.findViewById(R.id.listPois);
        ImageButton btnFechar = bsd.findViewById(R.id.btnFechar);
        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsd.dismiss();
            }
        });

        binding.textView15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.show();
                //log
                bundle = new Bundle();
                bundle.putString("parada", binding.getUmaParada().getParada().getNome()+", "+binding.getUmaParada().getNomeBairroComCidade());
                mFirebaseAnalytics.logEvent("pois_parada_consultados", bundle);
            }
        });

        viewModel.itinerarios.observe(this, itinerariosObserver);

        geraModalLoading();

        if(link == null){
            checaFavorito();
        }

        binding.textViewLegenda.setVisibility(View.GONE);

    }

    private void checaFavorito() {
        List<String> lstParadas = PreferenceUtils.carregaParadasFavoritas(getApplicationContext());

        int i = lstParadas.indexOf(idParada);

        if(i >= 0){
            binding.imageButton4.setImageResource(R.drawable.ic_star_white_24dp);
            flagFavorito = true;
        } else{
            binding.imageButton4.setImageResource(R.drawable.ic_star_border_white_24dp);
            flagFavorito = false;
        }
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

    public void onClickBtnMapa(View v){
        FormMapa formMapa = new FormMapa();
        formMapa.setParada(binding.getUmaParada());
        formMapa.setPontoInteresse(null);
        formMapa.setCtx(ctx.getApplication());
        formMapa.show(ctx.getSupportFragmentManager(), "formMapa");

        bundle = new Bundle();
        bundle.putString("parada", binding.getUmaParada().getParada().getNome()+", "+binding.getUmaParada().getNomeBairroComCidade());
        mFirebaseAnalytics.logEvent("consulta_mapa_parada", bundle);
    }

    public void onClickBtnFavorito(View v){

        List<String> lstParadas = PreferenceUtils.carregaParadasFavoritas(getApplicationContext());

        if(!flagFavorito){
            SnackbarHelper.notifica(v, "Parada adicionada aos favoritos!", Snackbar.LENGTH_LONG);
            binding.imageButton4.setImageResource(R.drawable.ic_star_white_24dp);
            flagFavorito = true;

            lstParadas.add(idParada);

            //log
            bundle = new Bundle();
            bundle.putString("parada", binding.getUmaParada().getParada().getNome()+", "+binding.getUmaParada().getNomeBairroComCidade());
            mFirebaseAnalytics.logEvent("fav_parada_adicionada", bundle);

        } else{
            SnackbarHelper.notifica(v, "Parada removida dos favoritos!", Snackbar.LENGTH_LONG);
            binding.imageButton4.setImageResource(R.drawable.ic_star_border_white_24dp);
            flagFavorito = false;

            lstParadas.remove(idParada);

            //log
            bundle = new Bundle();
            bundle.putString("parada", binding.getUmaParada().getParada().getNome()+", "+binding.getUmaParada().getNomeBairroComCidade());
            mFirebaseAnalytics.logEvent("fav_parada_removida", bundle);

        }

        PreferenceUtils.gravaParadasFavoritas(lstParadas, getApplicationContext());

    }

    @BindingAdapter("app:textDinheiro")
    public static void setTextDinheiro(TextView view, Double val){

        if(val != null){
            view.setText(NumberFormat.getCurrencyInstance().format(val));
        } else{
            view.setText("-");
        }

    }

    @BindingAdapter("app:textTaxa")
    public static void setTextTaxa(TextView view, Double val){

        if(val != null && val > 0.01){
            view.setText("Taxa de Embarque no valor de "+NumberFormat.getCurrencyInstance().format(val));
        } else{
            view.setText("Não há taxa de embarque");
        }

    }

    @BindingAdapter("app:text")
    public static void setText(TextView view, Double val){

        if(val != null){
            view.setText(NumberFormat.getNumberInstance().format(val));
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
    public static void setText(TextView view, String val){

        if(val != null){
            view.setText(val);
        } else{
            view.setText("-");
        }

    }

    @Override
    public void onGpsChanged(boolean ativo) {
        //Toast.makeText(getApplicationContext(), "GPS Status: "+ativo, Toast.LENGTH_SHORT).show();
    }

    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
//            Collections.sort(itinerarios, new Comparator<ItinerarioPartidaDestino>() {
//                @Override
//                public int compare(ItinerarioPartidaDestino o1, ItinerarioPartidaDestino o2) {
//
////                    if(DateTimeFormat.forPattern("HH:mm").parseLocalTime(o2.getProximoHorario()).isBefore(LocalTime.now())){
////                        return 1;
////                    } else{
////                        return DateTimeFormat.forPattern("HH:mm").parseLocalTime(o1.getProximoHorario())
////                                .compareTo(DateTimeFormat.forPattern("HH:mm").parseLocalTime(o2.getProximoHorario()));
////                    }
//
//                    return DateTimeFormat.forPattern("HH:mm").parseLocalTime(o1.getProximoHorario())
//                            .compareTo(DateTimeFormat.forPattern("HH:mm").parseLocalTime(o2.getProximoHorario()));
//
//                }
//            });

            int contLegenda = 0;

            for(ItinerarioPartidaDestino i : itinerarios){

                if(i.getTempoAcumulado() != null &&
                        (i.getTempoAcumulado().getHourOfDay() > 0 || i.getTempoAcumulado().getMinuteOfHour() > 0)){
                    contLegenda++;
                }

            }

            if(contLegenda == 0){
                binding.textViewLegenda.setVisibility(View.GONE);
            } else{
                binding.textViewLegenda.setVisibility(View.VISIBLE);
            }

            adapter.itinerarios = itinerarios;
            adapter.notifyDataSetChanged();
            ocultaModalLoading();
        }
    };

    Observer<ParadaBairro> paradaObserver = new Observer<ParadaBairro>() {
        @Override
        public void onChanged(ParadaBairro parada) {

            if(parada != null){
                binding.setUmaParada(parada);

                //log
                bundle = new Bundle();
                bundle.putString("parada", parada.getParada().getNome()+" - "+parada.getNomeBairroComCidade());
                mFirebaseAnalytics.logEvent("consulta_detalhe_parada", bundle);

                if(parada.getParada().getImagem() != null){
                    binding.imageView9.setImageDrawable(Drawable.createFromPath(getApplicationContext().getFilesDir()+"/"+parada.getParada().getImagem()));
                } else{
                    binding.imageView9.setImageDrawable(getResources().getDrawable(R.drawable.imagem_nao_disponivel_16_9));
                }

                adapterPois = new PontosInteresseAdapter(viewModel.pois.getValue(), ctx, parada, bsd);
                listPois.setAdapter(adapterPois);

                Location location = new Location(LocationManager.NETWORK_PROVIDER);
                location.setLatitude(parada.getParada().getLatitude());
                location.setLongitude(parada.getParada().getLongitude());

                viewModel.buscaPoisProximos(location);
                viewModel.pois.observe(ctx, poisObserver);

                if(link != null){
                    viewModel.carregarDadosVinculadosQRCode(parada.getParada().getId());
                    viewModel.itinerarios.observe(ctx, itinerariosObserver);
                    idParada = parada.getParada().getId();
                    checaFavorito();
                    geraModalLoading();
                }

                //viewModel.carregarItinerarios(parada.getParada().getId());
                //viewModel.itinerarios.observe(ctx, itinerariosObserver);
            }

        }
    };

    Observer<List<PontoInteresse>> poisObserver = new Observer<List<PontoInteresse>>() {
        @Override
        public void onChanged(List<PontoInteresse> pois) {

            if(pois.size() > 0){
                binding.textView15.setVisibility(View.VISIBLE);
            }

            adapterPois.pois = pois;
            adapterPois.notifyDataSetChanged();
        }
    };

    @Override
    public void onToolbarItemSelected(View v) {
        List<TapTarget> targets = criaTour();
        exibeTour(targets, new TapTargetSequence.Listener(){

            @Override
            public void onSequenceFinish() {
                exibindoTour = false;
                //Toast.makeText(getApplicationContext(), "Tour finalizado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {
                Toast.makeText(getApplicationContext(), "Tour cancelado. Se quiser visualizar novamente, basta pressionar o botão de ajuda no topo da tela", Toast.LENGTH_SHORT).show();
                exibindoTour = false;
            }
        });
        exibindoTour = true;
    }

    @Override
    public List<TapTarget> criaTour() {

        List<TapTarget> targets = new ArrayList<>();

        if(binding.textView15.getVisibility() == View.VISIBLE){
            targets.add(DestaqueUtils.geraTapTarget(binding.textView15, "Pontos de Interesse", "Lista os pontos de interesse, como hospitais, escolas e pontos de encontro próximos à parada!",
                    false, true, 1));
        }

        targets.add(DestaqueUtils.geraTapTarget(binding.imageButton3, "Mapa", "Aqui você pode visualizar no mapa a localização da parada!",
                false, true, 2));
        targets.add(DestaqueUtils.geraTapTarget(binding.imageButton4, "Favoritos", "Aqui você pode adicionar ou remover a parada dos favoritos!",
                false, true, 3));
        targets.add(DestaqueUtils.geraTapTarget(binding.listItinerarios.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.textView23),
                "Próximas Saídas", "Aqui você verá os próximos itinerários que sairão ou passarão pela parada!", false, true, 4));

        return targets;
    }

}
