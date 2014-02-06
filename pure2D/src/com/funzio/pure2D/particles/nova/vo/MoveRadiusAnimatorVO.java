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
import com.funzio.pure2D.animators.MoveRadiusAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
@Deprecated
public class MoveRadiusAnimatorVO extends TweenAnimatorVO {

    public ArrayList<Integer> distance;
    public ArrayList<Integer> degree;

    public MoveRadiusAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        distance = NovaVO.getListInt(json, "distance");
        degree = NovaVO.getListInt(json, "degree");
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new MoveRadiusAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final MoveRadiusAnimator move = (MoveRadiusAnimator) animator;
        // if (move != null) {
        move.setValues(NovaConfig.getInt(distance, emitIndex, 0), NovaConfig.getInt(degree, emitIndex, 0));
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

        // scale distance
        if (distance != null) {
            final int size = distance.size();
            for (int i = 0; i < size; i++) {
                distance.set(i, Math.round(distance.get(i) * scale));
            }
        }
    }

}
