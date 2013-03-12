/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import android.view.animation.Interpolator;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.MoveAnimator;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.particles.ClipParticle;
import com.funzio.pure2D.particles.nova.vo.AnimatorVO;
import com.funzio.pure2D.particles.nova.vo.EmitterVO;
import com.funzio.pure2D.particles.nova.vo.MoveAnimatorVO;
import com.funzio.pure2D.particles.nova.vo.NovaVO;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;
import com.funzio.pure2D.particles.nova.vo.RotateAnimatorVO;

/**
 * @author long
 */
public class NovaFactory {
    protected NovaVO mNovaVO;

    public NovaFactory(final NovaVO novaVO) {
        mNovaVO = novaVO;
    }

    public NovaEmitter[] createEmitters() {
        final int size = mNovaVO.emitters.size();
        final NovaEmitter[] emitters = new NovaEmitter[size];
        for (int i = 0; i < size; i++) {
            emitters[i] = createEmitter(mNovaVO.emitters.get(i));
        }

        return emitters;
    }

    public NovaEmitter createEmitter(final EmitterVO emitterVO) {
        return new NovaEmitter(this, emitterVO);
    }

    public ClipParticle createParticle(final ParticleVO particleVO) {
        // TODO

        return null;
    }

    public Animator createAnimator(final AnimatorVO animatorVO) {
        if (animatorVO instanceof MoveAnimatorVO) {
            MoveAnimatorVO vo = (MoveAnimatorVO) animatorVO;
            return new MoveAnimator(createInterpolator(vo.interpolator));
        } else if (animatorVO instanceof RotateAnimatorVO) {
            RotateAnimatorVO vo = (RotateAnimatorVO) animatorVO;
            return new RotateAnimator(createInterpolator(vo.interpolator));
        }

        return null;
    }

    public static Interpolator createInterpolator(final String name) {
        if ("accelerate".equalsIgnoreCase(name)) {
            return NovaConfig.INTER_ACCELARATION;
        } else if ("decelerate".equalsIgnoreCase(name)) {
            return NovaConfig.INTER_DECELERATION;
        } else if ("bounce".equalsIgnoreCase(name)) {
            return NovaConfig.INTER_BOUNCE;
        }

        return null;
    }

}
