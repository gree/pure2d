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

    public ScaleAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        final ArrayList<Float> from = NovaVO.getListFloat(json, "from");
        final ArrayList<Float> to = NovaVO.getListFloat(json, "to");

        x_from = NovaVO.getListFloat(json, "x_from");
        if (x_from == null) {
            x_from = from;
        }
        x_to = NovaVO.getListFloat(json, "x_to");
        if (x_to == null) {
            x_to = to;
        }

        y_from = NovaVO.getListFloat(json, "y_from");
        if (y_from == null) {
            y_from = from;
        }
        y_to = NovaVO.getListFloat(json, "y_to");
        if (y_to == null) {
            y_to = to;
        }
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new ScaleAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final ScaleAnimator scale = (ScaleAnimator) animator;
        // if (scale != null) {
        scale.setValues(NovaConfig.getFloat(x_from, emitIndex, 1), //
                NovaConfig.getFloat(y_from, emitIndex, 1), //
                NovaConfig.getFloat(x_to, emitIndex, 1), //
                NovaConfig.getFloat(y_to, emitIndex, 1));

        scale.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
        // }
    }
}
