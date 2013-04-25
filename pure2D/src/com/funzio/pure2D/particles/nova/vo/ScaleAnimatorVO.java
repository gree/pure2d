/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.ScaleAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class ScaleAnimatorVO extends TweenAnimatorVO {

    public ArrayList<Float> x_from;
    public ArrayList<Float> x_to;
    public ArrayList<Float> y_from;
    public ArrayList<Float> y_to;

    public ScaleAnimatorVO() {
        super();
    }

    public ScaleAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        x_from = NovaVO.getListFloat(json, "x_from");
        x_to = NovaVO.getListFloat(json, "x_to");
        y_from = NovaVO.getListFloat(json, "y_from");
        y_to = NovaVO.getListFloat(json, "y_to");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new ScaleAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final ScaleAnimator scale = (ScaleAnimator) animator;
        // if (scale != null) {
        scale.setValues(NovaConfig.getRandomFloat(x_from, 1), //
                NovaConfig.getRandomFloat(y_from, 1), //
                NovaConfig.getRandomFloat(x_to, 1), //
                NovaConfig.getRandomFloat(y_to, 1));

        scale.setDuration(NovaConfig.getRandomInt(duration));
        // }
    }
}
