/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.MoveAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class MoveAnimatorVO extends TweenAnimatorVO {

    public List<Float> dx;
    public List<Float> dy;

    public MoveAnimatorVO() {
        super();
    }

    public MoveAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        if (json.has("dx")) {
            dx = NovaVO.getListFloat(json.getJSONArray("dx"));
        }

        if (json.has("dy")) {
            dy = NovaVO.getListFloat(json.getJSONArray("dy"));
        }
    }

    @Override
    public Animator createAnimator() {
        return init(new MoveAnimator(NovaConfig.getInterpolator(interpolator)));
    }

    @Override
    public void resetAnimator(final Animator animator) {
        super.resetAnimator(animator);

        final MoveAnimator move = (MoveAnimator) animator;
        if (move != null) {
            move.setDelta(NovaConfig.getRandomFloat(dx), NovaConfig.getRandomFloat(dy));
            move.setDuration(NovaConfig.getRandomInt(duration));
        }
    }

}
