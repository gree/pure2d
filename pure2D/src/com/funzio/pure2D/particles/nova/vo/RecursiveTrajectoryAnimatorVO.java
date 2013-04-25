/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.RecursiveTrajectoryAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class RecursiveTrajectoryAnimatorVO extends TrajectoryAnimatorVO {

    public ArrayList<Float> deceleration_rate;

    public RecursiveTrajectoryAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        deceleration_rate = NovaVO.getListFloat(json, "deceleration_rate");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new RecursiveTrajectoryAnimator());
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final RecursiveTrajectoryAnimator move = (RecursiveTrajectoryAnimator) animator;
        if (move != null) {
            if (deceleration_rate != null) {
                move.setDecelerationRate(NovaConfig.getRandomFloat(deceleration_rate));
            }
        }
    }

}
