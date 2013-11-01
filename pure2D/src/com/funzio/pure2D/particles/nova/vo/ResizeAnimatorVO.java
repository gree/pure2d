/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.ResizeAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class ResizeAnimatorVO extends TweenAnimatorVO {

    public ArrayList<Float> width_from;
    public ArrayList<Float> width_to;
    public ArrayList<Float> height_from;
    public ArrayList<Float> height_to;

    public ResizeAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        width_from = NovaVO.getListFloat(json, "width_from");
        width_to = NovaVO.getListFloat(json, "width_to");

        height_from = NovaVO.getListFloat(json, "height_from");
        height_to = NovaVO.getListFloat(json, "height_to");
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new ResizeAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final ResizeAnimator resize = (ResizeAnimator) animator;
        resize.setValues(NovaConfig.getFloat(width_from, emitIndex, 1), //
                NovaConfig.getFloat(height_from, emitIndex, 1), //
                NovaConfig.getFloat(width_to, emitIndex, 1), //
                NovaConfig.getFloat(height_to, emitIndex, 1));

        resize.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
    }
}
