/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import com.funzio.pure2D.animators.Animator;

/**
 * @author long
 */
public abstract class TweenAnimatorVO extends AnimatorVO {
    public List<Integer> duration;
    public String interpolator;
    public boolean accumulating = true; // true by default

    public abstract Animator createAnimator();
}
