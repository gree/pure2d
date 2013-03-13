/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.particles.ClipParticle;
import com.funzio.pure2D.particles.ParticleEmitter;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;

/**
 * @author long
 */
public class NovaParticle extends ClipParticle implements Animator.AnimatorListener {
    protected ParticleVO mParticleVO;
    protected Animator mAnimator;

    public NovaParticle(final NovaEmitter emitter, final ParticleVO particleVO) {
        super();

        reset(emitter, particleVO);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.ClipParticle#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        super.reset(params);

        mEmitter = (ParticleEmitter) params[0];
        mParticleVO = (ParticleVO) params[1];

        if (mAnimator != null) {
            mAnimator.stop();
            removeManipulator(mAnimator);
            ((NovaEmitter) mEmitter).mFactory.releaseAnimator(mParticleVO.animator, mAnimator);
            mAnimator = null;
        }

        if (mEmitter != null) {
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
