/**
 * 
 */
package com.funzio.pure2D.particles.nova;

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
        // TODO use pool
        return new NovaEmitter(this, emitterVO);
    }

    public ClipParticle createParticle(final NovaEmitter emitter, final ParticleVO particleVO) {
        // TODO use pool
        return new NovaParticle(emitter, particleVO);
    }

    public Animator createAnimator(final String name) {
        AnimatorVO animatorVO = mNovaVO.getAnimatorVO(name);

        // TODO use pool
        if (animatorVO instanceof MoveAnimatorVO) {
            MoveAnimatorVO vo = (MoveAnimatorVO) animatorVO;
            return new MoveAnimator(NovaConfig.getInterpolator(vo.interpolator));
        } else if (animatorVO instanceof RotateAnimatorVO) {
            RotateAnimatorVO vo = (RotateAnimatorVO) animatorVO;
            return new RotateAnimator(NovaConfig.getInterpolator(vo.interpolator));
        }

        return null;
    }

}
