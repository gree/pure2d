/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.TrajectoryAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class TrajectoryAnimatorVO extends AnimatorVO {

    public List<Float> ground;
    public List<Float> velocity;
    public List<Float> angle;

    public TrajectoryAnimatorVO() {
        super();
    }

    public TrajectoryAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        ground = NovaVO.getListFloat(json, "ground");
        velocity = NovaVO.getListFloat(json, "velocity");
        angle = NovaVO.getListFloat(json, "angle");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new TrajectoryAnimator());
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final TrajectoryAnimator move = (TrajectoryAnimator) animator;
        if (move != null) {
            move.setGround(NovaConfig.getRandomFloat(ground));
            move.setValues(target.getPosition().x, target.getPosition().y, NovaConfig.getRandomFloat(velocity), NovaConfig.getRandomFloat(angle));
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.AnimatorVO#applyScale(float)
     */
    @Override
    public void applyScale(final float scale) {
        super.applyScale(scale);

        // scale velocity
        if (velocity != null) {
            final int size = velocity.size();
            for (int i = 0; i < size; i++) {
                velocity.set(i, velocity.get(i) * scale);
            }
        }
    }

}
