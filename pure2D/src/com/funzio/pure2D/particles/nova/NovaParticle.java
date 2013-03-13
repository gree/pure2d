/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.particles.ClipParticle;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;

/**
 * @author long
 */
public class NovaParticle extends ClipParticle implements Animator.AnimatorListener {
    protected ParticleVO mParticleVO;
    protected Animator mAnimator;

    public NovaParticle(final NovaEmitter emitter, final ParticleVO particleVO) {
        super();

        mParticleVO = particleVO;
        setEmitter(emitter);

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

        // stop all other animator
        if (mAnimator != null) {
            mAnimator.stop();
        }
    }

    protected void createAnimators() {
        // optional animators
        if (mParticleVO.animator != null) {
            mAnimator = ((NovaEmitter) mEmitter).mFactory.createAnimator(mParticleVO.animator);
            if (mAnimator != null) {
                mAnimator.setListener(this);
                addManipulator(mAnimator);
                // auto start
                mAnimator.start();
            }
        }
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        // auto remove
        finish();
    }

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        // TODO Auto-generated method stub

    }

}
