/**
 * 
 */
package com.funzio.pure2D.text;

import android.graphics.Paint.FontMetrics;

/**
 * @author long
 */
public class BitmapFontMetrics extends FontMetrics {

    public float whitespace = 0;

    public float letterSpacing = 0;

    public BitmapFontMetrics(final FontMetrics metrics) {
        super();

        setFontMetrics(metrics);
    }

    public void setFontMetrics(final FontMetrics metrics) {
        top = metrics.top;
        ascent = metrics.ascent;
        descent = metrics.descent;
        bottom = metrics.bottom;
        leading = metrics.leading;

        // implicitly set whitespace
        whitespace = -ascent;// * 0.5f;
    }

    public void applyScale(final float sx, final float sy) {
        top *= sy;
        ascent *= sy;
        descent *= sy;
        bottom *= sy;
        leading *= sy;

        whitespace *= sx;
    }
}
