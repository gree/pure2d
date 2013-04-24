/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

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

    public ArrayList<Integer> duration;

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

        animator.setLifespan(NovaConfig.getRandomInt(duration));
    }

}
