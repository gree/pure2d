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
    private ParticleVO mParticleVO;
    private Animator mAnimator;
    private NovaEmitter mNovaEmitter;

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

        // set properties
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

        // now, find optional animator
        if (mParticleVO.animator != null && !mParticleVO.animator.isEmpty()) {
            // get a new animator from pool
            mAnimator = mNovaEmitter.mFactory.createAnimator(this, NovaConfig.getRandomString(mParticleVO.animator));
        }

        // delegate something to this particle, such as AtlasFrameSet
        if (mNovaEmitter.mFactory.mNovaDelegator != null) {
            mNovaEmitter.mFactory.mNovaDelegator.delegateParticle(this, mNovaEmitter.mParams);
        }

        if (getAtlasFrameSet() != null) {
            playAt(Math.min(NovaConfig.getRandomInt(mParticleVO.start_frame), getNumFrames() - 1));
        } else if (mTexture == null) {
            // just a box
            setSize(50, 50);
        }

        // origin based on the frames
        if (mParticleVO.hasOriginAtCenter()) {
            setOriginAtCenter();
        } else {
            setOrigin(mParticleVO.origin_x, mParticleVO.origin_y);
        }

        // and others
        setBlendFunc(NovaConfig.getBlendFunc(NovaConfig.getRandomString(mParticleVO.blend_mode)));
        // z depth
        setZ(NovaConfig.getRandomFloat(mParticleVO.z));
        setAlphaTestEnabled(getZ() > 0);

        // check and start animator, Go!
        if (mAnimator != null) {
            // add it
            mAnimator.setListener(this);
            addManipulator(mAnimator);

            // auto start
            mAnimator.start();
        }
    }

    public ParticleVO getParticleVO() {
        return mParticleVO;
    }

    public Animator getAnimator() {
        return mAnimator;
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
