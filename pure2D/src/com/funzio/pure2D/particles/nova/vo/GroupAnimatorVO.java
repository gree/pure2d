/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author long
 */
public abstract class GroupAnimatorVO extends AnimatorVO {
    public List<AnimatorVO> animators;

    public GroupAnimatorVO() {

    }

    public GroupAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        if (json.has("animators")) {
            animators = NovaVO.getAnimators(json.getJSONArray("animators"));
        }

    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.AnimatorVO#resetAnimator(com.funzio.pure2D.animators.Animator)
     */
    // @Override
    // public void resetAnimator(final Animator animator) {
    // super.resetAnimator(animator);
    //
    // if (animator != null) {
    // GroupAnimator group = (GroupAnimator) animator;
    // int size = group.getNumAnimators();
    // Animator childAnimator;
    // // reset the children
    // for (int i = 0; i < size; i++) {
    // childAnimator = group.getAnimatorAt(i);
    // if (childAnimator != null && childAnimator.getData() instanceof AnimatorVO) {
    // AnimatorVO vo = (AnimatorVO) childAnimator.getData();
    // vo.resetAnimator(childAnimator);
    // }
    // }
    // }
    // }
}
