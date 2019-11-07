package br.com.vostre.circular.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import java.util.Objects;

import br.com.vostre.circular.R;

public class BadgeDrawerArrowDrawable extends DrawerArrowDrawable {

    // Fraction of the drawable's intrinsic size we want the badge to be.
    private static final float SIZE_FACTOR = .2f;
    private static final float HALF_SIZE_FACTOR = SIZE_FACTOR / 2;

    private Paint backgroundPaint;
    private Paint textPaint;
    private String text;
    private boolean enabled = true;

    public BadgeDrawerArrowDrawable(Context context) {
        super(context);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(context.getResources().getColor(R.color.azul));
        backgroundPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(SIZE_FACTOR * getIntrinsicHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (!enabled) {
            return;
        }

        final Rect bounds = getBounds();
        final float x = (1 - HALF_SIZE_FACTOR) * bounds.width();
        final float y = (1 - HALF_SIZE_FACTOR) * bounds.height();
        canvas.drawCircle(x, y-10, SIZE_FACTOR * bounds.width(), backgroundPaint);

        if (text == null || text.length() == 0) {
            return;
        }

        final Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, x, y + textBounds.height() / 2, textPaint);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            invalidateSelf();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setText(String text) {
        if (!Objects.equals(this.text, text)) {
            this.text = text;
            invalidateSelf();
        }
    }

    public String getText() {
        return text;
    }

    public void setBackgroundColor(int color) {
        if (backgroundPaint.getColor() != color) {
            backgroundPaint.setColor(color);
            invalidateSelf();
        }
    }

    public int getBackgroundColor() {
        return backgroundPaint.getColor();
    }

    public void setTextColor(int color) {
        if (textPaint.getColor() != color) {
            textPaint.setColor(color);
            invalidateSelf();
        }
    }

    public int getTextColor() {
        return textPaint.getColor();
    }
}
