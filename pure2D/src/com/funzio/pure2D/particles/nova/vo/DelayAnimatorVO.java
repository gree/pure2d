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

    public DelayAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        duration = NovaVO.getListInt(json, "duration");
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new DelayAnimator());
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        animator.setLifespan(NovaConfig.getInt(duration, emitIndex, 0));
    }

}
