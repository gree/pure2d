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
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

/**
 * @author long
 */
public class NovaParticleVO extends NovaEntryVO {
    public String name;

    // emitting delay and rate
    public int start_delay = 0;
    public int step_delay = Scene.DEFAULT_MSPF;
    public int duration = 0; // <= 0 is unlimited
    public int step_quantity = 1;

    // the containing layer
    public int layer = 0;

    // sprite or clip, optional
    public ArrayList<String> sprite;
    public ArrayList<Integer> start_frame;
    public ArrayList<String> loop_mode;

    // the sprite origin, center by default
    public int origin_x = -1;
    public int origin_y = -1;
    // DisplayObject's properties, optional
    public ArrayList<Integer> x;
    public ArrayList<Integer> y;
    public ArrayList<Float> z;
    public ArrayList<String> animator;
    public ArrayList<String> motion_trail;
    public ArrayList<String> blend_mode;
    public ArrayList<Float> alpha;
    public ArrayList<GLColor> color;
    public ArrayList<Float> rotation;
    public ArrayList<Float> scale_x;
    public ArrayList<Float> scale_y;
    public ArrayList<Float> skew_x;
    public ArrayList<Float> skew_y;

    public NovaParticleVO(final JSONObject json) throws JSONException {
        super(json);

        name = json.optString("name");

        if (json.has("start_delay")) {
            start_delay = json.getInt("start_delay");
        }

        if (json.has("step_delay")) {
            step_delay = json.getInt("step_delay");
        }

        if (json.has("duration")) {
            duration = json.getInt("duration");
        }

        if (json.has("step_quantity")) {
            step_quantity = json.getInt("step_quantity");
        }

        if (json.has("layer")) {
            layer = json.getInt("layer");
        }

        if (json.has("origin_x")) {
            origin_x = json.getInt("origin_x");
        }

        if (json.has("origin_y")) {
            origin_y = json.getInt("origin_y");
        }

        // optional sprite or clip
        sprite = NovaVO.getListString(json, "sprite");
        start_frame = NovaVO.getListInt(json, "start_frame");
        loop_mode = NovaVO.getListString(json, "loop_mode");

        // basic DisplayObject's properties
        x = NovaVO.getListInt(json, "x");
        y = NovaVO.getListInt(json, "y");
        z = NovaVO.getListFloat(json, "z");
        animator = NovaVO.getListString(json, "animator");
        blend_mode = NovaVO.getListString(json, "blend_mode");
        alpha = NovaVO.getListFloat(json, "alpha");
        color = NovaVO.getListColor(json, "color");
        rotation = NovaVO.getListFloat(json, "rotation");
        scale_x = NovaVO.getListFloat(json, "scale_x");
        scale_y = NovaVO.getListFloat(json, "scale_y");
        skew_x = NovaVO.getListFloat(json, "skew_x");
        skew_y = NovaVO.getListFloat(json, "skew_y");
        motion_trail = NovaVO.getListString(json, "motion_trail");
    }

    /**
     * Apply scale to all coordinates and sizes. This is used when you scale the texture.
     * 
     * @param scale
     * @see TextureOptions
     */
    public void applyScale(final float scale) {
        // only scale if origin is not at the center
        if (!hasOriginAtCenter()) {
            origin_x = Math.round(origin_x * scale);
            origin_y = Math.round(origin_y * scale);
        }

        // scale x
        if (x != null) {
            final int size = x.size();
            for (int i = 0; i < size; i++) {
                x.set(i, Math.round(x.get(i) * scale));
            }
        }

        // scale y
        if (y != null) {
            final int size = y.size();
            for (int i = 0; i < size; i++) {
                y.set(i, Math.round(y.get(i) * scale));
            }
        }
    }

    public boolean hasOriginAtCenter() {
        return origin_x == -1 && origin_y == -1;
    }

}
