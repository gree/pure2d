/**
 * 
 */
package com.funzio.pure2D.text;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

/**
 * @author long
 */
public class TextOptions extends TextureOptions {
    public TextPaint inTextPaint = new TextPaint();
    public float inOffsetX = 0;
    public float inOffsetY = 0;
    public float inPaddingX = 0;
    public float inPaddingY = 0;

    // extra
    public Bitmap inBackground = null;
    public Paint inStrokePaint;

    /**
     * Use {@link #getDefault()} to create a default instance
     */
    protected TextOptions() {
        super();
    }

    /**
     * @return a new instance with default configuration
     */
    public static TextOptions getDefault() {
        TextOptions options = new TextOptions();
        options.set(TextureOptions.getDefault());

        options.inTextPaint.setAntiAlias(true);
        options.inTextPaint.setColor(Color.WHITE);
        options.inTextPaint.setTextSize(32);
        options.inTextPaint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        return options;
    }
}
