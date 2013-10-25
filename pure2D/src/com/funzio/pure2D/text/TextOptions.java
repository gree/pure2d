/**
 * 
 */
package com.funzio.pure2D.text;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

/**
 * @author long
 */
public class TextOptions extends TextureOptions {
    private static final String TAG = TextOptions.class.getSimpleName();

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
        TextOptions options = new TextOptions();
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

    public void setXMLAttributes(final XmlPullParser parser, final AssetManager assets) {
        id = parser.getAttributeValue(null, "id"); // required

        final String characters = parser.getAttributeValue(null, "characters");
        if (characters != null) {
            inCharacters = characters;
        }

        final String typeface = parser.getAttributeValue(null, "typeface");
        if (typeface != null) {
            try {
                inTextPaint.setTypeface(Typeface.createFromAsset(assets, typeface));
            } catch (Exception e) {
                Log.e(TAG, "Creating Typeface Error: " + typeface, e);
                // fallback solution
                inTextPaint.setTypeface(Typeface.create(typeface, TextOptions.getTypefaceStyle(parser.getAttributeValue(null, "style"))));
            }
        }

        final String size = parser.getAttributeValue(null, "size");
        if (size != null) {
            inTextPaint.setTextSize(Float.valueOf(size));
        }

        final String color = parser.getAttributeValue(null, "color");
        if (color != null) {
            inTextPaint.setColor(Color.parseColor(color));
        }

        final String paddingX = parser.getAttributeValue(null, "paddingX");
        if (paddingX != null) {
            inPaddingX = Float.valueOf(paddingX);
        }

        final String paddingY = parser.getAttributeValue(null, "paddingY");
        if (paddingY != null) {
            inPaddingY = Float.valueOf(paddingY);
        }

        // stroke
        final String strokeColor = parser.getAttributeValue(null, "strokeColor");
        if (strokeColor != null) {
            inStrokePaint = new TextPaint(inTextPaint);
            inStrokePaint.setColor(Color.parseColor(strokeColor));

            final String strokeSize = parser.getAttributeValue(null, "strokeSize");
            if (strokeSize != null) {
                inStrokePaint.setTextSize(Float.valueOf(strokeSize));
            }
        }

        // shadow
        final String shadowRadius = parser.getAttributeValue(null, "shadowRadius");
        if (shadowRadius != null) {
            float shadowRadiusValue = 5;
            if (shadowRadius != null) {
                shadowRadiusValue = Float.valueOf(shadowRadius);
            }
            final String shadowDx = parser.getAttributeValue(null, "shadowDx");
            float shadowDxValue = 0;
            if (shadowDx != null) {
                shadowDxValue = Float.valueOf(shadowDx);
            }
            final String shadowDy = parser.getAttributeValue(null, "shadowDy");
            float shadowDyValue = 0;
            if (shadowDy != null) {
                shadowDyValue = Float.valueOf(shadowDy);
            }
            final String shadowColor = parser.getAttributeValue(null, "shadowColor");
            int shadowColorValue = 0xFF000000;
            if (shadowColor != null) {
                shadowColorValue = Color.parseColor(shadowColor);
            }

            if (inStrokePaint == null) {
                inStrokePaint = new TextPaint(inTextPaint);
            }

            inStrokePaint.setShadowLayer(shadowRadiusValue, shadowDxValue, shadowDyValue, shadowColorValue);
        }
    }
}
