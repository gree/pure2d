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

    // the sprite origin, center by default
    public int origin_x = -1;
    public int origin_y = -1;

    // optional
    public ArrayList<String> sprite;
    public ArrayList<Integer> start_frame;
    // offset x, y
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

        // optional
        sprite = NovaVO.getListString(json, "sprite");
        start_frame = NovaVO.getListInt(json, "start_frame");
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
