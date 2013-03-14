/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.TweenAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public abstract class TweenAnimatorVO extends AnimatorVO {
    public List<Integer> duration;
    public String interpolator;
    public boolean accumulating = true; // true by default

    public abstract Animator createAnimator();

    @Override
    protected Animator init(final Animator animator) {
        ((TweenAnimator) animator).setAccumulating(accumulating);
        ((TweenAnimator) animator).setLoop(NovaConfig.getLoopMode(loop_mode));

        return super.init(animator);
    }
}
