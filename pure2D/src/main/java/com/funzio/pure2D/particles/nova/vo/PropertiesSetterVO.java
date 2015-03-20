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
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.PropertiesSetter;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendModes;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class PropertiesSetterVO extends AnimatorVO {

    public ArrayList<Float> x;
    public ArrayList<Float> y;
    public ArrayList<Float> z;
    public ArrayList<Float> origin_x;
    public ArrayList<Float> origin_y;
    public ArrayList<Float> scale_x;
    public ArrayList<Float> scale_y;
    public ArrayList<Float> rotation;
    public ArrayList<Float> alpha;
    public ArrayList<GLColor> color;
    public ArrayList<String> blend_mode;

    public ArrayList<Integer> duration;

    private HashMap<String, Object> mProperties;

    public PropertiesSetterVO(final JSONObject json) throws JSONException {
        super(json);

        x = NovaVO.getListFloat(json, "x");
        y = NovaVO.getListFloat(json, "y");
        z = NovaVO.getListFloat(json, "z");
        origin_x = NovaVO.getListFloat(json, "origin_x");
        origin_y = NovaVO.getListFloat(json, "origin_y");
        scale_x = NovaVO.getListFloat(json, "scale_x");
        scale_y = NovaVO.getListFloat(json, "scale_y");
        rotation = NovaVO.getListFloat(json, "rotation");
        alpha = NovaVO.getListFloat(json, "alpha");
        color = NovaVO.getListColor(json, "color");
        blend_mode = NovaVO.getListString(json, "blend_mode");

        duration = NovaVO.getListInt(json, "duration");

        mProperties = new HashMap<String, Object>();
    }

    /**
     * Apply scale to all coordinates and sizes. This is used when you scale the texture.
     * 
     * @param scale
     * @see TextureOptions
     */
    @Override
    public void applyScale(final float scale) {
        // only scale if origin is not at the center
        if (origin_x != null) {
            final int size = origin_x.size();
            for (int i = 0; i < size; i++) {
                if (origin_x.get(i) != -1) {
                    origin_x.set(i, origin_x.get(i) * scale);
                }
            }
        }

        if (origin_y != null) {
            final int size = origin_y.size();
            for (int i = 0; i < size; i++) {
                if (origin_y.get(i) != -1) {
                    origin_y.set(i, origin_y.get(i) * scale);
                }
            }
        }

        // scale x
        if (x != null) {
            final int size = x.size();
            for (int i = 0; i < size; i++) {
                x.set(i, x.get(i) * scale);
            }
        }

        // scale y
        if (y != null) {
            final int size = y.size();
            for (int i = 0; i < size; i++) {
                y.set(i, y.get(i) * scale);
            }
        }
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new PropertiesSetter());
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        if (x != null) {
            mProperties.put(PropertiesSetter.X, NovaConfig.getFloat(x, emitIndex, 0));
        }
        if (y != null) {
            mProperties.put(PropertiesSetter.Y, NovaConfig.getFloat(y, emitIndex, 0));
        }
        if (z != null) {
            mProperties.put(PropertiesSetter.Z, NovaConfig.getFloat(z, emitIndex, 0));
        }

        if (origin_x != null) {
            mProperties.put(PropertiesSetter.ORIGIN_X, NovaConfig.getFloat(origin_x, emitIndex, 0));
        }
        if (origin_y != null) {
            mProperties.put(PropertiesSetter.ORIGIN_Y, NovaConfig.getFloat(origin_y, emitIndex, 0));
        }

        if (scale_x != null) {
            mProperties.put(PropertiesSetter.SCALE_X, NovaConfig.getFloat(scale_x, emitIndex, 1));
        }
        if (scale_y != null) {
            mProperties.put(PropertiesSetter.SCALE_Y, NovaConfig.getFloat(scale_y, emitIndex, 1));
        }
        if (rotation != null) {
            mProperties.put(PropertiesSetter.ROTATION, NovaConfig.getFloat(rotation, emitIndex, 0));
        }

        if (alpha != null) {
            mProperties.put(PropertiesSetter.ALPHA, NovaConfig.getFloat(alpha, emitIndex, 1));
        }
        if (color != null) {
            mProperties.put(PropertiesSetter.COLOR, NovaConfig.getColor(color, emitIndex, null));
        }
        if (blend_mode != null) {
            mProperties.put(PropertiesSetter.BLEND_MODE, BlendModes.getBlendFunc(NovaConfig.getString(blend_mode, emitIndex)));
        }

        final PropertiesSetter setter = (PropertiesSetter) animator;
        setter.setProperties(mProperties);
        setter.setLifespan(NovaConfig.getInt(duration, emitIndex, 1));
    }
}
