/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.GroupAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public abstract class GroupAnimatorVO extends AnimatorVO {
    // child animators
    public ArrayList<AnimatorVO> animators;

    public GroupAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        animators = NovaVO.getAnimators(json.optJSONArray("animators"));
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.AnimatorVO#resetAnimator(com.funzio.pure2D.animators.Animator)
     */
    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        // if (animator != null) {
        final GroupAnimator group = (GroupAnimator) animator;

        if (loop_count != null) {
            group.setLoopCount(NovaConfig.getInt(loop_count, emitIndex, 0));
        }

        Animator childAnimator;
        AnimatorVO vo;
        // reset the children
        final int size = group.getNumAnimators();
        for (int i = 0; i < size; i++) {
            childAnimator = group.getAnimatorAt(i);
            if (childAnimator != null && childAnimator.getData() instanceof AnimatorVO) {
                vo = (AnimatorVO) childAnimator.getData();
                vo.resetAnimator(emitIndex, target, childAnimator);
            }
        }
        // }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.AnimatorVO#applyScale(float)
     */
    @Override
    public void applyScale(final float scale) {
        super.applyScale(scale);

        // apply to all child animators
        if (animators != null) {
            final int size = animators.size();
            for (int i = 0; i < size; i++) {
                animators.get(i).applyScale(scale);
            }
        }
    }
}
