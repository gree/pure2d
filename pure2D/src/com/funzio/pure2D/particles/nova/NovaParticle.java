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

        // clean up first
        if (mAnimator != null) {
            // stop it
            mAnimator.stop();
            removeManipulator(mAnimator);

            // release to pool
            mNovaEmitter.mFactory.releaseAnimator(mAnimator);

            // flag
            mAnimator = null;
        }

        // set
        setEmitter(mNovaEmitter = (NovaEmitter) params[0]);
        mParticleVO = (ParticleVO) params[1];

        // init the particle
        mPosition = mNovaEmitter.getNextPosition(mPosition);
        // add offsets
        mPosition.x += NovaConfig.getRandomInt(mParticleVO.x);
        mPosition.y += NovaConfig.getRandomInt(mParticleVO.y);
        mScale.x = mScale.y = 1;
        mRotation = 0;
        mAlpha = 1;
        mColor = null;
        // frames
        setAtlasFrameSet(mNovaEmitter.mFactory.getFrameSet(NovaConfig.getRandomString(mParticleVO.sprite)));
        if (getAtlasFrameSet() == null) {
            // just a box
            setSize(50, 50);
        } else {
            playAt(Math.min(NovaConfig.getRandomInt(mParticleVO.start_frame), getNumFrames() - 1));
        }

        // origin
        if (mParticleVO.hasOriginAtCenter()) {
            setOriginAtCenter();
        } else {
            setOrigin(mParticleVO.origin_x, mParticleVO.origin_y);
        }

        // and others
        setBlendFunc(NovaConfig.getBlendFunc(NovaConfig.getRandomString(mParticleVO.blend_mode)));
        // setZ(particleVO.z);
        // setAlphaTestEnabled(particleVO.z != 0);

        // optional animators
        if (mParticleVO.animator != null) {
            // get a new animator from pool
            mAnimator = mNovaEmitter.mFactory.createAnimator(this, NovaConfig.getRandomString(mParticleVO.animator));

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
