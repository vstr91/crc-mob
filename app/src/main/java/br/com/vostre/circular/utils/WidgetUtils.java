package br.com.vostre.circular.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Itinerario;

public class WidgetUtils {

    public static void formataTabs(TabWidget tw, Context ctx, int textSize, int color){

        for (int i = 0; i < tw.getChildCount(); ++i){
            final View tabView = tw.getChildTabViewAt(i);
            final TextView tv = (TextView) tabView.findViewById(android.R.id.title);

            if(textSize > 0){
                tv.setTextSize(textSize);
            }

            if(color > 0){
                tv.setTextColor(ctx.getResources().getColor(color));
            }

        }

    }

    public static void desenhaTrajetoMapa(Itinerario itinerario, MapView map, int largura, int cor){

        List<GeoPoint> pontos = PolylineEncoder.decode(itinerario.getTrajeto(), 10, false);

        Polyline line = new Polyline();
        line.setPoints(pontos);
        line.getOutlinePaint().setStrokeWidth(largura);
        line.getOutlinePaint().setAlpha(255);
        line.getOutlinePaint().setColor(cor);

        line.getOutlinePaint().setStrokeJoin(Paint.Join.ROUND);
        line.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);

        map.getOverlayManager().add(line);

        map.invalidate();
//        map.getController().zoomToSpan(BoundingBox.fromGeoPoints(line.getPoints()).getLatitudeSpan(),
//                BoundingBox.fromGeoPoints(line.getPoints()).getLongitudeSpanWithDateLine());
    }

}
