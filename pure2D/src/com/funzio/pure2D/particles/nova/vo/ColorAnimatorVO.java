/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.ColorAnimator;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author juni
 */
public class ColorAnimatorVO extends TweenAnimatorVO {

    public ArrayList<GLColor> from;
    public ArrayList<GLColor> to;

    public ColorAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        from = NovaVO.getListColor(json, "from");
        to = NovaVO.getListColor(json, "to");
    }

    @Override
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new ColorAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final ColorAnimator color = (ColorAnimator) animator;
        // if (color != null) {
        color.setValues(NovaConfig.getColor(from, emitIndex, GLColor.WHITE), NovaConfig.getColor(to, emitIndex, GLColor.WHITE));

        color.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
        // }
    }
}
