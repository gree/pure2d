/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.ColorAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class ColorAnimatorVO extends TweenAnimatorVO {

    public List<Float> r_from;
    public List<Float> r_to;
    public List<Float> g_from;
    public List<Float> g_to;
    public List<Float> b_from;
    public List<Float> b_to;
    public List<Float> a_from;
    public List<Float> a_to;

    public ColorAnimatorVO() {
        super();
    }

    public ColorAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        r_from = NovaVO.getListFloat(json, "r_from");
        r_to = NovaVO.getListFloat(json, "r_to");
        g_from = NovaVO.getListFloat(json, "g_from");
        g_to = NovaVO.getListFloat(json, "g_to");
        b_from = NovaVO.getListFloat(json, "b_from");
        b_to = NovaVO.getListFloat(json, "b_to");
        a_from = NovaVO.getListFloat(json, "a_from");
        a_to = NovaVO.getListFloat(json, "a_to");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new ColorAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final ColorAnimator color = (ColorAnimator) animator;
        if (color != null) {
            color.setValues(r_from != null ? NovaConfig.getRandomFloat(r_from) : 1, //
                    g_from != null ? NovaConfig.getRandomFloat(g_from) : 1, //
                    b_from != null ? NovaConfig.getRandomFloat(b_from) : 1, //
                    a_from != null ? NovaConfig.getRandomFloat(a_from) : 1, //
                    r_to != null ? NovaConfig.getRandomFloat(r_to) : 1, //
                    g_to != null ? NovaConfig.getRandomFloat(g_to) : 1, //
                    b_to != null ? NovaConfig.getRandomFloat(b_to) : 1, //
                    a_to != null ? NovaConfig.getRandomFloat(a_to) : 1);

            color.setDuration(NovaConfig.getRandomInt(duration));
        }
    }
}
