/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.particles.nova.NovaConfig;

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

public abstract class AnimatorVO extends NovaEntryVO {
    public static final String PARALLEL = "parallel";
    public static final String SEQUENCE = "sequence";
    public static final String ALPHA = "alpha";
    public static final String TRANSLATE = "translate";
    public static final String MOVE = "move";
    public static final String UNSTABLE_MOVE = "unstable_move";
    public static final String ROTATE = "rotate";
    public static final String SCALE = "scale";
    public static final String SKEW = "skew";
    public static final String RESIZE = "resize";
    public static final String TRAJECTORY = "trajectory";
    public static final String RECURSIVE_TRAJECTORY = "recursive_trajectory";
    public static final String SIN_WAVE = "sin_wave";
    public static final String TORNADO = "tornado";
    public static final String WHIRL = "whirl";
    public static final String COLOR = "color";
    public static final String DELAY = "delay";
    public static final String SET = "set";

    public String name;
    public String type;
    public boolean accumulating = true; // true by default

    // looping
    public String loop_mode;
    public ArrayList<Integer> loop_count;
    public ArrayList<Integer> start_delay;
    public ArrayList<Integer> lifespan;

    public abstract Animator createAnimator(int emitIndex, Manipulatable target, Animator... animators);

    public AnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        name = json.optString("name");
        type = json.optString("type");

        if (json.has("accumulating")) {
            accumulating = json.getBoolean("accumulating");
        }

        loop_mode = json.optString("loop_mode");
        loop_count = NovaVO.getListInt(json, "loop_count");
        start_delay = NovaVO.getListInt(json, "start_delay");
        lifespan = NovaVO.getListInt(json, "lifespan");
    }

    /**
     * Initialize a newly created animator
     * 
     * @param animator
     * @return
     */
    final protected Animator init(final int emitIndex, final Manipulatable target, final Animator animator) {
        // MUST: couple with this VO
        animator.setData(this);
        animator.setAccumulating(accumulating);
        // init and reset
        resetAnimator(emitIndex, target, animator);

        return animator;
    }

    /**
     * Reset a specific animator to reflect this VO
     * 
     * @param animator
     */
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        // if (animator != null) {
        animator.reset();
        animator.setStartDelay(NovaConfig.getInt(start_delay, emitIndex, 0));
        animator.setLifespan(NovaConfig.getInt(lifespan, emitIndex, 0));
        // }
    }

    /**
     * @param scale
     * @see TextureOptions
     */
    public void applyScale(final float scale) {
        // TODO
    }

    public static AnimatorVO create(final JSONObject json) throws JSONException {
        if (!json.has("type")) {
            return null;
        }

        final String type = json.getString("type");

        if (type.equalsIgnoreCase(SEQUENCE)) {
            return new SequenceAnimatorVO(json);
        } else if (type.equalsIgnoreCase(PARALLEL)) {
            return new ParallelAnimatorVO(json);
        } else if (type.equalsIgnoreCase(TRANSLATE) || type.equalsIgnoreCase(MOVE)) {
            return new MoveAnimatorVO(json);
        } else if (type.equalsIgnoreCase(UNSTABLE_MOVE)) {
            return new UnstableMoveAnimatorVO(json);
        } else if (type.equalsIgnoreCase(ROTATE)) {
            return new RotateAnimatorVO(json);
        } else if (type.equalsIgnoreCase(SCALE)) {
            return new ScaleAnimatorVO(json);
        } else if (type.equalsIgnoreCase(SKEW)) {
            return new SkewAnimatorVO(json);
        } else if (type.equalsIgnoreCase(RESIZE)) {
            return new ResizeAnimatorVO(json);
        } else if (type.equalsIgnoreCase(ALPHA)) {
            return new AlphaAnimatorVO(json);
        } else if (type.equalsIgnoreCase(TRAJECTORY)) {
            return new TrajectoryAnimatorVO(json);
        } else if (type.equalsIgnoreCase(RECURSIVE_TRAJECTORY)) {
            return new RecursiveTrajectoryAnimatorVO(json);
        } else if (type.equalsIgnoreCase(SIN_WAVE)) {
            return new SinWaveAnimatorVO(json);
        } else if (type.equalsIgnoreCase(TORNADO)) {
            return new TornadoAnimatorVO(json);
        } else if (type.equalsIgnoreCase(WHIRL)) {
            return new WhirlAnimatorVO(json);
        } else if (type.equalsIgnoreCase(COLOR)) {
            return new ColorAnimatorVO(json);
        } else if (type.equalsIgnoreCase(DELAY)) {
            return new DelayAnimatorVO(json);
        } else if (type.equalsIgnoreCase(SET)) {
            return new PropertiesSetterVO(json);
        } else {
            return null;
        }
    }
}
