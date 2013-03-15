/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;

/**
 * @author long
 */

// @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
// @JsonSubTypes({
// @Type(value = MoveAnimatorVO.class, name = "translate"), //
// @Type(value = RotateAnimatorVO.class, name = "rotate"), //
// @Type(value = ScaleAnimatorVO.class, name = "scale"), //
// @Type(value = MoveAnimatorVO.class, name = "move"), //
// @Type(value = AlphaAnimatorVO.class, name = "alpha"), //
// @Type(value = SequenceAnimatorVO.class, name = "sequence"), //
// @Type(value = ParallelAnimatorVO.class, name = "parallel"), //
// })

public abstract class AnimatorVO {
    public static final String PARALLEL = "parallel";
    public static final String SEQUENCE = "sequence";
    public static final String ALPHA = "alpha";
    public static final String TRANSLATE = "translate";
    public static final String MOVE = "move";
    public static final String ROTATE = "rotate";
    public static final String SCALE = "scale";
    public static final String TRAJECTORY = "trajectory";
    public static final String RECURSIVE_TRAJECTORY = "recursive_trajectory";

    public String name;
    public String type;
    public String loop_mode;
    public boolean accumulating = true; // true by default

    public abstract Animator createAnimator(Manipulatable target, Animator... animators);

    public AnimatorVO() {
        // TODO nothing
    }

    public AnimatorVO(final JSONObject json) throws JSONException {
        name = json.optString("name");
        type = json.optString("type");
        loop_mode = json.optString("loop_mode");

        if (json.has("accumulating")) {
            accumulating = json.getBoolean("accumulating");
        }
    }

    /**
     * Initialize a newly created animator
     * 
     * @param animator
     * @return
     */
    protected Animator init(final Manipulatable target, final Animator animator) {
        // MUST: couple with this VO
        animator.setData(this);
        animator.setAccumulating(accumulating);
        // init and reset
        resetAnimator(target, animator);

        return animator;
    }

    /**
     * Reset a specific animator to reflect this VO
     * 
     * @param animator
     */
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        if (animator != null) {
            animator.reset();
        }
    }

    public static AnimatorVO create(final JSONObject json) throws JSONException {
        if (!json.has("type")) {
            return null;
        }

        final String type = json.getString("type");

        if (type.equalsIgnoreCase(TRANSLATE) || type.equalsIgnoreCase(MOVE)) {
            return new MoveAnimatorVO(json);
        } else if (type.equalsIgnoreCase(ROTATE)) {
            return new RotateAnimatorVO(json);
        } else if (type.equalsIgnoreCase(SCALE)) {
            return new ScaleAnimatorVO(json);
        } else if (type.equalsIgnoreCase(ALPHA)) {
            return new AlphaAnimatorVO(json);
        } else if (type.equalsIgnoreCase(TRAJECTORY)) {
            return new TrajectoryAnimatorVO(json);
        } else if (type.equalsIgnoreCase(RECURSIVE_TRAJECTORY)) {
            return new RecursiveTrajectoryAnimatorVO(json);
        } else if (type.equalsIgnoreCase(SEQUENCE)) {
            return new SequenceAnimatorVO(json);
        } else if (type.equalsIgnoreCase(PARALLEL)) {
            return new ParallelAnimatorVO(json);
        } else {
            return null;
        }
    }
}
