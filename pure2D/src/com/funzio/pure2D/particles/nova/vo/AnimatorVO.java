/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.json.JSONException;
import org.json.JSONObject;

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

    public AnimatorVO() {

    }

    public AnimatorVO(final JSONObject json) throws JSONException {
        if (json.has("name")) {
            name = json.getString("name");
        }

        if (json.has("type")) {
            type = json.getString("type");
        }

        if (json.has("loop_mode")) {
            loop_mode = json.getString("loop_mode");
        }
    }

    /**
     * Initialize a newly created animator
     * 
     * @param animator
     * @return
     */
    protected Animator init(final Animator animator) {
        // MUST: couple with this VO
        animator.setData(this);
        // init and reset
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

    public static AnimatorVO create(final JSONObject json) throws JSONException {
        if (!json.has("type")) {
            return null;
        }

        final String type = json.getString("type");

        if (type.equalsIgnoreCase("translate") || type.equalsIgnoreCase("move")) {
            return new MoveAnimatorVO(json);
        } else if (type.equalsIgnoreCase("rotate")) {
            return new RotateAnimatorVO(json);
        } else if (type.equalsIgnoreCase("scale")) {
            return new ScaleAnimatorVO(json);
        } else if (type.equalsIgnoreCase("alpha")) {
            return new AlphaAnimatorVO(json);
        } else if (type.equalsIgnoreCase("sequence")) {
            return new SequenceAnimatorVO(json);
        } else if (type.equalsIgnoreCase("parallel")) {
            return new ParallelAnimatorVO(json);
        } else {
            return null;
        }
    }
}
