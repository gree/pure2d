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

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.effects.trails.MotionTrail;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

/**
 * @author long
 */

public abstract class MotionTrailVO {
    public static final String SHAPE = "shape";

    public String name;
    public String type;
    public int num_points;

    public abstract MotionTrail createTrail(int emitIndex, DisplayObject target);

    public MotionTrailVO() {
        // TODO nothing
    }

    public MotionTrailVO(final JSONObject json) throws JSONException {
        name = json.optString("name");
        type = json.optString("type", SHAPE);
        num_points = json.optInt("num_points", 10);
    }

    /**
     * @param target
     * @param trail
     * @return
     */
    final protected MotionTrail init(final int emitIndex, final DisplayObject target, final MotionTrail trail) {
        // MUST: couple with this VO
        trail.setData(this);
        // init and reset
        resetTrail(emitIndex, target, trail);

        return trail;
    }

    /**
     * @param target
     * @param trail
     * @see NovaEmitter#onAnimationUpdate(com.funzio.pure2D.animators.Animator, float), NovaParticle#onAnimationUpdate(com.funzio.pure2D.animators.Animator, float)
     */
    public void resetTrail(final int emitIndex, final DisplayObject target, final MotionTrail trail) {
        trail.reset();
        trail.setNumPoints(num_points);
        trail.setTarget(null); // maybe wait until animation start to set the real target
    }

    /**
     * @param scale
     * @see TextureOptions
     */
    public void applyScale(final float scale) {
        // TODO
    }

    public static MotionTrailVO create(final JSONObject json) throws JSONException {
        if (!json.has("type")) {
            return null;
        }

        final String type = json.getString("type");

        if (type.equalsIgnoreCase(SHAPE)) {
            return new MotionTrailShapeVO(json);
        } else {
            return null;
        }
    }
}
