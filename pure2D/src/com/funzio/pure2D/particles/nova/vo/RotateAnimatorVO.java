/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class RotateAnimatorVO extends TweenAnimatorVO {

    public List<Float> delta;

    public RotateAnimatorVO() {
        super();
    }

    public RotateAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        if (json.has("delta")) {
            delta = NovaVO.getListFloat(json.getJSONArray("delta"));
        }
    }

    @Override
    public Animator createAnimator() {
        return init(new RotateAnimator(NovaConfig.getInterpolator(interpolator)));
    }

    @Override
    public void resetAnimator(final Animator animator) {
        super.resetAnimator(animator);

        final RotateAnimator rotate = (RotateAnimator) animator;
        if (rotate != null) {
            rotate.setDelta(NovaConfig.getRandomFloat(delta));
            rotate.setDuration(NovaConfig.getRandomInt(duration));
        }
    }

}
