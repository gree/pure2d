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

    public List<Integer> r_from;
    public List<Integer> r_to;
    public List<Integer> g_from;
    public List<Integer> g_to;
    public List<Integer> b_from;
    public List<Integer> b_to;
    public List<Integer> a_from;
    public List<Integer> a_to;

    public ColorAnimatorVO() {
        super();
    }

    public ColorAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        r_from = NovaVO.getListInt(json, "r_from");
        r_to = NovaVO.getListInt(json, "r_to");
        g_from = NovaVO.getListInt(json, "g_from");
        g_to = NovaVO.getListInt(json, "g_to");
        b_from = NovaVO.getListInt(json, "b_from");
        b_to = NovaVO.getListInt(json, "b_to");
        a_from = NovaVO.getListInt(json, "a_from");
        a_to = NovaVO.getListInt(json, "a_to");
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
            color.setValues(NovaConfig.getRandomInt(r_from, 255), //
                    NovaConfig.getRandomInt(g_from, 255), //
                    NovaConfig.getRandomInt(b_from, 255), //
                    NovaConfig.getRandomInt(a_from, 255), //
                    NovaConfig.getRandomInt(r_to, 255), //
                    NovaConfig.getRandomInt(g_to, 255), //
                    NovaConfig.getRandomInt(b_to, 255), //
                    NovaConfig.getRandomInt(a_to, 255));

            color.setDuration(NovaConfig.getRandomInt(duration));
        }
    }
}
