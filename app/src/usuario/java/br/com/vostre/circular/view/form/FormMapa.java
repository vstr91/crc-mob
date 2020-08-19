package br.com.vostre.circular.view.form;

import android.Manifest;
import android.app.Application;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormMapaBinding;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Parada;
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
import br.com.vostre.circular.view.utils.InfoWindow;
import br.com.vostre.circular.view.utils.InfoWindowPOI;
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

    int permissionGPS;
    AppCompatActivity act;
    MyLocationNewOverlay mLocationOverlay;

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

        act = (AppCompatActivity) this.getActivity().getParent();

        Dexter.withActivity(this.getActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if(report.areAllPermissionsGranted()){
                    configuraActivity();
                } else{
                    Toast.makeText(ctx, "Acesso ao GPS é necessário para o mapa funcionar corretamente!", Toast.LENGTH_LONG).show();
                    dismiss();
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        })
                .check();

        permissionGPS = ContextCompat.checkSelfPermission(ctx,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionGPS == PackageManager.PERMISSION_GRANTED){
            configuraActivity();
        }

        return binding.getRoot();

    }

    private void configuraActivity(){
        viewModel = ViewModelProviders.of(this.getActivity()).get(DetalhesParadaViewModel.class);

        configuraMapa();
    }

    private void configuraMapa() {
        map = binding.map;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(18d);
//        GeoPoint startPoint = new GeoPoint(-22.470804460339885, -43.82463455200195);
//        mapController.setCenter(startPoint);

        map.setMaxZoomLevel(21d);
        map.setMinZoomLevel(14d);

        mLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(ctx.getApplicationContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        if(parada != null){
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(parada.getParada().getLatitude(), parada.getParada().getLongitude()));
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            m.setIcon(getCtx().getResources().getDrawable(R.drawable.marker));
            m.setTitle(parada.getParada().getNome());
            m.setDraggable(false);
            m.setId(parada.getParada().getId());
            m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {

                    InfoWindow infoWindow = new InfoWindow();
                    infoWindow.setParada(parada);
                    infoWindow.setCtx(act);
                    infoWindow.show(getActivity().getSupportFragmentManager(), "infoWindow");
                    mapController.animateTo(marker.getPosition());

                    return true;
                }
            });
            map.getOverlays().add(m);
        }

        if(pontoInteresse != null){
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(pontoInteresse.getLatitude(), pontoInteresse.getLongitude()));
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            m.setIcon(getCtx().getResources().getDrawable(R.drawable.poi));
            m.setTitle(pontoInteresse.getNome());
            m.setDraggable(false);
            m.setId(pontoInteresse.getId());
            m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {

                    InfoWindowPOI infoWindow = new InfoWindowPOI();
                    infoWindow.setPontoInteresse(pontoInteresse);
                    infoWindow.setCtx(act);
                    infoWindow.show(getActivity().getSupportFragmentManager(), "infoWindowPOI");
                    mapController.animateTo(marker.getPosition());

                    return true;
                }
            });
            map.getOverlays().add(m);
        }

        if(parada != null && pontoInteresse != null){
            Location parada = new Location(LocationManager.GPS_PROVIDER);
            parada.setLatitude(this.parada.getParada().getLatitude());
            parada.setLongitude(this.parada.getParada().getLongitude());

            Location pontoInteresse = new Location(LocationManager.GPS_PROVIDER);
            pontoInteresse.setLatitude(this.pontoInteresse.getLatitude());
            pontoInteresse.setLongitude(this.pontoInteresse.getLongitude());

            Polyline polyline = new Polyline();
            polyline.setColor(Color.parseColor("#000088"));
            polyline.setWidth(2);
            map.getOverlays().add(polyline);

            ArrayList<GeoPoint> pathPoints = new ArrayList<>();

            pathPoints.add(new GeoPoint(parada));
            pathPoints.add(new GeoPoint(pontoInteresse));
            polyline.setPoints(pathPoints);
            map.invalidate();

            //viewModel.carregaDirections(map, this.parada, this.pontoInteresse);
            viewModel.midPoint(parada.getLatitude(), parada.getLongitude(), pontoInteresse.getLatitude(), pontoInteresse.getLongitude());

            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);

            binding.textViewDistancia.setText("~"+nf.format(parada.distanceTo(pontoInteresse))+" m");
            viewModel.localAtual.observe(this, localObserver);
        } else if(parada != null){
            Location parada = new Location(LocationManager.GPS_PROVIDER);
            parada.setLatitude(this.parada.getParada().getLatitude());
            parada.setLongitude(this.parada.getParada().getLongitude());

            viewModel.midPoint(parada.getLatitude(), parada.getLongitude(), parada.getLatitude(), parada.getLongitude());
            viewModel.localAtual.observe(this, localObserver);
            binding.textViewDistancia.setVisibility(View.GONE);
        } else{
            dismiss();
        }


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
