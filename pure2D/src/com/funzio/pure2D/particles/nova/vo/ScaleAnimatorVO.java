/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.ScaleAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class ScaleAnimatorVO extends TweenAnimatorVO {

    public List<Float> sx1;
    public List<Float> sx2;
    public List<Float> sy1;
    public List<Float> sy2;

    @Override
    public Animator createAnimator() {
        final ScaleAnimator scale = new ScaleAnimator(NovaConfig.getInterpolator(interpolator));
        scale.setAccumulating(accumulating);
        scale.setLoop(NovaConfig.getLoopMode(loop_mode));
        resetAnimator(scale);
        return scale;
    }

    @Override
    public void resetAnimator(final Animator animator) {
        final ScaleAnimator rotate = (ScaleAnimator) animator;
        if (rotate != null) {
            rotate.setValues(NovaConfig.getRandomFloat(sx1), NovaConfig.getRandomFloat(sy1), NovaConfig.getRandomFloat(sx2), NovaConfig.getRandomFloat(sy2));
            rotate.setDuration(NovaConfig.getRandomInt(duration));
        }
    }
}
