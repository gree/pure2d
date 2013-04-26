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
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new WhirlAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final WhirlAnimator whirl = (WhirlAnimator) animator;
        // if (whirl != null) {
        whirl.setValues(NovaConfig.getInt(radius1, emitIndex, 0), NovaConfig.getInt(radius2, emitIndex, WhirlAnimator.DEFAULT_RADIUS), NovaConfig.getInt(degree1, emitIndex, 0),
                NovaConfig.getInt(degree2, emitIndex, (int) WhirlAnimator.DEFAULT_ANGLE * 180));
        whirl.setCircleInterpolator(NovaConfig.getInterpolator(NovaConfig.getString(circle_interpolation, emitIndex)));
        whirl.setCircleRatio(NovaConfig.getFloat(circle_ratio, emitIndex, WhirlAnimator.DEFAULT_CIRCLE_RATIO));
        whirl.setCircleMultiplier(NovaConfig.getFloat(circle_multiplier, emitIndex, 1));
        whirl.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
        // }
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
