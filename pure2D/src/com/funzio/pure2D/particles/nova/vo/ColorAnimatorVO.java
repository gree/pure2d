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
import com.funzio.pure2D.animators.ColorAnimator;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author juni
 */
public class ColorAnimatorVO extends TweenAnimatorVO {

    public ArrayList<GLColor> from;
    public ArrayList<GLColor> to;

    public ColorAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        from = NovaVO.getListColor(json, "from");
        to = NovaVO.getListColor(json, "to");
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new ColorAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final ColorAnimator color = (ColorAnimator) animator;
        // if (color != null) {
        color.setValues(NovaConfig.getColor(from, emitIndex, GLColor.WHITE), NovaConfig.getColor(to, emitIndex, GLColor.WHITE));

        color.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
        // }
    }
}
