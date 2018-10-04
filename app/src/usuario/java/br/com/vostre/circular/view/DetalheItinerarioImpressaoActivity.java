package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
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
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.view.adapter.HorarioItinerarioAdapter;
import br.com.vostre.circular.view.adapter.SecaoItinerarioAdapter;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;

public class DetalheItinerarioImpressaoActivity extends AppCompatActivity {

    ActivityDetalheItinerarioImpressaoBinding binding;
    DetalhesItinerarioViewModel viewModel;

    AppCompatActivity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhe_itinerario_impressao);
        binding.getRoot().setDrawingCacheEnabled(true);
        binding.setLifecycleOwner(this);
        super.onCreate(savedInstanceState);

        ctx = this;

        viewModel = ViewModelProviders.of(this).get(DetalhesItinerarioViewModel.class);

        viewModel.setItinerario(getIntent().getStringExtra("itinerario"));

        viewModel.itinerario.observe(this, itinerarioObserver);


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

            for(HorarioItinerarioNome horario : horarios){
                LinhaHorariosItinerariosCompactoBinding b = DataBindingUtil.inflate(getLayoutInflater(), R.layout.linha_horarios_itinerarios_compacto, binding.linearLayoutHorarios, false);
                b.setHorario(horario);

                if(horario.getHorarioItinerario().getObservacao() == null
                        || horario.getHorarioItinerario().getObservacao() .isEmpty()
                        || horario.getHorarioItinerario().getObservacao().equals(null)){
                    b.textViewObservacao.setVisibility(View.GONE);
                } else{
                    b.textViewObservacao.setVisibility(View.VISIBLE);
                }

                binding.linearLayoutHorarios.addView(b.getRoot());
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
                            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
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

                if(itinerario.getItinerario().getSigla() == null || itinerario.getItinerario().getSigla().isEmpty() || itinerario.getItinerario().getSigla().equals(null)){
                    binding.textView37.setVisibility(View.GONE);
                } else{
                    binding.textView37.setVisibility(View.VISIBLE);
                }

                if(itinerario.getItinerario().getObservacao() == null ||
                        itinerario.getItinerario().getObservacao().isEmpty()
                        || itinerario.getItinerario().getObservacao().equals(null)){
                    binding.textViewObservacao.setVisibility(View.GONE);
                } else{
                    binding.textViewObservacao.setVisibility(View.VISIBLE);
                }

            }

        }
    };

}
