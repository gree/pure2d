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
import com.funzio.pure2D.animators.ScaleAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class ScaleAnimatorVO extends TweenAnimatorVO {

    public ArrayList<Float> x_from;
    public ArrayList<Float> x_to;
    public ArrayList<Float> y_from;
    public ArrayList<Float> y_to;

    public ScaleAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        final ArrayList<Float> from = NovaVO.getListFloat(json, "from");
        final ArrayList<Float> to = NovaVO.getListFloat(json, "to");

        x_from = NovaVO.getListFloat(json, "x_from");
        if (x_from == null) {
            x_from = from;
        }
        x_to = NovaVO.getListFloat(json, "x_to");
        if (x_to == null) {
            x_to = to;
        }

        y_from = NovaVO.getListFloat(json, "y_from");
        if (y_from == null) {
            y_from = from;
        }
        y_to = NovaVO.getListFloat(json, "y_to");
        if (y_to == null) {
            y_to = to;
        }
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new ScaleAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final ScaleAnimator scale = (ScaleAnimator) animator;
        // if (scale != null) {
        scale.setValues(NovaConfig.getFloat(x_from, emitIndex, 1), //
                NovaConfig.getFloat(y_from, emitIndex, 1), //
                NovaConfig.getFloat(x_to, emitIndex, 1), //
                NovaConfig.getFloat(y_to, emitIndex, 1));

        scale.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
        // }
    }
}
