/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Manipulator;
import com.funzio.pure2D.particles.ClipParticle;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;

/**
 * @author long
 */
public class NovaParticle extends ClipParticle {
    protected ParticleVO mParticleVO;

    public NovaParticle(final NovaEmitter emitter, final ParticleVO particleVO) {
        super();

        mEmitter = emitter;
        mParticleVO = particleVO;

        if (emitter != null) {
            createAnimators();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.ClipParticle#reset()
     */
    @Override
    public void reset() {
        super.reset();

        // stop all other animators
        int size = mManipulators.size();
        for (int i = 0; i < size; i++) {
            final Manipulator manipulator = mManipulators.get(i);
            if (manipulator instanceof Animator) {
                ((Animator) manipulator).stop();
            }
        }
    }

    protected void createAnimators() {
        // optional animators
        if (mParticleVO.animator != null) {
            final Animator animator = ((NovaEmitter) mEmitter).mFactory.createAnimator(mParticleVO.animator);
            addManipulator(animator);
            // auto start
            animator.start();
        }
    }

}
