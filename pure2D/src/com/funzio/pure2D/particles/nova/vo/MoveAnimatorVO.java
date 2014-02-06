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
import com.funzio.pure2D.animators.MoveAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class MoveAnimatorVO extends TweenAnimatorVO {

    // param set 1
    public ArrayList<Integer> dx;
    public ArrayList<Integer> dy;

    // or param set 2
    public ArrayList<Integer> distance;
    public ArrayList<Integer> degree;

    public MoveAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        dx = NovaVO.getListInt(json, "dx");
        dy = NovaVO.getListInt(json, "dy");
        // or
        distance = NovaVO.getListInt(json, "distance");
        degree = NovaVO.getListInt(json, "degree");
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new MoveAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final MoveAnimator move = (MoveAnimator) animator;
        // if (move != null) {
        if (distance != null) {
            move.setDistance(NovaConfig.getInt(distance, emitIndex, 0), NovaConfig.getInt(degree, emitIndex, 0));
        } else {
            move.setDelta(NovaConfig.getInt(dx, emitIndex, 0), NovaConfig.getInt(dy, emitIndex, 0));
        }
        move.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
        // }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.AnimatorVO#applyScale(float)
     */
    @Override
    public void applyScale(final float scale) {
        super.applyScale(scale);

        // scale dx
        if (dx != null) {
            final int size = dx.size();
            for (int i = 0; i < size; i++) {
                dx.set(i, Math.round(dx.get(i) * scale));
            }
        }

        // scale dy
        if (dy != null) {
            final int size = dy.size();
            for (int i = 0; i < size; i++) {
                dy.set(i, Math.round(dy.get(i) * scale));
            }
        }

        // scale distance
        if (distance != null) {
            final int size = distance.size();
            for (int i = 0; i < size; i++) {
                distance.set(i, Math.round(distance.get(i) * scale));
            }
        }
    }

}
