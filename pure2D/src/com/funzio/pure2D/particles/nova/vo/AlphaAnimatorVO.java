/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.AlphaAnimator;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class AlphaAnimatorVO extends TweenAnimatorVO {

    public ArrayList<Float> from;
    public ArrayList<Float> to;

    public AlphaAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        from = NovaVO.getListFloat(json, "from");
        to = NovaVO.getListFloat(json, "to");
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new AlphaAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final AlphaAnimator alpha = (AlphaAnimator) animator;
        // if (alpha != null) {
        alpha.setValues(NovaConfig.getFloat(from, emitIndex, 1), NovaConfig.getFloat(to, emitIndex, 0));
        alpha.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
        // }
    }
}
