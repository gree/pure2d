/**
 * 
 */
package com.funzio.pure2D.ui.vo;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.text.TextOptions;

/**
 * @author long.ngo
 */
public class FontVO {
    public String name;

    public String style;
    public String characters;

    public float size;
    public String typeface;
    public String color;
    public float padding_x;
    public float padding_y;

    public float shadow_radius;
    public String shadow_color;
    public float shadow_dx;
    public float shadow_dy;

    public float stroke_size;
    public String stroke_color;

    public FontVO(final JSONObject json) throws JSONException {
        name = json.getString("name");

        style = json.optString("style");
        characters = json.optString("characters");

        size = (float) json.optDouble("size", 10);
        typeface = json.optString("typeface");
        color = json.optString("color", "#FFFFFFFF");
        padding_x = (float) json.optDouble("padding_x", 0);
        padding_y = (float) json.optDouble("padding_y", 0);

        shadow_radius = (float) json.optDouble("shadow_radius", 0);
        shadow_color = json.optString("shadow_color", "#FF000000");
        shadow_dx = (float) json.optDouble("shadow_dx", 0);
        shadow_dy = (float) json.optDouble("shadow_dy", 0);

        stroke_size = (float) json.optDouble("stroke_size", 0);
        stroke_color = json.optString("stroke_color", "");
    }

    public TextOptions createTextOptions(final AssetManager assets) {
        final TextOptions options = TextOptions.getDefault();

        options.id = name;
        options.inCharacters = characters;

        try {
            options.inTextPaint.setTypeface(Typeface.createFromAsset(assets, typeface));
        } catch (Exception e) {
            // Log.e(TAG, "Creating Typeface Error: " + typeface, e);
            // fallback solution
            options.inTextPaint.setTypeface(Typeface.create(typeface, TextOptions.getTypefaceStyle(style)));
        }

        options.inTextPaint.setTextSize(Float.valueOf(size));
        options.inTextPaint.setColor(Color.parseColor(color));

        options.inPaddingX = padding_x;
        options.inPaddingY = padding_y;

        // stroke
        if (stroke_size > 0) {
            options.inStrokePaint = new TextPaint(options.inTextPaint);
            options.inStrokePaint.setColor(Color.parseColor(stroke_color));
            options.inStrokePaint.setTextSize(stroke_size);
        }

        // shadow
        if (shadow_radius > 0) {
            if (options.inStrokePaint == null) {
                options.inStrokePaint = new TextPaint(options.inTextPaint);
            }
            options.inStrokePaint.setShadowLayer(shadow_radius, shadow_dx, shadow_dy, Color.parseColor(shadow_color));
        }

        return options;
    }

    public void applyScale(final float scale) {

        size *= scale;
        padding_x *= scale;
        padding_y *= scale;

        shadow_radius *= scale;
        shadow_dx *= scale;
        shadow_dy *= scale;

        stroke_size *= scale;
    }

}
