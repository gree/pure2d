/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.funzio.pure2D.animators.Animator;

/**
 * @author long
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = MoveAnimatorVO.class, name = "translate"), //
        @Type(value = RotateAnimatorVO.class, name = "rotate"), //
        @Type(value = ScaleAnimatorVO.class, name = "scale"), //
        @Type(value = MoveAnimatorVO.class, name = "move"), //
        @Type(value = AlphaAnimatorVO.class, name = "alpha"), //
        @Type(value = SequenceAnimatorVO.class, name = "sequence"), //
        @Type(value = ParallelAnimatorVO.class, name = "parallel"), //
})
public abstract class AnimatorVO {
    public String name;
    public String type;
    public String loop_mode;

    /**
     * Initialize a newly created animator
     * 
     * @param animator
     * @return
     */
    protected Animator init(final Animator animator) {
        // couple with this VO
        animator.setData(this);
        // reset
        resetAnimator(animator);

        return animator;
    }

    /**
     * Reset a specific animator to reflect this VO
     * 
     * @param animator
     */
    public void resetAnimator(final Animator animator) {
        if (animator != null) {
            animator.reset();
        }
    }
}
