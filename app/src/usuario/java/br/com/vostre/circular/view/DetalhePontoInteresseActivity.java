package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalheParadaBinding;
import br.com.vostre.circular.databinding.ActivityDetalhePontoInteresseBinding;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseBairro;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioCompactoAdapter;
import br.com.vostre.circular.view.adapter.PontosInteresseAdapter;
import br.com.vostre.circular.view.form.FormMapa;
import br.com.vostre.circular.viewModel.DetalhesParadaViewModel;
import br.com.vostre.circular.viewModel.DetalhesPontoInteresseViewModel;

public class DetalhePontoInteresseActivity extends BaseActivity {

    ActivityDetalhePontoInteresseBinding binding;
    DetalhesPontoInteresseViewModel viewModel;
    ItinerarioCompactoAdapter adapter;

    AppCompatActivity ctx;

    String idPoi;

    Uri link = null;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhe_ponto_interesse);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        super.onCreate(savedInstanceState);
        setTitle("Ponto de Interesse");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        ctx = this;

        link = getIntent().getData();

        viewModel = ViewModelProviders.of(this).get(DetalhesPontoInteresseViewModel.class);

        binding.setViewModel(viewModel);

        if(link != null){

//            String[] parametros = link.toString().split("\\/");
//
//            String uf = parametros[5];
//            String local = parametros[6];
//            String bairro = parametros[7];
//            String slugPoi = parametros[8];
//
//            viewModel.carregaParadaQRCode(uf, local, bairro, slugPoi);
//
//            viewModel.poi.observe(this, poiObserver);
//
//            //log
//            bundle = new Bundle();
//            bundle.putString("parametros", uf+" | "+local+" | "+bairro+" | "+slugPoi);
//            mFirebaseAnalytics.logEvent("consulta_qr_code_poi", bundle);

        } else{
            idPoi = getIntent().getStringExtra("poi");

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            binding.listItinerarios.setLayoutManager(linearLayoutManager);

            adapter = new ItinerarioCompactoAdapter(viewModel.itinerarios.getValue(), this);
            binding.listItinerarios.setAdapter(adapter);

            viewModel.setPontoInteresse(idPoi);

            viewModel.poi.observe(this, poiObserver);
        }

//        listItinerarios = binding.listItinerarios;
//        adapter = new ItinerarioCompactoAdapter(viewModel.itinerarios.getValue(), this);
//        listItinerarios.setAdapter(adapter);
//
//        Location l = new Location(LocationManager.NETWORK_PROVIDER);
//        l.setLatitude(poi.getLatitude());
//        l.setLongitude(poi.getLongitude());
//        viewModel.buscarParadasProximas(ctx, l);
//        viewModel.paradas.observe(ctx, paradasPoiObserver);

//        geraModalLoading();

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
        formMapa.setPontoInteresse(binding.getPoi().getPontoInteresse());
        formMapa.setParada(null);
        formMapa.setCtx(ctx.getApplication());
        formMapa.show(ctx.getSupportFragmentManager(), "formMapa");

        bundle = new Bundle();
        bundle.putString("poi", binding.getPoi().getPontoInteresse().getNome());
        mFirebaseAnalytics.logEvent("consulta_mapa_parada", bundle);
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

    Observer<PontoInteresseBairro> poiObserver = new Observer<PontoInteresseBairro>
            () {
        @Override
        public void onChanged(PontoInteresseBairro poi) {

            if(poi != null){
                binding.setPoi(poi);

                geraModalLoading();

                //log
                bundle = new Bundle();
                bundle.putString("poi", poi.getPontoInteresse().getNome());
                mFirebaseAnalytics.logEvent("consulta_detalhe_poi", bundle);

                if(poi.getPontoInteresse().getImagem() != null){
                    binding.imageView9.setImageDrawable(Drawable.createFromPath(getApplicationContext().getFilesDir()+"/"+poi.getPontoInteresse().getImagem()));
                } else{
                    binding.imageView9.setImageDrawable(getResources().getDrawable(R.drawable.imagem_nao_disponivel_16_9));
                }

                Location location = new Location(LocationManager.NETWORK_PROVIDER);
                location.setLatitude(poi.getPontoInteresse().getLatitude());
                location.setLongitude(poi.getPontoInteresse().getLongitude());

                viewModel.buscarParadasProximas(getApplicationContext(), location);
                viewModel.paradas.observe(ctx, paradasPoiObserver);

                //viewModel.carregarItinerarios(parada.getParada().getId());
                //viewModel.itinerarios.observe(ctx, itinerariosObserver);
            }

        }
    };

    Observer<List<ParadaBairro>> paradasPoiObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {

            List<String> listParadas = new ArrayList<>();

            for(ParadaBairro p : paradas){

                listParadas.add(p.getParada().getId());

                //System.out.println("PARADAS: "+p.getParada().getId()+" | "+p.getParada().getNome()+" - "+p.getNomeBairroComCidade());
            }

            viewModel.listarTodosAtivosProximosPoi(listParadas);
            viewModel.itinerarios.observe(ctx, itinerariosPoiObserver);

        }
    };

    Observer<List<ItinerarioPartidaDestino>> itinerariosPoiObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {

            adapter.itinerarios = itinerarios;

            adapter.notifyDataSetChanged();

            binding.listItinerarios.invalidate();

            binding.listItinerarios.setLayoutManager(new GridLayoutManager(ctx, 2));

            for(ItinerarioPartidaDestino i : itinerarios){
                System.out.println("ITINERARIOS: "+i.getItinerario().getId()+" | "+i.getNomePartida()+", "+i.getNomeBairroPartida()+" - "+i.getNomeDestino()+", "+i.getNomeBairroDestino());
            }

            ocultaModalLoading();

        }
    };

}
