/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.DelayAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class DelayAnimatorVO extends AnimatorVO {

    public List<Integer> duration;

    public DelayAnimatorVO() {
        super();
    }

    public DelayAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        duration = NovaVO.getListInt(json, "duration");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new DelayAnimator());
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final DelayAnimator move = (DelayAnimator) animator;
        if (move != null) {
            move.setLifespan(NovaConfig.getRandomInt(duration));
        }
    }

}
