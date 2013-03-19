/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Scene;

/**
 * @author long
 */
public class ParticleVO {
    public int start_delay = 0;
    public int step_delay = Scene.DEFAULT_MSPF;
    public int duration = 0; // <= 0 is unlimited
    public int step_quantity = 1;

    // the containing layer
    public int layer = 0;

    // the sprite origin
    public int origin_x = -1;
    public int origin_y = -1;

    // optional
    public List<String> sprite;
    public List<Integer> start_frame;
    // offset x, y
    public List<Integer> x;
    public List<Integer> y;

    public List<String> animator;
    public List<String> blend_mode;

    public ParticleVO() {

    }

    public ParticleVO(final JSONObject json) throws JSONException {
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
        animator = NovaVO.getListString(json, "animator");
        blend_mode = NovaVO.getListString(json, "blend_mode");
    }

}
