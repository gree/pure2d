/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.SinWaveAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class SinWaveAnimatorVO extends TweenAnimatorVO {

    public List<Integer> dx;
    public List<Integer> dy;
    public List<Integer> wave_radius;
    public List<Integer> wave_num;

    public SinWaveAnimatorVO() {
        super();
    }

    public SinWaveAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        dx = NovaVO.getListInt(json, "dx");
        dy = NovaVO.getListInt(json, "dy");
        wave_radius = NovaVO.getListInt(json, "wave_radius");
        wave_num = NovaVO.getListInt(json, "wave_num");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new SinWaveAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final SinWaveAnimator move = (SinWaveAnimator) animator;
        if (move != null) {
            move.setDelta(NovaConfig.getRandomInt(dx), NovaConfig.getRandomInt(dy));
            move.setDuration(NovaConfig.getRandomInt(duration));

            if (wave_radius != null) {
                move.setWaveRadius(NovaConfig.getRandomInt(wave_radius));
            }

            if (wave_num != null) {
                move.setWaveNum(NovaConfig.getRandomInt(wave_num));
            }
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
        if (wave_radius != null) {
            final int size = wave_radius.size();
            for (int i = 0; i < size; i++) {
                wave_radius.set(i, Math.round(wave_radius.get(i) * scale));
            }
        }
    }
}
