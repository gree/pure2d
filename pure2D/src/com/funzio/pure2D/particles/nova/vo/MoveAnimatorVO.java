/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.MoveAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class MoveAnimatorVO extends TweenAnimatorVO {

    public List<Float> dx;
    public List<Float> dy;

    @Override
    public Animator createAnimator() {
        final MoveAnimator move = new MoveAnimator(NovaConfig.getInterpolator(interpolator));
        move.setAccumulating(true);
        resetAnimator(move);
        return move;
    }

    @Override
    public void resetAnimator(final Animator animator) {
        final MoveAnimator move = (MoveAnimator) animator;
        if (move != null) {
            move.setDelta(NovaConfig.getRandomFloat(dx), NovaConfig.getRandomFloat(dy));
            move.setDuration(NovaConfig.getRandomInt(duration));
        }
    }

}
