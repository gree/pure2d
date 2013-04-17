/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.MoveRadiusAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class MoveRadiusAnimatorVO extends TweenAnimatorVO {

    public List<Integer> radius;
    public List<Integer> degree;

    public MoveRadiusAnimatorVO() {
        super();
    }

    public MoveRadiusAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        radius = NovaVO.getListInt(json, "radius");
        degree = NovaVO.getListInt(json, "degree");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new MoveRadiusAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final MoveRadiusAnimator move = (MoveRadiusAnimator) animator;
        if (move != null) {
            move.setValues(NovaConfig.getRandomInt(radius), NovaConfig.getRandomInt(degree));
            move.setDuration(NovaConfig.getRandomInt(duration));
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.AnimatorVO#applyScale(float)
     */
    @Override
    public void applyScale(final float scale) {
        super.applyScale(scale);

        // scale radius
        if (radius != null) {
            final int size = radius.size();
            for (int i = 0; i < size; i++) {
                radius.set(i, Math.round(radius.get(i) * scale));
            }
        }
    }

}
