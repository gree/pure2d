/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.TornadoAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class TornadoAnimatorVO extends TweenAnimatorVO {

    public List<Integer> dx;
    public List<Integer> dy;
    public List<Integer> circle_radius;
    public List<Integer> circle_num;
    public List<String> circle_interpolation;

    public TornadoAnimatorVO() {
        super();
    }

    public TornadoAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        dx = NovaVO.getListInt(json, "dx");
        dy = NovaVO.getListInt(json, "dy");
        circle_radius = NovaVO.getListInt(json, "circle_radius");
        circle_num = NovaVO.getListInt(json, "circle_num");
        circle_interpolation = NovaVO.getListString(json, "circle_interpolation");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new TornadoAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final TornadoAnimator move = (TornadoAnimator) animator;
        if (move != null) {
            move.setDelta(NovaConfig.getRandomInt(dx), NovaConfig.getRandomInt(dy));
            move.setCircles(NovaConfig.getRandomInt(circle_radius), NovaConfig.getRandomInt(circle_num), TornadoAnimator.DEFAULT_CIRCLE_RATIO,
                    NovaConfig.getInterpolator(NovaConfig.getRandomString(circle_interpolation)));
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

        // scale dx
        if (dx != null) {
            final int size = dx.size();
            for (int i = 0; i < size; i++) {
                dx.set(i, Math.round(dx.get(i) * scale));
            }
        }

        // scale dy
        if (dy != null) {
            final int size = dy.size();
            for (int i = 0; i < size; i++) {
                dy.set(i, Math.round(dy.get(i) * scale));
            }
        }

        // scale radius
        if (circle_radius != null) {
            final int size = circle_radius.size();
            for (int i = 0; i < size; i++) {
                circle_radius.set(i, Math.round(circle_radius.get(i) * scale));
            }
        }
    }
}
