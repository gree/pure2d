/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

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
    public List<AnimatorVO> animators;

    public GroupAnimatorVO() {

    }

    public GroupAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        animators = NovaVO.getAnimators(json.optJSONArray("animators"));
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.AnimatorVO#resetAnimator(com.funzio.pure2D.animators.Animator)
     */
    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        if (animator != null) {
            GroupAnimator group = (GroupAnimator) animator;

            if (loop_count != null) {
                group.setLoopCount(NovaConfig.getRandomInt(loop_count));
            }

            Animator childAnimator;
            AnimatorVO vo;
            // reset the children
            final int size = group.getNumAnimators();
            for (int i = 0; i < size; i++) {
                childAnimator = group.getAnimatorAt(i);
                if (childAnimator != null && childAnimator.getData() instanceof AnimatorVO) {
                    vo = (AnimatorVO) childAnimator.getData();
                    vo.resetAnimator(target, childAnimator);
                }
            }
        }
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
