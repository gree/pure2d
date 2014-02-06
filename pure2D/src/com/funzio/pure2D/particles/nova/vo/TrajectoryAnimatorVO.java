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
import com.funzio.pure2D.animators.TrajectoryAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class TrajectoryAnimatorVO extends AnimatorVO {

    public ArrayList<Float> ground;
    public ArrayList<Float> velocity;
    public ArrayList<Float> angle;
    public ArrayList<Float> gravity;

    public TrajectoryAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        ground = NovaVO.getListFloat(json, "ground");
        velocity = NovaVO.getListFloat(json, "velocity");
        angle = NovaVO.getListFloat(json, "angle");
        gravity = NovaVO.getListFloat(json, "gravity");
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new TrajectoryAnimator());
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final TrajectoryAnimator move = (TrajectoryAnimator) animator;
        // if (move != null) {
        move.setGround(NovaConfig.getFloat(ground, emitIndex, 0));
        move.setGravity(NovaConfig.getFloat(gravity, emitIndex, TrajectoryAnimator.DEFAULT_GRAVITY));
        move.setValues(target.getPosition().x, target.getPosition().y, NovaConfig.getFloat(velocity, emitIndex, 0), NovaConfig.getFloat(angle, emitIndex, 0));
        // }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.AnimatorVO#applyScale(float)
     */
    @Override
    public void applyScale(final float scale) {
        super.applyScale(scale);

        // scale velocity
        if (velocity != null) {
            final int size = velocity.size();
            for (int i = 0; i < size; i++) {
                velocity.set(i, velocity.get(i) * scale);
            }
        }
    }

}
