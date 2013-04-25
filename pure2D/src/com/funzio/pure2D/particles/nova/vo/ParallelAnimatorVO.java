/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.ParallelAnimator;

/**
 * @author long
 */
public class ParallelAnimatorVO extends GroupAnimatorVO {

    public ParallelAnimatorVO(final JSONObject json) throws JSONException {
        super(json);
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new ParallelAnimator(animators));
    }
}
