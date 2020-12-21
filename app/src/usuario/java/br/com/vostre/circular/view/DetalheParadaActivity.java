package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.common.util.CrashUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.CrashlyticsRegistrar;
import com.jama.carouselview.CarouselView;
import com.jama.carouselview.CarouselViewListener;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalheParadaBinding;
import br.com.vostre.circular.databinding.LinhaCarrosselBinding;
import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.log.LogParada;
import br.com.vostre.circular.model.log.TiposLog;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.DataHoraUtils;
import br.com.vostre.circular.utils.DestaqueUtils;
import br.com.vostre.circular.utils.LogConsultaUtils;
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

    static int PICK_FILE = 173;

    CarouselView carouselView;
    LogParada log;

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

        log = LogConsultaUtils.iniciaLogParada(TiposLog.PARADA_DETALHE.name(), ctx);

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

//            viewModel.setParada(idParada);
//
//            viewModel.parada.observe(this, paradaObserver);
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

        log.setParada(idParada);

        viewModel.itinerarios.observe(this, itinerariosObserver);

        geraModalLoading();

        if(link == null){
            checaFavorito();
        }

        binding.textViewLegenda.setVisibility(View.GONE);
        binding.textViewFeriado.setVisibility(View.GONE);

        viewModel.checaFeriado(Calendar.getInstance());
        viewModel.isFeriado.observe(this, feriadoObserver);
        viewModel.retorno.observe(this, retornoObserver);

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

    public void onClickBtnStreetView(View v){
        // STREET VIEW

        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+viewModel.parada.getValue().getParada().getLatitude()+","
                +viewModel.parada.getValue().getParada().getLongitude());

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);

        // STREET VIEW
    }

    public void onClickBtnFoto(View v){
        CropImage.activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Escolher Foto")
                .setAspectRatio(16,9)
                .setFixAspectRatio(true)
                .start(this);

//        Intent intentFile = new Intent();
//        intentFile.setType("text/*");
//        intentFile.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intentFile, "Escolha o arquivo de dados"), PICK_FILE);

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
            view.setText("Taxa de Embarque: "+NumberFormat.getCurrencyInstance().format(val));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                Bitmap bmp = BitmapFactory.decodeFile(resultUri.getPath());

                ImagemParada imagemParada = new ImagemParada();
                imagemParada.setParada(viewModel.parada.getValue().getParada().getId());

                viewModel.salvarFoto(imagemParada, bmp);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Houve um problema ao processar a foto. Por favor tente novamente.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    Observer<Boolean> feriadoObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean isFeriado) {

            if(idParada != null && isFeriado != null){

                //viewModel.itinerarios.observe(ctx, itinerariosObserver);

                viewModel.setParada(idParada, isFeriado);

                viewModel.parada.observe(ctx, paradaObserver);

                if(isFeriado){
                    binding.textViewFeriado.setVisibility(View.VISIBLE);
                } else{
                    binding.textViewFeriado.setVisibility(View.GONE);
                }

            }

        }

    };

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
            binding.listItinerarios.scheduleLayoutAnimation();

            LogConsultaUtils.finalizaLog(ctx, log);
        }
    };

    Observer<ParadaBairro> paradaObserver = new Observer<ParadaBairro>() {
        @Override
        public void onChanged(ParadaBairro parada) {

            if(parada != null){
                binding.setUmaParada(parada);

                viewModel.getImagensParada(parada.getParada().getId());
                viewModel.imagensParada.observe(ctx, imagensParadaObserver);

                //log
                bundle = new Bundle();
                bundle.putString("parada", parada.getParada().getNome()+" - "+parada.getNomeBairroComCidade());
                mFirebaseAnalytics.logEvent("consulta_detalhe_parada", bundle);

//                if(parada.getParada().getImagem() != null){
//                    binding.imageView9.setImageDrawable(Drawable.createFromPath(getApplicationContext().getFilesDir()+"/"+parada.getParada().getImagem()));
//                } else{
//                    binding.imageView9..getsetImageDrawable(getResources().getDrawable(R.drawable.imagem_nao_disponivel_16_9));
//                }

                if(parada.getParada().getRua() != null && !parada.getParada().getRua().isEmpty()){
                    binding.textViewRua.setVisibility(View.VISIBLE);
                    binding.linearLayout2.invalidate();
                } else{
                    binding.textViewRua.setVisibility(View.GONE);
                    binding.linearLayout2.invalidate();
                }

                if(parada.getParada().getSentido() > -1){
                    binding.textViewSentidoParada.setText(parada.getParada().getSentidoTexto());
                    binding.textView88.setVisibility(View.VISIBLE);
                    binding.textViewSentidoParada.setVisibility(View.VISIBLE);

                    switch(parada.getParada().getSentido()){
                        case 0:
                            binding.textViewSentidoParada.setTextColor(ctx.getResources().getColor(R.color.azul));
                            break;
                        case 1:
                            binding.textViewSentidoParada.setTextColor(ctx.getResources().getColor(R.color.ciano));
                            break;
                    }

                } else{
                    binding.textViewSentidoParada.setText("");
                    binding.textView88.setVisibility(View.GONE);
                    binding.textViewSentidoParada.setVisibility(View.GONE);
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
                } else{
                    //viewModel.itinerarios.observe(ctx, itinerariosObserver);
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

    Observer<List<ImagemParada>> imagensParadaObserver = new Observer<List<ImagemParada>>() {
        @Override
        public void onChanged(List<ImagemParada> imagens) {

            if(carouselView == null){
                int totalImagens = imagens.size();

                if (totalImagens == 0) {
                    ImagemParada ip = new ImagemParada();

                    ParadaBairro pb = viewModel.parada.getValue();

                    if(pb != null){
                        ip.setImagem(pb.getParada().getImagem());
                        ip.setDescricao("-1");
                    }

                    imagens.add(ip);

                    totalImagens++;
                }

                carouselView = binding.imageView9;

                carouselView.setSize(totalImagens);
                carouselView.setResource(R.layout.linha_carrossel);
                carouselView.setAutoPlay(true);
                carouselView.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
                carouselView.setCarouselOffset(OffsetType.CENTER);
                carouselView.setAutoPlayDelay(5000);
                carouselView.setIndicatorPadding(60);
                carouselView.hideIndicator(false);
                carouselView.setIndicatorRadius(8);
                carouselView.setCarouselViewListener(new CarouselViewListener() {
                    @Override
                    public void onBindView(View view, int position) {
                        // Example here is setting up a full image carousel
                        ImageView imageViewFoto = view.findViewById(R.id.imageViewFoto);

                        ImagemParada ip = viewModel.imagensParada.getValue().get(position);

                        if (ip.getImagem() == null || ip.getImagem().isEmpty()) {
                            imageViewFoto.setImageResource(R.drawable.imagem_nao_disponivel_16_9);
                        } else {
                            imageViewFoto.setImageDrawable(Drawable.createFromPath(getApplicationContext().getFilesDir() + "/"
                                    + ip.getImagem()));
                        }

                        TextView textViewCredito = view.findViewById(R.id.textViewCredito);

                        if(ip.getDescricao() != null && !ip.getDescricao().isEmpty() && !ip.getDescricao().equals("-1")){
                            textViewCredito.setText("Foto por "+ip.getDescricao());
                            textViewCredito.setVisibility(View.VISIBLE);
                        } else if(ip.getDescricao() != null && ip.getDescricao().equals("-1")){
                            textViewCredito.setVisibility(View.GONE);
                        } else{
                            textViewCredito.setText("Foto por usuário anônimo");
                            textViewCredito.setVisibility(View.VISIBLE);
                        }

                    }
                });
                // After you finish setting up, show the CarouselView
                carouselView.show();
            }

        }

    };

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getApplicationContext(), "Sugestão de imagem da Parada cadastrada! Obrigado!", Toast.LENGTH_SHORT).show();
            } else if(retorno == 0){
                Toast.makeText(getApplicationContext(),
                        "Houve um problema ao salvar a imagem. Por favor, tente novamente.",
                        Toast.LENGTH_SHORT).show();
            }

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
        targets.add(DestaqueUtils.geraTapTarget(binding.imageButton5, "Ver no Street View", "Aqui você pode ver o entorno da parada no Google Street View!",
                false, true, 4));
        targets.add(DestaqueUtils.geraTapTarget(binding.imageButton6, "Enviar Foto", "Aqui você pode sugerir novas fotos do ponto de parada!",
                false, true, 5));
        targets.add(DestaqueUtils.geraTapTarget(binding.listItinerarios.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.textView23),
                "Próximas Saídas", "Aqui você verá os próximos itinerários que sairão ou passarão pela parada!", false, true, 5));

        return targets;
    }

}
