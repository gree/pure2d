/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

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

    public ArrayList<Integer> dx;
    public ArrayList<Integer> dy;
    public ArrayList<Integer> radius1;
    public ArrayList<Integer> radius2;
    public ArrayList<Integer> wave_num;

    public SinWaveAnimatorVO() {
        super();
    }

    public SinWaveAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        dx = NovaVO.getListInt(json, "dx");
        dy = NovaVO.getListInt(json, "dy");
        radius1 = NovaVO.getListInt(json, "radius1");
        radius2 = NovaVO.getListInt(json, "radius2");
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
            move.setWaveRadius(NovaConfig.getRandomInt(radius1, SinWaveAnimator.DEFAULT_RADIUS), NovaConfig.getRandomInt(radius2, SinWaveAnimator.DEFAULT_RADIUS));
            move.setWaveNum(NovaConfig.getRandomInt(wave_num, SinWaveAnimator.DEFAULT_WAVE_NUM));
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
