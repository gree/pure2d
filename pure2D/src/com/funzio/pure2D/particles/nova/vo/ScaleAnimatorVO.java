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

    public List<Float> x_from;
    public List<Float> x_to;
    public List<Float> y_from;
    public List<Float> y_to;

    @Override
    public Animator createAnimator() {
        return init(new ScaleAnimator(NovaConfig.getInterpolator(interpolator)));
    }

    @Override
    public void resetAnimator(final Animator animator) {
        super.resetAnimator(animator);

        final ScaleAnimator scale = (ScaleAnimator) animator;
        if (scale != null) {
            scale.setValues(NovaConfig.getRandomFloat(x_from), NovaConfig.getRandomFloat(y_from), NovaConfig.getRandomFloat(x_to), NovaConfig.getRandomFloat(y_to));
            scale.setDuration(NovaConfig.getRandomInt(duration));
        }
    }
}
