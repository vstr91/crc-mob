package br.com.vostre.circular.view;

import android.Manifest;
import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityParadasSugeridasBinding;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;
import br.com.vostre.circular.utils.DialogUtils;
import br.com.vostre.circular.utils.DrawableUtils;
import br.com.vostre.circular.view.adapter.ParadaSugestaoAdapter;
import br.com.vostre.circular.view.adapter.PontoInteresseSugestaoAdapter;
import br.com.vostre.circular.view.form.FormParada;
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.viewModel.ParadasSugeridasViewModel;
import br.com.vostre.circular.viewModel.ParadasViewModel;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ParadasSugeridasActivity extends BaseActivity {

    ActivityParadasSugeridasBinding binding;

    ParadasSugeridasViewModel viewModel;

    AppCompatActivity ctx;

    ParadaSugestaoAdapter adapterSugestao;
    ParadaSugestaoAdapter adapterAceitas;
    ParadaSugestaoAdapter adapterRejeitadas;

    PontoInteresseSugestaoAdapter adapterSugestaoPois;
    PontoInteresseSugestaoAdapter adapterAceitasPois;
    PontoInteresseSugestaoAdapter adapterRejeitadasPois;

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_paradas_sugeridas);
        super.onCreate(savedInstanceState);

        ctx = this;

        binding.setView(this);
        setTitle("Sugestões");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        viewModel = ViewModelProviders.of(this).get(ParadasSugeridasViewModel.class);
        viewModel.sugeridas.observe(this, paradasSugeridasObserver);
        viewModel.aceitas.observe(this, paradasAceitasObserver);
        viewModel.rejeitadas.observe(this, paradasRejeitadasObserver);

        // POIS
        viewModel.sugeridasPois.observe(this, poisSugeridosObserver);
        viewModel.aceitasPois.observe(this, poisAceitosObserver);
        viewModel.rejeitadasPois.observe(this, poisRejeitadosObserver);

        adapterSugestao = new ParadaSugestaoAdapter(viewModel.sugeridas.getValue(), this);

        binding.listSugestoes.setAdapter(adapterSugestao);

        adapterAceitas = new ParadaSugestaoAdapter(viewModel.aceitas.getValue(), this);

        binding.listAceitas.setAdapter(adapterAceitas);

        adapterRejeitadas = new ParadaSugestaoAdapter(viewModel.rejeitadas.getValue(), this);

        binding.listRejeitadas.setAdapter(adapterRejeitadas);

        // POIS

        adapterSugestaoPois = new PontoInteresseSugestaoAdapter(viewModel.sugeridasPois.getValue(), this);

        binding.listSugestoesPois.setAdapter(adapterSugestaoPois);

        adapterAceitasPois = new PontoInteresseSugestaoAdapter(viewModel.aceitasPois.getValue(), this);

        binding.listAceitasPois.setAdapter(adapterAceitasPois);

        adapterRejeitadasPois = new PontoInteresseSugestaoAdapter(viewModel.rejeitadasPois.getValue(), this);

        binding.listRejeitadasPois.setAdapter(adapterRejeitadasPois);

        tabHost = binding.tabs;
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Pendentes");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Pendentes");
        tabHost.addTab(spec);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Aceitas");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Aceitas");
        tabHost.addTab(spec2);

        TabHost.TabSpec spec3 = tabHost.newTabSpec("Rejeitadas");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Rejeitadas");
        tabHost.addTab(spec3);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean retorno = super.onCreateOptionsMenu(menu);

        if(menu != null){
            menu.getItem(0).setVisible(false);
        }

        return retorno;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    Observer<List<ParadaSugestaoBairro>> paradasSugeridasObserver = new Observer<List<ParadaSugestaoBairro>>() {
        @Override
        public void onChanged(List<ParadaSugestaoBairro> paradas) {
            adapterSugestao.paradas = paradas;
            adapterSugestao.notifyDataSetChanged();

            if(paradas.size() > 0){
                binding.textViewParadaPendenteVazia.setVisibility(View.GONE);
            } else{
                binding.textViewParadaPendenteVazia.setVisibility(View.VISIBLE);
            }

        }
    };

    Observer<List<ParadaSugestaoBairro>> paradasAceitasObserver = new Observer<List<ParadaSugestaoBairro>>() {
        @Override
        public void onChanged(List<ParadaSugestaoBairro> paradas) {
            adapterAceitas.paradas = paradas;
            adapterAceitas.notifyDataSetChanged();

            if(paradas.size() > 0){
                binding.textViewParadaAceitaVazia.setVisibility(View.GONE);
            } else{
                binding.textViewParadaAceitaVazia.setVisibility(View.VISIBLE);
            }

        }
    };

    Observer<List<ParadaSugestaoBairro>> paradasRejeitadasObserver = new Observer<List<ParadaSugestaoBairro>>() {
        @Override
        public void onChanged(List<ParadaSugestaoBairro> paradas) {
            adapterRejeitadas.paradas = paradas;
            adapterRejeitadas.notifyDataSetChanged();

            if(paradas.size() > 0){
                binding.textViewParadaRejeitadaVazia.setVisibility(View.GONE);
            } else{
                binding.textViewParadaRejeitadaVazia.setVisibility(View.VISIBLE);
            }

        }
    };

    // POIS

    Observer<List<PontoInteresseSugestaoBairro>> poisSugeridosObserver = new Observer<List<PontoInteresseSugestaoBairro>>() {
        @Override
        public void onChanged(List<PontoInteresseSugestaoBairro> pois) {
            adapterSugestaoPois.pois = pois;
            adapterSugestaoPois.notifyDataSetChanged();

            if(pois.size() > 0){
                binding.textViewPoiPendenteVazia.setVisibility(View.GONE);
            } else{
                binding.textViewPoiPendenteVazia.setVisibility(View.VISIBLE);
            }

        }
    };

    Observer<List<PontoInteresseSugestaoBairro>> poisAceitosObserver = new Observer<List<PontoInteresseSugestaoBairro>>() {
        @Override
        public void onChanged(List<PontoInteresseSugestaoBairro> pois) {
            adapterAceitasPois.pois = pois;
            adapterAceitasPois.notifyDataSetChanged();

            if(pois.size() > 0){
                binding.textViewPoiAceitaVazia.setVisibility(View.GONE);
            } else{
                binding.textViewPoiAceitaVazia.setVisibility(View.VISIBLE);
            }

        }
    };

    Observer<List<PontoInteresseSugestaoBairro>> poisRejeitadosObserver = new Observer<List<PontoInteresseSugestaoBairro>>() {
        @Override
        public void onChanged(List<PontoInteresseSugestaoBairro> pois) {
            adapterRejeitadasPois.pois = pois;
            adapterRejeitadasPois.notifyDataSetChanged();

            if(pois.size() > 0){
                binding.textViewPoiRejeitadoVazia.setVisibility(View.GONE);
            } else{
                binding.textViewPoiRejeitadoVazia.setVisibility(View.VISIBLE);
            }

        }
    };

}
