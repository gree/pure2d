/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public List<EmitterVO> emitters;
    public List<AnimatorVO> animators;
    public List<MotionTrailVO> motion_trails;

    // for fast look up
    private Map<String, EmitterVO> mEmitterMap;
    private Map<String, AnimatorVO> mAnimatorMap;
    private Map<String, MotionTrailVO> mMotionTrailMap;

    private Set<String> mUsedSprites;

    // @JsonCreator
    // public NovaVO( //
    // @JsonProperty("version")//
    // final int version, //
    //
    // @JsonProperty("emitters")//
    // final List<EmitterVO> emitters, //
    //
    // @JsonProperty("animators")//
    // final List<AnimatorVO> animators //
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
        version = json.optInt("version");
        name = json.optString("name");
        pool_size = json.optInt("pool_size");
        emitters = getEmitters(json.optJSONArray("emitters"));
        animators = getAnimators(json.optJSONArray("animators"));
        motion_trails = getMotionTrails(json.optJSONArray("motion_trails"));

        // make the maps
        if (emitters != null) {
            mEmitterMap = new HashMap<String, EmitterVO>();
            for (final EmitterVO vo : emitters) {
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
        for (final EmitterVO vo : emitters) {
            if (vo != null) {
                vo.applyScale(scale);
            }
        }

        // scale animators
        for (final AnimatorVO vo : animators) {
            if (vo != null) {
                vo.applyScale(scale);
            }
        }

        // scale trails
        for (final MotionTrailVO vo : motion_trails) {
            if (vo != null) {
                vo.applyScale(scale);
            }
        }
    }

    public EmitterVO getEmitterVO(final String name) {
        return mEmitterMap != null ? mEmitterMap.get(name) : null;
    }

    public AnimatorVO getAnimatorVO(final String name) {
        return mAnimatorMap != null ? mAnimatorMap.get(name) : null;
    }

    public MotionTrailVO getMotionTrailVO(final String name) {
        return mMotionTrailMap != null ? mMotionTrailMap.get(name) : null;
    }

    protected static List<EmitterVO> getEmitters(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final ArrayList<EmitterVO> result = new ArrayList<EmitterVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add(new EmitterVO(array.getJSONObject(i)));
        }

        return result;
    }

    protected static List<AnimatorVO> getAnimators(final JSONArray array) throws JSONException {
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

    protected static List<MotionTrailVO> getMotionTrails(final JSONArray array) throws JSONException {
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

    protected static List<Integer> getListInt(final JSONObject json, final String field) throws JSONException {
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

    protected static List<Float> getListFloat(final JSONObject json, final String field) throws JSONException {
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

    protected static List<String> getListString(final JSONObject json, final String field) throws JSONException {
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

    protected static List<GLColor> getListColor(final JSONObject json, final String field) throws JSONException {
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
                    result.add(new GLColor(array.getInt(i)));
                }
            }
        } catch (JSONException e) {
            // single value
            result.add(new GLColor(json.getInt(field)));
        }

        return result;
    }

    /**
     * @return the set of Sprites being used
     */
    public Set<String> getUsedSprites() {
        if (mUsedSprites == null) {
            mUsedSprites = new HashSet<String>();

            // collect from the emitters
            for (EmitterVO emitterVO : emitters) {
                mUsedSprites.addAll(emitterVO.getUsedSprites());
            }
        }

        return mUsedSprites;
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
