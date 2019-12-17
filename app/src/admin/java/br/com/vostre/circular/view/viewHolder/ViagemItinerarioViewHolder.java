package br.com.vostre.circular.view.viewHolder;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.FolderOverlay;

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

    public ViagemItinerarioViewHolder(LinhaViagensBinding binding, AppCompatActivity context, ParadaListener listener) {
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
        FolderOverlay myOverLay = (FolderOverlay)kmlDocument.mKmlRoot.buildOverlay(binding.map,null,null,kmlDocument);

        kmlDocument.mKmlRoot.mItems.get(0).getBoundingBox().getCenterLatitude();

        binding.map.getOverlays().add(myOverLay );
        binding.map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        binding.map.getController().animateTo(new GeoPoint(kmlDocument.mKmlRoot.mItems.get(0).getBoundingBox().getCenterLatitude(),
                kmlDocument.mKmlRoot.mItems.get(0).getBoundingBox().getCenterLongitude()), 14d, 10L);

        binding.map.invalidate();

    }

}
