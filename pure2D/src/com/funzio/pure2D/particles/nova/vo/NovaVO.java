/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author long
 */
public class NovaVO {
    public int version;
    public List<EmitterVO> emitters;
    public List<AnimatorVO> animators;

    // for fast look up
    private Map<String, EmitterVO> mEmitterMap;
    private Map<String, AnimatorVO> mAnimatorMap;

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
        emitters = getEmitters(json.optJSONArray("emitters"));
        animators = getAnimators(json.optJSONArray("animators"));

        // make the maps
        if (emitters != null) {
            mEmitterMap = new HashMap<String, EmitterVO>();
            for (EmitterVO vo : emitters) {
                mEmitterMap.put(vo.name, vo);
            }
        }

        if (animators != null) {
            mAnimatorMap = new HashMap<String, AnimatorVO>();
            for (AnimatorVO vo : animators) {
                if (vo != null) {
                    mAnimatorMap.put(vo.name, vo);
                }
            }
        }
    }

    public NovaVO(final String json) throws JSONException {
        this(new JSONObject(json));
    }

    public EmitterVO getEmitterVO(final String name) {
        return mEmitterMap != null ? mEmitterMap.get(name) : null;
    }

    public AnimatorVO getAnimatorVO(final String name) {
        return mAnimatorMap != null ? mAnimatorMap.get(name) : null;
    }

    protected static List<EmitterVO> getEmitters(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final List<EmitterVO> result = new ArrayList<EmitterVO>();
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

        final List<AnimatorVO> result = new ArrayList<AnimatorVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add(AnimatorVO.create(array.getJSONObject(i)));
        }

        return result;
    }

    protected static List<Integer> getListInteger(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final List<Integer> result = new ArrayList<Integer>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add(array.getInt(i));
        }

        return result;
    }

    protected static List<Float> getListFloat(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final List<Float> result = new ArrayList<Float>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add((float) array.getDouble(i));
        }

        return result;
    }

    protected static List<String> getListString(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final List<String> result = new ArrayList<String>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            result.add(array.getString(i));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Version: " + version + "\n" //
                + "Emitters: " + (emitters == null ? 0 : emitters.size()) + "\n" //
                + "Animators: " + (animators == null ? 0 : animators.size());
    }

}
