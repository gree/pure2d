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

    // param set 1
    public ArrayList<Integer> dx;
    public ArrayList<Integer> dy;

    // or param set 2
    public ArrayList<Integer> distance;
    public ArrayList<Integer> degree;

    // and wave params
    public ArrayList<Integer> wave_radius1;
    public ArrayList<Integer> wave_radius2;
    public ArrayList<Integer> wave_num;

    public SinWaveAnimatorVO() {
        super();
    }

    public SinWaveAnimatorVO(final JSONObject json) throws JSONException {
        super(json);

        dx = NovaVO.getListInt(json, "dx");
        dy = NovaVO.getListInt(json, "dy");
        // or
        distance = NovaVO.getListInt(json, "distance");
        degree = NovaVO.getListInt(json, "degree");

        wave_radius1 = NovaVO.getListInt(json, "wave_radius1");
        wave_radius2 = NovaVO.getListInt(json, "wave_radius2");
        wave_num = NovaVO.getListInt(json, "wave_num");
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new SinWaveAnimator(NovaConfig.getInterpolator(interpolation)));
    }

    @Override
    public void resetAnimator(final Manipulatable target, final Animator animator) {
        super.resetAnimator(target, animator);

        final SinWaveAnimator sinWave = (SinWaveAnimator) animator;
        // if (sinWave != null) {
        if (distance != null) {
            sinWave.setDistance(NovaConfig.getRandomInt(distance), NovaConfig.getRandomInt(degree));
        } else {
            sinWave.setDelta(NovaConfig.getRandomInt(dx), NovaConfig.getRandomInt(dy));
        }
        sinWave.setWaveRadius(NovaConfig.getRandomInt(wave_radius1, SinWaveAnimator.DEFAULT_RADIUS), NovaConfig.getRandomInt(wave_radius2, SinWaveAnimator.DEFAULT_RADIUS));
        sinWave.setWaveNum(NovaConfig.getRandomInt(wave_num, SinWaveAnimator.DEFAULT_WAVE_NUM));
        sinWave.setDuration(NovaConfig.getRandomInt(duration));
        // }
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

        // scale wave_radius1
        if (wave_radius1 != null) {
            final int size = wave_radius1.size();
            for (int i = 0; i < size; i++) {
                wave_radius1.set(i, Math.round(wave_radius1.get(i) * scale));
            }
        }

        // scale wave_radius2
        if (wave_radius2 != null) {
            final int size = wave_radius2.size();
            for (int i = 0; i < size; i++) {
                wave_radius2.set(i, Math.round(wave_radius2.get(i) * scale));
            }
        }
    }
}
