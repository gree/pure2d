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
import java.util.HashSet;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

/**
 * @author long
 */
public class NovaVO {
    public int version;
    public String name;
    public int pool_size = 0;

    public ArrayList<NovaEmitterVO> emitters;
    public ArrayList<AnimatorVO> animators;
    public ArrayList<MotionTrailVO> motion_trails;

    // for fast look up
    private HashMap<String, NovaEmitterVO> mEmitterMap;
    private HashMap<String, AnimatorVO> mAnimatorMap;
    private HashMap<String, MotionTrailVO> mMotionTrailMap;

    private HashSet<String> mUsedSprites;
    private JSONObject mSource;

    // @JsonCreator
    // public NovaVO( //
    // @JsonProperty("version")//
    // final int version, //
    //
    // @JsonProperty("emitters")//
    // final ArrayList<EmitterVO> emitters, //
    //
    // @JsonProperty("animators")//
    // final ArrayList<AnimatorVO> animators //
    // ) {
    // this.version = version;
    // this.emitters = emitters;
    // this.animators = animators;
    //
    // // make the map
    // mAnimatorMap = new HashMap<String, AnimatorVO>();
    // for (AnimatorVO vo : animators) {
    // mAnimatorMap.put(vo.name, vo);
    // }
    // }

    public NovaVO(final JSONObject json) throws JSONException {
        mSource = json;

        version = json.optInt("version");
        name = json.optString("name");
        pool_size = json.optInt("pool_size");
        emitters = getEmitters(json.optJSONArray("emitters"));
        animators = getAnimators(json.optJSONArray("animators"));
        motion_trails = getMotionTrails(json.optJSONArray("motion_trails"));

        // make the maps
        if (emitters != null) {
            mEmitterMap = new HashMap<String, NovaEmitterVO>();
            for (final NovaEmitterVO vo : emitters) {
                mEmitterMap.put(vo.name, vo);
            }
        }

        if (animators != null) {
            mAnimatorMap = new HashMap<String, AnimatorVO>();
            for (final AnimatorVO vo : animators) {
                if (vo != null) {
                    mAnimatorMap.put(vo.name, vo);
                }
            }
        }

        if (motion_trails != null) {
            mMotionTrailMap = new HashMap<String, MotionTrailVO>();
            for (final MotionTrailVO vo : motion_trails) {
                if (vo != null) {
                    mMotionTrailMap.put(vo.name, vo);
                }
            }
        }
    }

    public NovaVO(final String json) throws JSONException {
        this(new JSONObject(json));
    }

    /**
     * Apply a screen's scale factor to some certain numbers such as x, y, dx, dy. This is used when you scale the texture.
     * 
     * @param scale
     * @see TextureOptions
     */
    public void applyScale(final float scale) {
        // scale emitters
        if (emitters != null) {
            for (final NovaEmitterVO vo : emitters) {
                if (vo != null) {
                    vo.applyScale(scale);
                }
            }
        }

        // scale animators
        if (animators != null) {
            for (final AnimatorVO vo : animators) {
                if (vo != null) {
                    vo.applyScale(scale);
                }
            }
        }

        // scale trails
        if (motion_trails != null) {
            for (final MotionTrailVO vo : motion_trails) {
                if (vo != null) {
                    vo.applyScale(scale);
                }
            }
        }
    }

    public NovaEmitterVO getEmitterVO(final String name) {
        return mEmitterMap != null ? mEmitterMap.get(name) : null;
    }

    public AnimatorVO getAnimatorVO(final String name) {
        return mAnimatorMap != null ? mAnimatorMap.get(name) : null;
    }

    public MotionTrailVO getMotionTrailVO(final String name) {
        return mMotionTrailMap != null ? mMotionTrailMap.get(name) : null;
    }

    protected static ArrayList<NovaEmitterVO> getEmitters(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final ArrayList<NovaEmitterVO> result = new ArrayList<NovaEmitterVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add(new NovaEmitterVO(array.getJSONObject(i)));
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

    protected static ArrayList<MotionTrailVO> getMotionTrails(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final ArrayList<MotionTrailVO> result = new ArrayList<MotionTrailVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add(MotionTrailVO.create(array.getJSONObject(i)));
        }

        return result;
    }

    protected static ArrayList<Integer> getListInt(final JSONObject json, final String field) throws JSONException {
        // field check
        if (!json.has(field)) {
            return null;
        }

        final ArrayList<Integer> result = new ArrayList<Integer>();
        try {
            final JSONArray array = json.getJSONArray(field);
            if (array != null) {
                final int size = array.length();
                for (int i = 0; i < size; i++) {
                    result.add(array.getInt(i));
                }
            }
        } catch (JSONException e) {
            // single value
            result.add(json.getInt(field));
        }

        return result;
    }

    protected static ArrayList<Float> getListFloat(final JSONObject json, final String field) throws JSONException {
        // field check
        if (!json.has(field)) {
            return null;
        }

        final ArrayList<Float> result = new ArrayList<Float>();
        try {
            final JSONArray array = json.getJSONArray(field);
            if (array != null) {
                final int size = array.length();
                for (int i = 0; i < size; i++) {
                    result.add((float) array.getDouble(i));
                }
            }
        } catch (JSONException e) {
            // single value
            result.add((float) json.getDouble(field));
        }

        return result;
    }

    protected static ArrayList<String> getListString(final JSONObject json, final String field) throws JSONException {
        // field check
        if (!json.has(field)) {
            return null;
        }

        final ArrayList<String> result = new ArrayList<String>();
        try {
            final JSONArray array = json.getJSONArray(field);
            if (array != null) {
                final int size = array.length();
                for (int i = 0; i < size; i++) {
                    result.add(array.getString(i));
                }
            }
        } catch (JSONException e) {
            // single value
            result.add(json.optString(field));
        }

        return result;
    }

    protected static ArrayList<GLColor> getListColor(final JSONObject json, final String field) throws JSONException {
        // field check
        if (!json.has(field)) {
            return null;
        }

        final ArrayList<GLColor> result = new ArrayList<GLColor>();
        try {
            final JSONArray array = json.getJSONArray(field);
            if (array != null) {
                final int size = array.length();
                for (int i = 0; i < size; i++) {
                    if (array.get(i) instanceof String) {
                        result.add(new GLColor(Color.parseColor(array.getString(i))));
                    } else {
                        result.add(new GLColor(array.getInt(i)));
                    }
                }
            }
        } catch (JSONException e) {
            // single value
            if (json.get(field) instanceof String) {
                result.add(new GLColor(Color.parseColor(json.getString(field))));
            } else {
                result.add(new GLColor(json.getInt(field)));
            }
        }

        return result;
    }

    /**
     * @return the set of Sprites being used
     */
    public HashSet<String> getUsedSprites() {
        if (mUsedSprites == null) {
            mUsedSprites = new HashSet<String>();

            // collect from the emitters
            for (NovaEmitterVO emitterVO : emitters) {
                mUsedSprites.addAll(emitterVO.getUsedSprites());
            }
        }

        return mUsedSprites;
    }

    /**
     * To save some memory
     */
    public void releaseSource() {
        mSource = null;
    }

    public JSONObject getSource() {
        return mSource;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Name: " + name + ", " //
                + "Version: " + version + ", " //
                + "Pool-Size: " + pool_size + ", " //
                + "Emitters: " + (emitters == null ? 0 : emitters.size()) + ", " //
                + "Animators: " + (animators == null ? 0 : animators.size()) + ", " //
                + "Trails: " + (motion_trails == null ? 0 : motion_trails.size());
    }

}
