/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.effects.trails.MotionTrail;
import com.funzio.pure2D.gl.gl10.BlendModes;
import com.funzio.pure2D.particles.ClipParticle;
import com.funzio.pure2D.particles.nova.vo.NovaParticleVO;

/**
 * @author long
 */
public class NovaParticle extends ClipParticle implements Animator.AnimatorListener {
    private NovaParticleVO mParticleVO;
    private Animator mAnimator;
    private MotionTrail mMotionTrail;
    private NovaEmitter mNovaEmitter;

    public NovaParticle(final NovaEmitter emitter, final NovaParticleVO particleVO, final int emitIndex) {
        super();

        reset(emitter, particleVO, emitIndex);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.ClipParticle#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        super.reset(params);

        // clean up animator
        // if (mAnimator != null) {
        // // stop it
        // mAnimator.stop();
        // removeManipulator(mAnimator);
        //
        // // release to pool
        // mNovaEmitter.mFactory.releaseAnimator(mAnimator);
        // // flag
        // mAnimator = null;
        // }

        // clean up trail
        // if (mMotionTrail != null) {
        // mNovaEmitter.mFactory.releaseMotionTrail(mMotionTrail);
        // mMotionTrail = null;
        // }

        // set properties
        setEmitter(mNovaEmitter = (NovaEmitter) params[0]);
        mParticleVO = (NovaParticleVO) params[1];
        int emitIndex = (Integer) params[2];

        // init the particle
        mPosition = mNovaEmitter.getNextPosition(mPosition);
        // add offsets
        mPosition.x += NovaConfig.getInt(mParticleVO.x, emitIndex, 0);
        mPosition.y += NovaConfig.getInt(mParticleVO.y, emitIndex, 0);
        mZ = NovaConfig.getFloat(mParticleVO.z, emitIndex, 0);
        mAlphaTestEnabled = mZ > 0;
        mScale.x = NovaConfig.getFloat(mParticleVO.scale_x, emitIndex, 1);
        mScale.y = NovaConfig.getFloat(mParticleVO.scale_y, emitIndex, 1);
        setSkew(NovaConfig.getFloat(mParticleVO.skew_x, emitIndex, 0), NovaConfig.getFloat(mParticleVO.skew_y, emitIndex, 0));
        mRotation = NovaConfig.getFloat(mParticleVO.rotation, emitIndex, 0);
        mAlpha = NovaConfig.getFloat(mParticleVO.alpha, emitIndex, 1);
        mColor = NovaConfig.getColor(mParticleVO.color, emitIndex, null);
        mBlendFunc = BlendModes.getBlendFunc(NovaConfig.getString(mParticleVO.blend_mode, emitIndex));
        mTexture = null;

        // now, find optional animator
        if (mParticleVO.animator != null && !mParticleVO.animator.isEmpty()) {
            // get a new animator from pool
            mAnimator = mNovaEmitter.mFactory.createAnimator(this, mNovaEmitter.mFactory.mNovaVO.getAnimatorVO(NovaConfig.getString(mParticleVO.animator, emitIndex)), emitIndex);

            if (mAnimator != null) {
                // now, find optional trail
                if (mParticleVO.motion_trail != null && !mParticleVO.motion_trail.isEmpty()) {
                    // get a new trail from pool
                    mMotionTrail = mNovaEmitter.mFactory.createMotionTrail(emitIndex, this, mNovaEmitter.mFactory.mNovaVO.getMotionTrailVO(NovaConfig.getString(mParticleVO.motion_trail, emitIndex)));
                }

                // add it
                addManipulator(mAnimator);
            }
        }

        // delegate something to this particle, such as AtlasFrameSet
        if (mNovaEmitter.mFactory.mNovaDelegator != null) {
            mNovaEmitter.mFactory.mNovaDelegator.delegateParticle(this, mNovaEmitter.mParams);
        }

        if (getAtlasFrameSet() != null) {
            playAt(Math.min(NovaConfig.getInt(mParticleVO.start_frame, emitIndex, 0), getNumFrames() - 1));
            if (mParticleVO.loop_mode != null) {
                setLoop(NovaConfig.getLoopMode(NovaConfig.getString(mParticleVO.loop_mode, emitIndex)));
            } else {
                setLoop(LOOP_REPEAT); // repeat by default
            }
        } else if (mTexture == null) {
            // just a dummy box
            setSize(50, 50);
        }

        // origin based on the frames
        if (mParticleVO.hasOriginAtCenter()) {
            setOriginAtCenter();
        } else {
            setOrigin(mParticleVO.origin_x, mParticleVO.origin_y);
        }

        // check and start animator, Go!
        if (mAnimator != null) {
            mAnimator.setListener(this);

            // auto start
            mAnimator.start();
        }

    }

    public NovaParticleVO getParticleVO() {
        return mParticleVO;
    }

    public Animator getAnimator() {
        return mAnimator;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#onAdded(com.funzio.pure2D.containers.Container)
     */
    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        // also add trail if there is any
        if (mMotionTrail != null) {
            // add below this object
            parent.addChild(mMotionTrail, parent.getChildIndex(this));
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onRemoved()
     */
    @Override
    public void onRemoved() {
        // clean up animator
        if (mAnimator != null) {
            removeManipulator(mAnimator);
            // release to pool
            mNovaEmitter.mFactory.releaseAnimator(mAnimator);
            // flag
            mAnimator = null;
        }

        // also remove trail if there is any
        if (mMotionTrail != null) {
            mMotionTrail.removeFromParent();
            // release
            mNovaEmitter.mFactory.releaseMotionTrail(mMotionTrail);
            // flag
            mMotionTrail = null;
        }

        super.onRemoved();
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        // auto remove
        finish();
    }

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        if (mMotionTrail != null && mMotionTrail.getTarget() == null) {
            // initial position for trail
            mMotionTrail.setTarget(this);
        }
    }

}
