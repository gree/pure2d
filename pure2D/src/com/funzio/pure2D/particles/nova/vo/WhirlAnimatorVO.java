/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.WhirlAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class WhirlAnimatorVO extends TweenAnimatorVO {

    public ArrayList<Integer> radius1;
    public ArrayList<Integer> radius2;
    public ArrayList<Integer> degree1;
    public ArrayList<Integer> degree2;
    public ArrayList<String> circle_interpolation;
    public ArrayList<Float> circle_ratio;
    public ArrayList<Float> circle_multiplier;

    public WhirlAnimatorVO() {
        super();
    }

    public WhirlAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        radius1 = NovaVO.getListInt(json, "radius1");
        radius2 = NovaVO.getListInt(json, "radius2");
        degree1 = NovaVO.getListInt(json, "degree1");
        degree2 = NovaVO.getListInt(json, "degree2");
        circle_interpolation = NovaVO.getListString(json, "circle_interpolation");
        circle_ratio = NovaVO.getListFloat(json, "circle_ratio");
        circle_multiplier = NovaVO.getListFloat(json, "circle_multiplier");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new WhirlAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final WhirlAnimator move = (WhirlAnimator) animator;
        if (move != null) {
            move.setValues(NovaConfig.getRandomInt(radius1), NovaConfig.getRandomInt(radius2, WhirlAnimator.DEFAULT_RADIUS), NovaConfig.getRandomInt(degree1),
                    NovaConfig.getRandomInt(degree2, (int) WhirlAnimator.DEFAULT_ANGLE * 180));
            move.setCircleInterpolator(NovaConfig.getInterpolator(NovaConfig.getRandomString(circle_interpolation)));
            move.setCircleRatio(NovaConfig.getRandomFloat(circle_ratio, WhirlAnimator.DEFAULT_CIRCLE_RATIO));
            move.setCircleMultiplier(NovaConfig.getRandomFloat(circle_multiplier, 1));
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

        // scale radius1
        if (radius1 != null) {
            final int size = radius1.size();
            for (int i = 0; i < size; i++) {
                radius1.set(i, Math.round(radius1.get(i) * scale));
            }
        }

        // scale radius2
        if (radius2 != null) {
            final int size = radius2.size();
            for (int i = 0; i < size; i++) {
                radius2.set(i, Math.round(radius2.get(i) * scale));
            }
        }

    }
}
