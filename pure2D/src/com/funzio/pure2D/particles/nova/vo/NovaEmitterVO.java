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
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

/**
 * @author long
 */
public class NovaEmitterVO extends NovaEntryVO {
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
    public ArrayList<NovaParticleVO> particles;

    private HashSet<String> mUsedSprites;

    public NovaEmitterVO(final JSONObject json) throws JSONException {
        super(json);

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

    private ArrayList<NovaParticleVO> getParticles(final JSONArray array) throws JSONException {
        final ArrayList<NovaParticleVO> list = new ArrayList<NovaParticleVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add(new NovaParticleVO(array.getJSONObject(i)));
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
            for (NovaParticleVO particleVO : particles) {
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
