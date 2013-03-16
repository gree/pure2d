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

        dx = NovaVO.getListInteger(json.optJSONArray("dx"));
        dy = NovaVO.getListInteger(json.optJSONArray("dy"));
        wave_radius = NovaVO.getListInteger(json.optJSONArray("wave_radius"));
        wave_num = NovaVO.getListInteger(json.optJSONArray("wave_num"));
    }

    @Override
    public Animator createAnimator(final Manipulatable target, final Animator... animators) {
        return init(target, new SinWaveAnimator(NovaConfig.getInterpolator(interpolator)));
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
}
