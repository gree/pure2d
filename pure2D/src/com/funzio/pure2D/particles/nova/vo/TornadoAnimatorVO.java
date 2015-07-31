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
import com.funzio.pure2D.animators.TornadoAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class TornadoAnimatorVO extends TweenAnimatorVO {

    public ArrayList<Integer> dx;
    public ArrayList<Integer> dy;
    public ArrayList<Integer> circle_radius;
    public ArrayList<Integer> circle_num;
    public ArrayList<String> circle_interpolation;
    public ArrayList<Float> circle_multiplier;
    public ArrayList<Float> circle_ratio;
    public boolean z_enabled;

    public TornadoAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        dx = NovaVO.getListInt(json, "dx");
        dy = NovaVO.getListInt(json, "dy");
        circle_radius = NovaVO.getListInt(json, "circle_radius");
        circle_num = NovaVO.getListInt(json, "circle_num");
        circle_interpolation = NovaVO.getListString(json, "circle_interpolation");
        circle_multiplier = NovaVO.getListFloat(json, "circle_multiplier");
        circle_ratio = NovaVO.getListFloat(json, "circle_ratio");

        z_enabled = json.optInt("z_enabled", 0) > 0;
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new TornadoAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final TornadoAnimator tornado = (TornadoAnimator) animator;
        // if (tornado != null) {
        tornado.setDelta(NovaConfig.getInt(dx, emitIndex, 0), NovaConfig.getInt(dy, emitIndex, 0));
        tornado.setCircles(NovaConfig.getInt(circle_radius, emitIndex, 0), NovaConfig.getInt(circle_num, emitIndex, 0),
                NovaConfig.getFloat(circle_ratio, emitIndex, TornadoAnimator.DEFAULT_CIRCLE_RATIO), NovaConfig.getInterpolator(NovaConfig.getString(circle_interpolation, emitIndex)));
        tornado.setCircleMultiplier(NovaConfig.getFloat(circle_multiplier, emitIndex, 1));
        tornado.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
        tornado.setZEnabled(z_enabled);
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

        // scale radius
        if (circle_radius != null) {
            final int size = circle_radius.size();
            for (int i = 0; i < size; i++) {
                circle_radius.set(i, Math.round(circle_radius.get(i) * scale));
            }
        }
    }
}
