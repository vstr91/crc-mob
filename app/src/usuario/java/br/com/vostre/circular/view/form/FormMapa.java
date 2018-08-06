package br.com.vostre.circular.view.form;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormBairroBinding;
import br.com.vostre.circular.databinding.FormMapaBinding;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.DetalheParadaActivity;
import br.com.vostre.circular.view.adapter.BairroAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.listener.ItemListener;
import br.com.vostre.circular.view.listener.SelectListener;
import br.com.vostre.circular.viewModel.DetalhesParadaViewModel;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;

public class FormMapa extends FormBase {

    FormMapaBinding binding;

    DetalhesParadaViewModel viewModel;

    MapView map;
    IMapController mapController;

    ParadaBairro parada;
    PontoInteresse pontoInteresse;

    static Application ctx;

    public ParadaBairro getParada() {
        return parada;
    }

    public void setParada(ParadaBairro parada) {
        this.parada = parada;
    }

    public PontoInteresse getPontoInteresse() {
        return pontoInteresse;
    }

    public void setPontoInteresse(PontoInteresse pontoInteresse) {
        this.pontoInteresse = pontoInteresse;
    }

    public Application getCtx() {
        return ctx;
    }

    public void setCtx(Application ctx) {
        this.ctx = ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.form_pais, container, false);
//
//        if(this.getDialog() != null){
//            this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        }
//
//        return view;

        binding = DataBindingUtil.inflate(
                inflater, R.layout.form_mapa, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(DetalhesParadaViewModel.class);

        configuraMapa();

        return binding.getRoot();

    }

    private void configuraMapa() {
        map = binding.map;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(19d);
//        GeoPoint startPoint = new GeoPoint(-22.470804460339885, -43.82463455200195);
//        mapController.setCenter(startPoint);

        map.setMaxZoomLevel(19d);
        map.setMinZoomLevel(16d);

        if(parada != null){
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(parada.getParada().getLatitude(), parada.getParada().getLongitude()));
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            m.setTitle(parada.getParada().getNome());
            m.setDraggable(false);
            m.setId(parada.getParada().getId());
            map.getOverlays().add(m);
        }

        if(pontoInteresse != null){
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(pontoInteresse.getLatitude(), pontoInteresse.getLongitude()));
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            m.setTitle(pontoInteresse.getNome());
            m.setDraggable(false);
            m.setId(pontoInteresse.getId());
            map.getOverlays().add(m);
        }

        Location parada = new Location(LocationManager.NETWORK_PROVIDER);
        parada.setLatitude(this.parada.getParada().getLatitude());
        parada.setLongitude(this.parada.getParada().getLongitude());

        Location pontoInteresse = new Location(LocationManager.NETWORK_PROVIDER);
        pontoInteresse.setLatitude(this.parada.getParada().getLatitude());
        pontoInteresse.setLongitude(this.parada.getParada().getLongitude());

        viewModel.carregaDirections(map, this.parada, this.pontoInteresse);

        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(0);

        binding.textViewDistancia.setText(nf.format(parada.distanceTo(pontoInteresse)));
        viewModel.localAtual.observe(this, localObserver);

    }

    public static void setMapCenter(MapView map, GeoPoint geoPoint){

        if(geoPoint != null){
            map.getController().setCenter(geoPoint);
        }

    }

    Observer<Location> localObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location local) {

            if(local.getLatitude() != 0.0 && local.getLongitude() != 0.0){
                setMapCenter(map, new GeoPoint(local.getLatitude(), local.getLongitude()));
            }

        }
    };

}
