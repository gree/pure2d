/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.GroupAnimator;
import com.funzio.pure2D.animators.SequenceAnimator;

/**
 * @author long
 */
public class SequenceAnimatorVO extends GroupAnimatorVO {

    @Override
    public GroupAnimator createAnimator(final Animator... animators) {
        return (SequenceAnimator) init(new SequenceAnimator(animators));
    }
}
