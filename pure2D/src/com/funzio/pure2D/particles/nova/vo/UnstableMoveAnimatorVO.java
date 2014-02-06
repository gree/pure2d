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
import com.funzio.pure2D.animators.UnstableMoveAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class UnstableMoveAnimatorVO extends TweenAnimatorVO {

    // param set 1
    public ArrayList<Integer> dx;
    public ArrayList<Integer> dy;

    // or param set 2
    public ArrayList<Integer> distance;
    public ArrayList<Integer> degree;

    // wind atts
    public ArrayList<Integer> segment_duration;
    public ArrayList<Float> wind_x1;
    public ArrayList<Float> wind_x2;
    public ArrayList<Float> wind_y1;
    public ArrayList<Float> wind_y2;

    public UnstableMoveAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        dx = NovaVO.getListInt(json, "dx");
        dy = NovaVO.getListInt(json, "dy");
        // or
        distance = NovaVO.getListInt(json, "distance");
        degree = NovaVO.getListInt(json, "degree");

        // wind atts
        segment_duration = NovaVO.getListInt(json, "segment_duration");
        wind_x1 = NovaVO.getListFloat(json, "wind_x1");
        wind_x2 = NovaVO.getListFloat(json, "wind_x2");
        wind_y1 = NovaVO.getListFloat(json, "wind_y1");
        wind_y2 = NovaVO.getListFloat(json, "wind_y2");
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new UnstableMoveAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final UnstableMoveAnimator move = (UnstableMoveAnimator) animator;
        if (distance != null) {
            move.setDistance(NovaConfig.getInt(distance, emitIndex, 0), NovaConfig.getInt(degree, emitIndex, 0));
        } else {
            move.setDelta(NovaConfig.getInt(dx, emitIndex, 0), NovaConfig.getInt(dy, emitIndex, 0));
        }
        move.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
        // wind atts
        move.setSegmentDuration(NovaConfig.getInt(segment_duration, emitIndex, 0));
        move.setWindRange(NovaConfig.getFloat(wind_x1, emitIndex, 0), NovaConfig.getFloat(wind_x2, emitIndex, 0), NovaConfig.getFloat(wind_y1, emitIndex, 0),
                NovaConfig.getFloat(wind_y2, emitIndex, 0));
    }

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

        // scale wind_x1
        if (wind_x1 != null) {
            final int size = wind_x1.size();
            for (int i = 0; i < size; i++) {
                wind_x1.set(i, wind_x1.get(i) * scale);
            }
        }

        // scale wind_x2
        if (wind_x2 != null) {
            final int size = wind_x2.size();
            for (int i = 0; i < size; i++) {
                wind_x2.set(i, wind_x2.get(i) * scale);
            }
        }

        // scale wind_y1
        if (wind_y1 != null) {
            final int size = wind_y1.size();
            for (int i = 0; i < size; i++) {
                wind_y1.set(i, wind_y1.get(i) * scale);
            }
        }

        // scale wind_y2
        if (wind_y2 != null) {
            final int size = wind_y2.size();
            for (int i = 0; i < size; i++) {
                wind_y2.set(i, wind_y2.get(i) * scale);
            }
        }
    }

}
