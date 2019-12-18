package br.com.vostre.circular.view.viewHolder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlLineString;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.kml.KmlPoint;
import org.osmdroid.bonuspack.kml.KmlPolygon;
import org.osmdroid.bonuspack.kml.KmlTrack;
import org.osmdroid.bonuspack.kml.Style;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.databinding.LinhaViagensBinding;
import br.com.vostre.circular.listener.ParadaListener;
import br.com.vostre.circular.model.ViagemItinerario;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.ViagensActivity;
import br.com.vostre.circular.view.form.FormParada;

public class ViagemItinerarioViewHolder extends RecyclerView.ViewHolder {

    private final LinhaViagensBinding binding;
    AppCompatActivity ctx;

    public ViagemItinerarioViewHolder(LinhaViagensBinding binding, AppCompatActivity context) {
        super(binding.getRoot());
        this.binding = binding;
        this.ctx = context;
    }

    public void bind(final ViagemItinerario viagem) {
        binding.setViagem(viagem);
        binding.setContext(ctx);

        addAdditionalLayer(ctx, viagem);

        binding.executePendingBindings();
    }

    private void addAdditionalLayer (Context ctx, ViagemItinerario viagem) {
        String jsonString = null;
        try {
            InputStream jsonStream = new FileInputStream(new File(ctx.getFilesDir(), viagem.getTrajeto()));
            int size = jsonStream.available();
            byte[] buffer = new byte[size];
            jsonStream.read(buffer);
            jsonStream.close();
            jsonString = new String(buffer,"UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        KmlDocument kmlDocument = new KmlDocument();
        kmlDocument.parseGeoJSON(jsonString);

        //kmlDocument.mKmlRoot.mItems.get(0).getBoundingBox().getCenterLatitude();

        if(kmlDocument.mKmlRoot.mItems.size() > 0){

//            mLineStyle.getOutlinePaint().setStrokeJoin(Paint.Join.ROUND);

            FolderOverlay myOverLay = (FolderOverlay)kmlDocument.mKmlRoot.buildOverlay(binding.map, null, new KmlFeature.Styler() {
                @Override
                public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

                }

                @Override
                public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {

                }

                @Override
                public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {
                    polyline.getOutlinePaint().setStrokeJoin(Paint.Join.ROUND);
                    polyline.getPaint().setStrokeCap(Paint.Cap.ROUND);
                    polyline.getOutlinePaint().setColor(Color.BLUE);
                    polyline.getOutlinePaint().setStrokeWidth(5);
                }

                @Override
                public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {
                    polygon.getOutlinePaint().setStrokeJoin(Paint.Join.ROUND);
                }

                @Override
                public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack) {

                }
            }, kmlDocument);

            binding.map.getOverlays().add(myOverLay);
            binding.map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

            binding.map.getController().animateTo(new GeoPoint(kmlDocument.mKmlRoot.mItems.get(0).getBoundingBox().getCenterLatitude(),
                    kmlDocument.mKmlRoot.mItems.get(0).getBoundingBox().getCenterLongitude()), 10d, 10L);

            binding.map.invalidate();
        } else{
            binding.map.setVisibility(View.GONE);
        }



    }

}
