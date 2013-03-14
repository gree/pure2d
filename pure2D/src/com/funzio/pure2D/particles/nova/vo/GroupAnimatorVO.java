/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.GroupAnimator;

/**
 * @author long
 */
public abstract class GroupAnimatorVO extends AnimatorVO {
    public List<AnimatorVO> animators;

    public abstract GroupAnimator createAnimator(Animator... animators);

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.AnimatorVO#resetAnimator(com.funzio.pure2D.animators.Animator)
     */
    // @Override
    // public void resetAnimator(final Animator animator) {
    // super.resetAnimator(animator);
    //
    // if (animator != null) {
    // GroupAnimator group = (GroupAnimator) animator;
    // int size = group.getNumAnimators();
    // Animator childAnimator;
    // // reset the children
    // for (int i = 0; i < size; i++) {
    // childAnimator = group.getAnimatorAt(i);
    // if (childAnimator != null && childAnimator.getData() instanceof AnimatorVO) {
    // AnimatorVO vo = (AnimatorVO) childAnimator.getData();
    // vo.resetAnimator(childAnimator);
    // }
    // }
    // }
    // }
}
