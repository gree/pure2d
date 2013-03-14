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
    protected NovaEmitter mNovaEmitter;

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

        setEmitter(mNovaEmitter = (NovaEmitter) params[0]);
        mParticleVO = (ParticleVO) params[1];

        if (mAnimator != null) {
            // stop it
            mAnimator.stop();
            removeManipulator(mAnimator);

            // release to pool
            mNovaEmitter.mFactory.releaseAnimator(mAnimator);

            // flag
            mAnimator = null;
        }

        if (mEmitter != null) {
            // optional animators
            if (mParticleVO.animator != null) {
                // get a new animator from pool
                mAnimator = mNovaEmitter.mFactory.createAnimator(mParticleVO.animator);

                // null check
                if (mAnimator != null) {
                    // add it
                    mAnimator.setListener(this);
                    addManipulator(mAnimator);

                    // auto start
                    mAnimator.start();
                }
            }
        }

        // init the particle
        setPosition(mNovaEmitter.getNextPosition(getPosition()));
        setAtlasFrameSet(mNovaEmitter.mFactory.mSpriteDelegator.getFrameSet(NovaConfig.getRandomString(mParticleVO.sprites)));
        setOriginAtCenter();
        setBlendFunc(NovaConfig.getBlendFunc(mParticleVO.blend_mode));
        // setZ(particleVO.z);
        // setAlphaTestEnabled(particleVO.z != 0);
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
