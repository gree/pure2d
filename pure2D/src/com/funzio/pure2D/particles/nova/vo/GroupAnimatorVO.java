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

/**
 * @author long
 */
public abstract class GroupAnimatorVO extends AnimatorVO {
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
}
