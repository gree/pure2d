/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.AlphaAnimator;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class AlphaAnimatorVO extends TweenAnimatorVO {

    public ArrayList<Float> from;
    public ArrayList<Float> to;

    public AlphaAnimatorVO() {
        super();
    }

    public AlphaAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        from = NovaVO.getListFloat(json, "from");
        to = NovaVO.getListFloat(json, "to");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new AlphaAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final AlphaAnimator alpha = (AlphaAnimator) animator;
        // if (alpha != null) {
        alpha.setValues(NovaConfig.getRandomFloat(from), NovaConfig.getRandomFloat(to));
        alpha.setDuration(NovaConfig.getRandomInt(duration));
        // }
    }
}
