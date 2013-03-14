/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.GroupAnimator;
import com.funzio.pure2D.animators.SequenceAnimator;

/**
 * @author long
 */
public class SequenceAnimatorVO extends GroupAnimatorVO {

    public SequenceAnimatorVO() {
        super();
    }

    public SequenceAnimatorVO(final JSONObject json) throws JSONException {
        super(json);
    }

    @Override
    public GroupAnimator createAnimator(final Animator... animators) {
        return (SequenceAnimator) init(new SequenceAnimator(animators));
    }
}
