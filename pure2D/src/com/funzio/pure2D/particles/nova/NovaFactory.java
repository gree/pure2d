/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import java.util.List;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.MoveAnimator;
import com.funzio.pure2D.animators.ParallelAnimator;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.animators.SequenceAnimator;
import com.funzio.pure2D.particles.nova.vo.AnimatorVO;
import com.funzio.pure2D.particles.nova.vo.EmitterVO;
import com.funzio.pure2D.particles.nova.vo.MoveAnimatorVO;
import com.funzio.pure2D.particles.nova.vo.NovaVO;
import com.funzio.pure2D.particles.nova.vo.ParallelAnimatorVO;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;
import com.funzio.pure2D.particles.nova.vo.RotateAnimatorVO;
import com.funzio.pure2D.particles.nova.vo.SequenceAnimatorVO;

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

    public NovaParticle createParticle(final NovaEmitter emitter, final ParticleVO particleVO) {
        // TODO use pool
        final NovaParticle particle = new NovaParticle(emitter, particleVO);
        particle.setPosition(emitter.getNextPosition(particle.getPosition()));
        return particle;
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
        } else if (animatorVO instanceof SequenceAnimatorVO) {
            SequenceAnimatorVO vo = (SequenceAnimatorVO) animatorVO;
            return new SequenceAnimator(createAnimators(vo.animators));
        } else if (animatorVO instanceof ParallelAnimatorVO) {
            ParallelAnimatorVO vo = (ParallelAnimatorVO) animatorVO;
            return new ParallelAnimator(createAnimators(vo.animators));
        }

        return null;
    }

    public Animator[] createAnimators(final List<AnimatorVO> vos) {
        // null check
        if (vos == null) {
            return null;
        }

        final int size = vos.size();
        Animator[] animators = new Animator[size];
        for (int i = 0; i < size; i++) {
            animators[i] = createAnimator(vos.get(i).name);
        }

        return animators;
    }

}
