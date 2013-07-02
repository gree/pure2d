/**
 * 
 */
package com.funzio.pure2D.text;

import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * @author long
 */
public class BitmapFontMetrics extends FontMetrics {

    // extended metrics
    public float whitespace = 0;
    public float letterSpacing = 0;
    public float letterPaddingX = 0;
    public float letterPaddingY = 0;

    // private stuff
    private TextOptions mTextOptions;
    private Rect mTempRect;

    public BitmapFontMetrics(final TextOptions options) {
        super();

        setTextOptions(options);
    }

    protected void setTextOptions(final TextOptions options) {
        mTextOptions = options;
        options.inTextPaint.getFontMetrics(this);

        // apply scale
        top *= options.inScaleY;
        ascent *= options.inScaleY;
        descent *= options.inScaleY;
        bottom *= options.inScaleY;
        leading *= options.inScaleY;

        letterPaddingX = options.inPaddingX * options.inScaleX;
        letterPaddingY = options.inPaddingY * options.inScaleY;
        letterSpacing *= options.inScaleX;
        whitespace = options.inTextPaint.measureText(String.valueOf(Characters.SPACE)) * options.inScaleX + letterPaddingX * 2;
    }

    public void getTextBounds(final String text, final RectF textBounds) {
        final int length = text.length();
        float baseline = 0;
        int start = 0, lineLength;
        int end = text.indexOf(Characters.NEW_LINE);
        if (end < 0) {
            end = length - 1;
        } else {
            end -= 1; // don't count the new-line
        }
        lineLength = end - start + 1;

        // init rects
        if (mTempRect == null) {
            mTempRect = new Rect();
        }
        textBounds.setEmpty();

        // multi lines
        do {
            mTextOptions.inTextPaint.getTextBounds(text, start, start + lineLength, mTempRect);
            // apply scale
            mTempRect.left *= mTextOptions.inScaleX;
            mTempRect.right *= mTextOptions.inScaleX;
            mTempRect.top *= mTextOptions.inScaleY;
            mTempRect.bottom *= mTextOptions.inScaleY;
            // inflate by padding * lineChars
            mTempRect.inset(-Math.round(letterPaddingX * 2 * lineLength + letterSpacing * (lineLength - 1) * 0.5f), -Math.round(letterPaddingY * 2));
            mTempRect.offset(0, Math.round(baseline));
            // merge to bounds
            textBounds.union(mTempRect.left, mTempRect.top, mTempRect.right, mTempRect.bottom);

            start = end + 2; // also skip newline
            end = text.indexOf(Characters.NEW_LINE, start);
            if (end < 0) {
                end = length - 1;
            } else {
                end -= 1; // don't count the new-line
            }
            lineLength = end - start + 1;
            baseline += bottom - top;
        } while (start < length);
    }
}
