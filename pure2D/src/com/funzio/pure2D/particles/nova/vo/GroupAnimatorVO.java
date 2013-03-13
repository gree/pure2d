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
}
