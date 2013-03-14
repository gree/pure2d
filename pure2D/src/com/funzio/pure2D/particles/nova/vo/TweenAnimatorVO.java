/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.TweenAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public abstract class TweenAnimatorVO extends AnimatorVO {
    public boolean accumulating = true; // true by default

    public String interpolator;
    public List<Integer> duration;

    public abstract Animator createAnimator();

    public TweenAnimatorVO() {
        super();
    }

    public TweenAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        if (json.has("accumulating")) {
            accumulating = json.getBoolean("accumulating");
        }

        if (json.has("interpolator")) {
            type = json.getString("interpolator");
        }

        if (json.has("duration")) {
            duration = NovaVO.getListInteger(json.getJSONArray("duration"));
        }

    }

    @Override
    protected Animator init(final Animator animator) {
        ((TweenAnimator) animator).setAccumulating(accumulating);
        ((TweenAnimator) animator).setLoop(NovaConfig.getLoopMode(loop_mode));

        return super.init(animator);
    }
}
