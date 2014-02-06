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

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class RotateAnimatorVO extends TweenAnimatorVO {
    public ArrayList<Float> from;
    public ArrayList<Float> to;
    public ArrayList<Float> delta;
    public ArrayList<Integer> pivot_x;
    public ArrayList<Integer> pivot_y;
    public ArrayList<Integer> radius;

    public RotateAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        from = NovaVO.getListFloat(json, "from");
        to = NovaVO.getListFloat(json, "to");
        delta = NovaVO.getListFloat(json, "delta");

        pivot_x = NovaVO.getListInt(json, "pivot_x");
        pivot_y = NovaVO.getListInt(json, "pivot_y");
        radius = NovaVO.getListInt(json, "radius");
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new RotateAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final RotateAnimator rotate = (RotateAnimator) animator;
        if (delta != null) {
            rotate.setDelta(NovaConfig.getFloat(delta, emitIndex, 0));
        } else {
            rotate.setValues(NovaConfig.getFloat(from, emitIndex, 0), NovaConfig.getFloat(to, emitIndex, 0));
        }

        // check pivot
        if (pivot_x != null && pivot_y != null) {
            // PointF offset;
            // if (target instanceof NovaParticle) {
            // // relative to the emitter's position
            // offset = ((NovaParticle) target).getEmitter().getPosition();
            // } else {
            // // relative to the current target
            // offset = target.getPosition();
            // }
            // rotate.setPivot(NovaConfig.getInt(pivot_x, emitIndex, 0) + offset.x, NovaConfig.getInt(pivot_y, emitIndex, 0) + offset.y, NovaConfig.getInt(radius, emitIndex, 0));
            rotate.setPivot(NovaConfig.getInt(pivot_x, emitIndex, 0), NovaConfig.getInt(pivot_y, emitIndex, 0), NovaConfig.getInt(radius, emitIndex, 0));

        } else {
            rotate.clearPivot();
        }

        rotate.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
    }
}
