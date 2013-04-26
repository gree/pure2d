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
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author juni
 */
public class ColorAnimatorVO extends TweenAnimatorVO {

    public ArrayList<Integer> r_from;
    public ArrayList<Integer> r_to;
    public ArrayList<Integer> g_from;
    public ArrayList<Integer> g_to;
    public ArrayList<Integer> b_from;
    public ArrayList<Integer> b_to;
    public ArrayList<Integer> a_from;
    public ArrayList<Integer> a_to;

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
    public Animator createAnimator(final int emitIndex, final Manipulatable target, final Animator... animators) {
        return init(emitIndex, target, new ColorAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final int emitIndex, final Manipulatable target, final Animator animator) {
        super.resetAnimator(emitIndex, target, animator);

        final ColorAnimator color = (ColorAnimator) animator;
        // if (color != null) {
        color.setValues(NovaConfig.getInt(r_from, emitIndex, 255), //
                NovaConfig.getInt(g_from, emitIndex, 255), //
                NovaConfig.getInt(b_from, emitIndex, 255), //
                NovaConfig.getInt(a_from, emitIndex, 255), //
                NovaConfig.getInt(r_to, emitIndex, 255), //
                NovaConfig.getInt(g_to, emitIndex, 255), //
                NovaConfig.getInt(b_to, emitIndex, 255), //
                NovaConfig.getInt(a_to, emitIndex, 255));

        color.setDuration(NovaConfig.getInt(duration, emitIndex, 0));
        // }
    }
}
