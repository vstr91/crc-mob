package br.com.vostre.circular.utils;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;

public class DrawableUtils {

    public static Drawable mergeDrawable(Context ctx, int drawable1, int drawable2){

        // 47x68

        Drawable marker = ContextCompat.getDrawable(ctx, drawable1);
        Drawable seta = ContextCompat.getDrawable(ctx, drawable2);

        LayerDrawable finalDrawable = new LayerDrawable(new Drawable[] {marker, seta});
        finalDrawable.setLayerInset(0, 0, 0, 0, 0);
        finalDrawable.setLayerInset(1, 1, 0, 1, 100);

        return finalDrawable;
    }

    public static Drawable convertToGrayscale(Drawable drawable)
    {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

        drawable.setColorFilter(filter);

        return drawable;
    }

}
