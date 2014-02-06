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
package com.funzio.pure2D.ui.vo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.text.TextOptions;
import com.funzio.pure2D.ui.UIManager;

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

    public int texture_size;

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

        texture_size = json.optInt("texture_size", 512);
    }

    public TextOptions createTextOptions(final UIManager manager) {
        final TextOptions options = TextOptions.getDefault();

        options.id = name;
        options.inCharacters = manager.evalString(characters);

        try {
            options.inTextPaint.setTypeface(Typeface.createFromAsset(manager.getContext().getAssets(), manager.evalString(typeface)));
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
