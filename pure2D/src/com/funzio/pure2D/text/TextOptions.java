/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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
    // private static final String TAG = TextOptions.class.getSimpleName();

    public String id;

    public TextPaint inTextPaint = new TextPaint();
    public float inOffsetX = 0;
    public float inOffsetY = 0;
    public float inPaddingX = 0;
    public float inPaddingY = 0;

    // extra
    public String inCharacters;
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
        final TextOptions options = new TextOptions();
        options.set(TextureOptions.getDefault());

        options.inTextPaint.setAntiAlias(true);
        options.inTextPaint.setColor(Color.WHITE);
        options.inTextPaint.setTextSize(32);
        options.inTextPaint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        return options;
    }

    public static int getTypefaceStyle(final String style) {
        if ("bold".equalsIgnoreCase(style)) {
            return Typeface.BOLD;
        } else if ("bold_italic".equalsIgnoreCase(style)) {
            return Typeface.BOLD_ITALIC;
        } else if ("italic".equalsIgnoreCase(style)) {
            return Typeface.ITALIC;
        }

        return Typeface.NORMAL;
    }

}
