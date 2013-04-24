/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.TweenAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public abstract class TweenAnimatorVO extends AnimatorVO {
    public String interpolation;
    public ArrayList<Integer> duration;

    public TweenAnimatorVO() {
        super();
    }

    public TweenAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        interpolation = json.optString("interpolation");
        duration = NovaVO.getListInt(json, "duration");
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.AnimatorVO#resetAnimator(com.funzio.pure2D.Manipulatable, com.funzio.pure2D.animators.Animator)
     */
    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        // if (animator instanceof TweenAnimator) {
        ((TweenAnimator) animator).setLoop(NovaConfig.getLoopMode(loop_mode));

        if (loop_count != null) {
            ((TweenAnimator) animator).setLoopCount(NovaConfig.getRandomInt(loop_count));
        }
        // }
    }
}
