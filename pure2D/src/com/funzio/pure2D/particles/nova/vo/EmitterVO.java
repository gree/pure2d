/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

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

    // offset position
    public int x = 0;
    public int y = 0;

    // animator for this emitter
    public String animator;
    public String motion_trail;

    // and particles this will emit
    public ArrayList<ParticleVO> particles;

    private HashSet<String> mUsedSprites;

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

        // offset position
        x = json.optInt("x");
        y = json.optInt("y");

        animator = json.optString("animator");
        motion_trail = json.optString("motion_trail");
        particles = getParticles(json.optJSONArray("particles"));
    }

    /**
     * Apply scale to the coordinates and sizes
     * 
     * @param scale
     * @see TextureOptions
     */
    public void applyScale(final float scale) {
        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        // also apply to all particles
        if (particles != null) {
            final int size = particles.size();
            for (int i = 0; i < size; i++) {
                particles.get(i).applyScale(scale);
            }
        }
    }

    private ArrayList<ParticleVO> getParticles(final JSONArray array) throws JSONException {
        final ArrayList<ParticleVO> list = new ArrayList<ParticleVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add(new ParticleVO(array.getJSONObject(i)));
        }

        return list;
    }

    /**
     * @return the set of Sprites being used
     */
    public HashSet<String> getUsedSprites() {
        if (mUsedSprites == null) {
            mUsedSprites = new HashSet<String>();

            // collect from the particles
            for (ParticleVO particleVO : particles) {
                if (particleVO.sprite != null) {
                    for (String sprite : particleVO.sprite) {
                        mUsedSprites.add(sprite);
                    }
                }
            }
        }

        return mUsedSprites;
    }

}
