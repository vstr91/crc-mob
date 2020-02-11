package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.glxn.qrgen.android.QRCode;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalheItinerarioBinding;
import br.com.vostre.circular.databinding.ActivityDetalheItinerarioImpressaoBinding;
import br.com.vostre.circular.databinding.LinhaHorariosItinerariosBinding;
import br.com.vostre.circular.databinding.LinhaHorariosItinerariosCompactoBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.Legenda;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.SecaoItinerarioParada;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.view.adapter.LegendaAdapter;
import br.com.vostre.circular.view.adapter.LegendaCompartilhamentoAdapter;
import br.com.vostre.circular.view.adapter.SecaoItinerarioAdapter;
import br.com.vostre.circular.view.adapter.SecaoItinerarioDetalhadoAdapter;
import br.com.vostre.circular.view.listener.LegendaListener;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.view.viewHolder.LegendaViewHolder;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;

public class DetalheItinerarioImpressaoActivity extends AppCompatActivity {

    ActivityDetalheItinerarioImpressaoBinding binding;
    DetalhesItinerarioViewModel viewModel;

    AppCompatActivity ctx;

    String itinerarioPartida;
    String itinerarioDestino;

    String paradaPartidaOriginal;
    String paradaDestinoOriginal;

    String paradaPartida;
    String paradaDestino;

    boolean imprimePorPartidaEDestino = true;

    boolean trechoIsolado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhe_itinerario_impressao);
        binding.getRoot().setDrawingCacheEnabled(true);
        binding.setLifecycleOwner(this);
        super.onCreate(savedInstanceState);

        ctx = this;

        viewModel = ViewModelProviders.of(this).get(DetalhesItinerarioViewModel.class);

        itinerarioPartida = getIntent().getStringExtra("itinerarioPartida");
        itinerarioDestino = getIntent().getStringExtra("itinerarioDestino");

        paradaPartida = getIntent().getStringExtra("paradaPartida");
        paradaDestino = getIntent().getStringExtra("paradaDestino");

        // nova busca por trecho
        trechoIsolado = getIntent().getBooleanExtra("trechoIsolado", false);
        viewModel.trechoIsolado = trechoIsolado;

        viewModel.partidaConsulta = getIntent().getStringExtra("partidaConsulta");
        viewModel.destinoConsulta = getIntent().getStringExtra("destinoConsulta");

        imprimePorPartidaEDestino = getIntent().getBooleanExtra("imprimePorPartidaEDestino", true);

        if(!imprimePorPartidaEDestino){
            paradaPartidaOriginal = paradaPartida;
            paradaPartida = null;
            paradaDestinoOriginal = paradaDestino;
            paradaDestino = null;

            viewModel.setItinerario(getIntent().getStringExtra("itinerario"), paradaPartida, paradaDestino, itinerarioPartida, itinerarioDestino);

            viewModel.itinerario.observe(this, itinerarioObserver);

            viewModel.setPartidaEDestino(paradaPartidaOriginal, paradaDestinoOriginal);

        } else{
            viewModel.setItinerario(getIntent().getStringExtra("itinerario"), paradaPartida, paradaDestino, itinerarioPartida, itinerarioDestino);

            viewModel.itinerario.observe(this, itinerarioObserver);
            binding.textViewObs.setVisibility(View.GONE);
        }

        viewModel.partida.observe(this, partidaObserver);
        viewModel.destino.observe(this, destinoObserver);




        viewModel.horarios.observe(this, horariosObserver);

        Bitmap myBitmap = QRCode.from("https://play.google.com/store/apps/details?id=br.com.vostre.circular").bitmap();
        binding.imageViewQR.setImageBitmap(myBitmap);

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

    Observer<List<HorarioItinerarioNome>> horariosObserver = new Observer<List<HorarioItinerarioNome>>() {
        @Override
        public void onChanged(List<HorarioItinerarioNome> horarios) {

            List<Legenda> dados = null;

            if(viewModel.qtdItinerarios.size() > 1){
                binding.listLegenda.setVisibility(View.VISIBLE);

                List<ItinerarioPartidaDestino> itinerarios = viewModel.qtdItinerarios;
                dados = new ArrayList<>();

                int[] cores = ctx.getResources().getIntArray(R.array.cores_legenda);

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

                final LegendaCompartilhamentoAdapter adapter = new LegendaCompartilhamentoAdapter(dados, ctx);
                adapter.setListener(new LegendaListener() {
                    @Override
                    public void onLegendaSelected(boolean ativa, String itinerario) {
                        //vazio
                    }
                });
                binding.listLegenda.setAdapter(adapter);
                binding.textViewObs.setVisibility(View.GONE);

            } else{
                binding.listLegenda.setVisibility(View.GONE);
            }

            int cont = 1;
            LinearLayout ll = new LinearLayout(ctx);
            ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            ll.setOrientation(LinearLayout.VERTICAL);

            for(HorarioItinerarioNome horario : horarios){
                LinhaHorariosItinerariosCompactoBinding b = DataBindingUtil.inflate(getLayoutInflater(), R.layout.linha_horarios_itinerarios_compacto, binding.linearLayoutHorarios, false);
                b.setHorario(horario);

                if(dados != null && dados.size() > 0){
                    Legenda l = new Legenda();
                    l.setItinerario(horario.getHorarioItinerario().getItinerario());
                    l = dados.get(dados.indexOf(l));
                    b.imageViewCor.setBackgroundColor(l.getCor());
                    b.imageViewCor.setVisibility(View.VISIBLE);
                } else{
                    b.imageViewCor.setVisibility(View.GONE);
                }

                if(horario.getHorarioItinerario().getObservacao() == null
                        || horario.getHorarioItinerario().getObservacao() .isEmpty()
                        || horario.getHorarioItinerario().getObservacao().equals(null)){
                    b.textViewObservacao.setVisibility(View.GONE);
                } else{
                    b.textViewObservacao.setVisibility(View.VISIBLE);
                }

                if((cont > 1 && cont % 15 == 0) || cont == horarios.size()){
                    ll.addView(b.getRoot());
                    binding.linearLayoutHorarios.addView(ll);

                    ll = new LinearLayout(ctx);
                    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    ll.setOrientation(LinearLayout.VERTICAL);
                } else{
                    ll.addView(b.getRoot());
                }

                cont++;

                //binding.linearLayoutHorarios.addView(b.getRoot());
            }

            if(horarios.size() > 0){

                final View layout = binding.getRoot();

                binding.textViewData.setText("Quadro de horários gerado no Vostrè Circular " +
                        "em "+DateTimeFormat.forPattern("dd/MM/YYYY HH:mm").print(DateTime.now()));

                ViewTreeObserver vto = layout.getViewTreeObserver();
                vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        try {
                            File file = new File(getApplication().getExternalCacheDir()+"/quadro-horarios.jpg");
//                            getScreenViewBitmap(binding.getRoot()).compress(Bitmap.CompressFormat.JPEG, 100,
//                                    new FileOutputStream(new File(getApplication().getFilesDir()+"/teste.jpg")));
                            getBitmapFromView(binding.scrollView, binding.scrollView.getChildAt(0).getHeight(),
                                    binding.scrollView.getChildAt(0).getWidth())
                                    .compress(Bitmap.CompressFormat.JPEG, 70,
                                    new FileOutputStream(file));
                            Intent share = new Intent(Intent.ACTION_SEND);

                            share.setType("image/jpeg");
                            share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(ctx, "br.com.vostre.circular.fileprovider", file));
                            startActivity(Intent.createChooser(share, "Compartilhar Quadro de Horários"));
                            finish();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }

        }
    };

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    Observer<ItinerarioPartidaDestino> itinerarioObserver = new Observer<ItinerarioPartidaDestino>() {
        @Override
        public void onChanged(ItinerarioPartidaDestino itinerario) {

            if(itinerario != null){
                binding.setItinerario(itinerario);

                if(itinerario.getItinerario().getObservacao() == null || itinerario.getItinerario().getObservacao().isEmpty()){
                    binding.textViewObs.setVisibility(View.GONE);
                } else{
                    binding.textViewObs.setVisibility(View.VISIBLE);
                }

                //viewModel.carregaSecoesPorItinerario(itinerario.getItinerario().getId());
                //viewModel.secoesComNome.observe(ctx, secoesObserver);

            }

        }
    };

    Observer<ParadaBairro> partidaObserver = new Observer<ParadaBairro>() {
        @Override
        public void onChanged(ParadaBairro parada) {

            if(parada != null){
                binding.setPartida(parada);
            }

        }
    };

    Observer<ParadaBairro> destinoObserver = new Observer<ParadaBairro>() {
        @Override
        public void onChanged(ParadaBairro parada) {

            if(parada != null){
                binding.setDestino(parada);
            }

        }
    };

    Observer<List<SecaoItinerarioParada>> secoesObserver = new Observer<List<SecaoItinerarioParada>>() {
        @Override
        public void onChanged(List<SecaoItinerarioParada> secoes) {

            final SecaoItinerarioDetalhadoAdapter adapter = new SecaoItinerarioDetalhadoAdapter(secoes, ctx);
            binding.listSecoes.setAdapter(adapter);
        }
    };

    public int dpToPx(int dp) {
        float density = ctx.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

}
