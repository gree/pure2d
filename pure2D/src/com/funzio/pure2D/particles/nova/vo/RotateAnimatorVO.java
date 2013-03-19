/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

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
    public List<Float> from;
    public List<Float> to;
    public List<Float> delta;

    public RotateAnimatorVO() {
        super();
    }

    public RotateAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        from = NovaVO.getListFloat(json, "from");
        to = NovaVO.getListFloat(json, "to");
        delta = NovaVO.getListFloat(json, "delta");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new RotateAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final RotateAnimator rotate = (RotateAnimator) animator;
        if (rotate != null) {
            if (delta != null) {
                rotate.setDelta(NovaConfig.getRandomFloat(delta));
            } else {
                rotate.setValues(NovaConfig.getRandomFloat(from), NovaConfig.getRandomFloat(to));
            }
            rotate.setDuration(NovaConfig.getRandomInt(duration));
        }
    }

}
