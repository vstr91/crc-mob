package br.com.vostre.circular.view.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Almir on 28/02/2018.
 */

public class CircleView extends View {

    private float mWidth;
    private float mHeight;
    private Paint paint;

    int corArcoMaior;
    int corArcoMenor;
    int espessuraArcoMaior;
    int espessuraArcoMenor;

    Drawable imagem;
    Bitmap b;

    Rect rectImagem;
    RectF rectF;

    String textoMaior;
    String textoMenor;

    public String getTextoMaior() {
        return textoMaior;
    }

    public void setTextoMaior(String textoMaior) {
        this.textoMaior = textoMaior;
    }

    public String getTextoMenor() {
        return textoMenor;
    }

    public void setTextoMenor(String textoMenor) {
        this.textoMenor = textoMenor;
    }

    public int getCorArcoMaior() {
        return corArcoMaior;
    }

    public void setCorArcoMaior(int corArcoMaior) {
        this.corArcoMaior = corArcoMaior;
        invalidate();
    }

    public int getCorArcoMenor() {
        return corArcoMenor;
    }

    public void setCorArcoMenor(int corArcoMenor) {
        this.corArcoMenor = corArcoMenor;
        invalidate();
    }

    public int getEspessuraArcoMaior() {
        return espessuraArcoMaior;
    }

    public void setEspessuraArcoMaior(int espessuraArcoMaior) {
        this.espessuraArcoMaior = espessuraArcoMaior;
        invalidate();
    }

    public int getEspessuraArcoMenor() {
        return espessuraArcoMenor;
    }

    public void setEspessuraArcoMenor(int espessuraArcoMenor) {
        this.espessuraArcoMenor = espessuraArcoMenor;
        invalidate();
    }

    public Drawable getImagem() {
        return imagem;
    }

    public void setImagem(Drawable imagem) {
        this.imagem = imagem;
        invalidate();
    }

    public CircleView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        corArcoMaior = Color.GRAY;
        corArcoMenor = Color.GRAY;
        espessuraArcoMaior = 50;
        espessuraArcoMenor = 5;

        imagem = null;
        textoMaior = null;
        textoMenor = null;

        // checa atributos xml
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    br.com.vostre.circleview.R.styleable.CircleView,
                    0, 0);

            float logicalDensity = context.getResources().getDisplayMetrics().density;

            // valores atributos xml
            corArcoMaior = typedArray.getColor(br.com.vostre.circleview.R.styleable.CircleView_corArcoMaior,
                    corArcoMaior);
            corArcoMenor = typedArray.getColor(br.com.vostre.circleview.R.styleable.CircleView_corArcoMenor,
                    corArcoMenor);
            espessuraArcoMaior = typedArray.getInteger(br.com.vostre.circleview.R.styleable.CircleView_espessuraArcoMaior,
                    espessuraArcoMaior);

            espessuraArcoMaior = (int) (espessuraArcoMaior * logicalDensity + 0.5);

            espessuraArcoMenor = typedArray.getInteger(br.com.vostre.circleview.R.styleable.CircleView_espessuraArcoMenor,
                    espessuraArcoMenor);

            espessuraArcoMenor = (int) (espessuraArcoMenor * logicalDensity + 0.5);

            imagem = typedArray.getDrawable(br.com.vostre.circleview.R.styleable.CircleView_imagem);
            typedArray.recycle();

        }

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(corArcoMenor);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Calculate the radius from the width and height.
        mWidth = w;
        mHeight = h;

        if(imagem != null){
            b = Bitmap.createScaledBitmap(drawableToBitmap(imagem), (int) mWidth, (int) mHeight, true);
            b = getCircularBitmap(b);
        }

        int left = 0;
        int top = 0;
        int right = (int) mWidth;
        int bottom = (int) mHeight;

        rectImagem = new Rect(left, top, right, bottom);

        rectF = new RectF(espessuraArcoMaior, espessuraArcoMaior, mWidth-espessuraArcoMaior, mHeight-espessuraArcoMaior);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(imagem != null){

            b = Bitmap.createScaledBitmap(drawableToBitmap(imagem), (int) mWidth, (int) mHeight, true);
            b = getCircularBitmap(b);

            canvas.drawBitmap(b, rectImagem, rectF, paint);
        }

        // desenhando arco menor
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(espessuraArcoMenor);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(corArcoMenor);

        canvas.drawArc(rectF, -90, 180, false, paint);

        // desenhando arco maior
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(espessuraArcoMaior);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(corArcoMaior);

        canvas.drawArc(rectF, 90, 180, false, paint);

    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

}
