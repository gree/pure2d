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
package com.funzio.pure2D.ui.vo;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.particles.nova.vo.AnimatorVO;

/**
 * @author long.ngo
 */
public class UIConfigVO {

    public float screen_scale = 1;

    public TextureManagerVO texture_manager;
    public ArrayList<FontVO> fonts;
    public ArrayList<AnimatorVO> animators;

    protected HashMap<String, AnimatorVO> mAnimatorMap;

    public UIConfigVO(final JSONObject json) throws JSONException {
        texture_manager = new TextureManagerVO(json.getJSONObject("texture_manager"));
        fonts = getFonts(json.getJSONArray("fonts"));
        animators = getAnimators(json.optJSONArray("animators"));

        // mapping
        if (animators != null) {
            mAnimatorMap = new HashMap<String, AnimatorVO>();
            for (final AnimatorVO vo : animators) {
                if (vo != null) {
                    mAnimatorMap.put(vo.name, vo);
                }
            }
        }

    }

    /**
     * Apply a screen's scale factor to some certain numbers such as x, y, dx, dy. This is used when you scale the texture.
     * 
     * @param factor
     * @see TextureOptions
     */
    public void applyScale(final float factor) {
        screen_scale *= factor;

        // scale animators
        if (fonts != null) {
            for (final FontVO vo : fonts) {
                if (vo != null) {
                    vo.applyScale(factor);
                }
            }
        }

        // scale animators
        if (animators != null) {
            for (final AnimatorVO vo : animators) {
                if (vo != null) {
                    vo.applyScale(factor);
                }
            }
        }

    }

    protected static ArrayList<FontVO> getFonts(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final ArrayList<FontVO> result = new ArrayList<FontVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            FontVO vo = new FontVO(array.getJSONObject(i));
            result.add(vo);
        }

        return result;
    }

    protected static ArrayList<AnimatorVO> getAnimators(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final ArrayList<AnimatorVO> result = new ArrayList<AnimatorVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add(AnimatorVO.create(array.getJSONObject(i)));
        }

        return result;
    }

    public AnimatorVO getAnimatorVO(final String name) {
        return mAnimatorMap != null ? mAnimatorMap.get(name) : null;
    }
}
