/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author long
 */
public class EmitterVO {
    public String name;
    public String type = "rectangle";
    public int width = 1;
    public int height = 1;
    public int quantity = 1;
    public int lifespan = 0; // ms

    // animator for this emitter
    public String animator;

    // and particles this will emit
    public List<ParticleVO> particles;

    public EmitterVO() {
    }

    public EmitterVO(final JSONObject json) throws JSONException {
        name = json.optString("name");

        if (json.has("type")) {
            type = json.getString("type");
        }

        if (json.has("width")) {
            width = json.getInt("width");
        }

        if (json.has("height")) {
            height = json.getInt("height");
        }

        if (json.has("quantity")) {
            quantity = json.getInt("quantity");
        }

        if (json.has("lifespan")) {
            lifespan = json.getInt("lifespan");
        }

        animator = json.optString("animator");
        particles = getParticles(json.optJSONArray("particles"));
    }

    private List<ParticleVO> getParticles(final JSONArray array) throws JSONException {
        final List<ParticleVO> list = new ArrayList<ParticleVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add(new ParticleVO(array.getJSONObject(i)));
        }

        return list;
    }

}
