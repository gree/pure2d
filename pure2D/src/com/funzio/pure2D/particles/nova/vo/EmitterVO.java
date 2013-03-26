/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // offset position
    public int x = 0;
    public int y = 0;

    // animator for this emitter
    public String animator;

    // and particles this will emit
    public List<ParticleVO> particles;

    private Set<String> mUsedSprites;

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
        particles = getParticles(json.optJSONArray("particles"));
    }

    public void applyScale(final float scale) {
        // TODO
    }

    private List<ParticleVO> getParticles(final JSONArray array) throws JSONException {
        final List<ParticleVO> list = new ArrayList<ParticleVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add(new ParticleVO(array.getJSONObject(i)));
        }

        return list;
    }

    /**
     * @return the set of Sprites being used
     */
    public Set<String> getUsedSprites() {
        if (mUsedSprites == null) {
            mUsedSprites = new HashSet<String>();

            // collect from the particles
            for (ParticleVO particleVO : particles) {
                for (String sprite : particleVO.sprite) {
                    mUsedSprites.add(sprite);
                }
            }
        }

        return mUsedSprites;
    }

}
