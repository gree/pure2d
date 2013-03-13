/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import com.funzio.pure2D.animators.AlphaAnimator;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class AlphaAnimatorVO extends TweenAnimatorVO {

    public List<Float> from;
    public List<Float> to;

    @Override
    public Animator createAnimator() {
        final AlphaAnimator alpha = new AlphaAnimator(NovaConfig.getInterpolator(interpolator));
        alpha.setAccumulating(accumulating);
        alpha.setLoop(NovaConfig.getLoopMode(loop_mode));
        resetAnimator(alpha);
        return alpha;
    }

    @Override
    public void resetAnimator(final Animator animator) {
        super.resetAnimator(animator);

        final AlphaAnimator alpha = (AlphaAnimator) animator;
        if (alpha != null) {
            alpha.setValues(NovaConfig.getRandomFloat(from), NovaConfig.getRandomFloat(to));
            alpha.setDuration(NovaConfig.getRandomInt(duration));
        }
    }
}
