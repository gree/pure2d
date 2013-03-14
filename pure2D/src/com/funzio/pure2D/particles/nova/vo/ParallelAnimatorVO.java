/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.GroupAnimator;
import com.funzio.pure2D.animators.ParallelAnimator;

/**
 * @author long
 */
public class ParallelAnimatorVO extends GroupAnimatorVO {

    public ParallelAnimatorVO() {
        super();
    }

    public ParallelAnimatorVO(final JSONObject json) throws JSONException {
        super(json);
    }

    @Override
    public GroupAnimator createAnimator(final Animator... animators) {
        return (ParallelAnimator) init(new ParallelAnimator(animators));
    }
}
