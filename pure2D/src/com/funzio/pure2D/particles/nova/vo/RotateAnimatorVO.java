/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class RotateAnimatorVO extends TweenAnimatorVO {
    public ArrayList<Float> from;
    public ArrayList<Float> to;
    public ArrayList<Float> delta;

    public RotateAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        from = NovaVO.getListFloat(json, "from");
        to = NovaVO.getListFloat(json, "to");
        delta = NovaVO.getListFloat(json, "delta");
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new RotateAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final RotateAnimator rotate = (RotateAnimator) animator;
        // if (rotate != null) {
        if (delta != null) {
            rotate.setDelta(NovaConfig.getFloat(delta, emitIndex, 0));
        } else {
            rotate.setValues(NovaConfig.getFloat(from, emitIndex, 0), NovaConfig.getFloat(to, emitIndex, 0));
        }
        rotate.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
        // }
    }

}
