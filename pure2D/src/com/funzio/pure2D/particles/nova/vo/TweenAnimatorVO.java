/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

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
    public String interpolator;
    public List<Integer> duration;

    public TweenAnimatorVO() {
        super();
    }

    public TweenAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        type = json.optString("interpolator");
        duration = NovaVO.getListInteger(json.optJSONArray("duration"));
    }

    @Override
    protected Animator init(final Manipulatable target, final Animator animator) {
        if (animator instanceof TweenAnimator) {
            ((TweenAnimator) animator).setLoop(NovaConfig.getLoopMode(loop_mode));
        }

        return super.init(target, animator);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.AnimatorVO#resetAnimator(com.funzio.pure2D.Manipulatable, com.funzio.pure2D.animators.Animator)
     */
    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        if (animator instanceof TweenAnimator) {
            ((TweenAnimator) animator).setLoopCount(NovaConfig.getRandomInt(loop_count));
        }
    }
}
