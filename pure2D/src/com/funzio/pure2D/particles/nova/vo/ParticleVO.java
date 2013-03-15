/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Scene;

/**
 * @author long
 */
public class ParticleVO {
    public List<String> sprites;

    public int start_delay = 0;
    public int step_delay = Scene.DEFAULT_MSPF;
    public int duration = 0; // <= 0 is unlimited
    public int step_quantity = 1;

    // the layer
    public int layer = 0;

    // optional
    public String animator;
    public String blend_mode;

    public ParticleVO() {

    }

    public ParticleVO(final JSONObject json) throws JSONException {
        sprites = getSprites(json.optJSONArray("sprites"));

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

        // optional
        animator = json.optString("animator");
        blend_mode = json.optString("blend_mode");
    }

    private static List<String> getSprites(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final List<String> result = new ArrayList<String>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add(array.getString(i));
        }

        return result;
    }
}
