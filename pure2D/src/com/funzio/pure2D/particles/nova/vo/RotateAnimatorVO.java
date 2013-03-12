/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class RotateAnimatorVO extends TweenAnimatorVO {

    public List<Float> delta;

    public RotateAnimatorVO() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Animator createAnimator() {
        final RotateAnimator rotate = new RotateAnimator(NovaConfig.getInterpolator(interpolator));
        rotate.setAccumulating(true);
        resetAnimator(rotate);
        return rotate;
    }

    @Override
    public void resetAnimator(final Animator animator) {
        final RotateAnimator rotate = (RotateAnimator) animator;
        if (rotate != null) {
            rotate.setDelta(NovaConfig.getRandomFloat(delta));
            rotate.setDuration(NovaConfig.getRandomInt(duration));
        }
    }

}
