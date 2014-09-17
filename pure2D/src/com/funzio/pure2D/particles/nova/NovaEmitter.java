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

import android.graphics.PointF;
import android.util.SparseArray;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Timeline;
import com.funzio.pure2D.animators.Timeline.Action;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.effects.trails.MotionTrail;
import com.funzio.pure2D.particles.Particle;
import com.funzio.pure2D.particles.RectangularEmitter;
import com.funzio.pure2D.particles.nova.vo.AnimatorVO;
import com.funzio.pure2D.particles.nova.vo.NovaEmitterVO;
import com.funzio.pure2D.particles.nova.vo.NovaParticleVO;
import com.funzio.pure2D.utils.Reusable;

/**
 * @author long
 */
public class NovaEmitter extends RectangularEmitter implements Reusable, Timeline.Listener, Animator.AnimatorListener {

    protected final Timeline mTimeline;
    protected final NovaFactory mFactory;

    protected NovaEmitterVO mEmitterVO;
    protected Object[] mParams;
    protected Animator mAnimator;
    protected MotionTrail mMotionTrail;

    // layers for particles
    protected SparseArray<DisplayGroup> mLayers;

    public NovaEmitter(final NovaFactory factory, final NovaEmitterVO vo, final PointF pos, final Object... params) {
        super();

        mFactory = factory;
        mEmitterVO = vo;
        mParams = params;
        // auto remove
        mRemoveOnFinish = true;

        // main timeline
        mTimeline = new Timeline(mEmitterVO.lifespan, this);

        // define the area size
        setSize(vo.width, vo.height);
        setOriginAtCenter();
        // initial position
        if (pos != null) {
            mPosition.x = pos.x;
            mPosition.y = pos.y;
        }
        // offset position
        mPosition.x += vo.x;
        mPosition.y += vo.y;

        createManipulators();
        createLayers();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.utils.Reusable#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        // stop and reset timeline
        mTimeline.reset();

        // stop animator
        if (mAnimator != null) {
            // stop animator first
            mAnimator.stop();
            // reset the animator
            if (mAnimator.getData() instanceof AnimatorVO) {
                ((AnimatorVO) mAnimator.getData()).resetAnimator(-1, this, mAnimator);
            }
        }
    }

    public NovaEmitterVO getEmitterVO() {
        return mEmitterVO;
    }

    public Animator getAnimator() {
        return mAnimator;
    }

    public Timeline getTimeline() {
        return mTimeline;
    }

    protected void createManipulators() {
        // emitting action for particles
        int size = mEmitterVO.particles.size();
        for (int i = 0; i < size; i++) {
            mTimeline.addAction(new EmitAction(mEmitterVO.particles.get(i)));
        }
        // add timeline
        addManipulator(mTimeline);
        // auto start
        mTimeline.start();

        // optional emitter animator
        if (mEmitterVO.animator != null && !"".equals(mEmitterVO.animator)) {
            mAnimator = mFactory.createAnimator(this, mFactory.mNovaVO.getAnimatorVO(mEmitterVO.animator), -1);

            if (mAnimator != null) {
                // only create trail when there is an animator
                if (mEmitterVO.motion_trail != null) {
                    // get a new trail from pool
                    mMotionTrail = mFactory.createMotionTrail(-1, this, mFactory.mNovaVO.getMotionTrailVO(mEmitterVO.motion_trail));
                }

                // add animator
                addManipulator(mAnimator);
            }
        }

        // delegate something
        if (mFactory.mNovaDelegator != null) {
            mFactory.mNovaDelegator.delegateEmitter(this, mParams);
        }

        // check and start animator, Go!
        if (mAnimator != null) {
            // only listen if there is motion trail
            if (mMotionTrail != null) {
                mAnimator.setListener(this);
            }

            // auto start
            mAnimator.start();
        }
    }

    protected void createLayers() {
        int size = mEmitterVO.particles.size();
        for (int i = 0; i < size; i++) {
            final NovaParticleVO particle = mEmitterVO.particles.get(i);
            if (particle.layer > 0) {
                if (mLayers == null) {
                    mLayers = new SparseArray<DisplayGroup>();
                }
                mLayers.put(particle.layer, new DisplayGroup());
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.RectangularEmitter#onParticleFinish(com.funzio.pure2D.particles.Particle)
     */
    @Override
    public void onParticleFinish(final Particle particle) {
        particle.queueEvent(new Runnable() {

            @Override
            public void run() {
                // auto remove
                particle.removeFromParent();

                // add to pool
                if (mFactory.mParticlePool != null) {
                    mFactory.mParticlePool.release((NovaParticle) particle);
                }
            }
        });
    }

    /**
     * Check and remove motion trail
     */
    private void removeMotionTrail() {
        if (mMotionTrail != null) {
            mMotionTrail.removeFromParent();
            // release it
            mFactory.releaseMotionTrail(mMotionTrail);
            // flag
            mMotionTrail = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onAdded(com.funzio.pure2D.containers.Container)
     */
    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        // add the layers
        if (mLayers != null) {
            final int size = mLayers.size();
            for (int i = 0; i < size; i++) {
                parent.addChild(mLayers.get(mLayers.keyAt(i)));
            }
        }

        // also add trail if there is any
        if (mMotionTrail != null) {
            // add below this object
            parent.addChild(mMotionTrail, parent.getChildIndex(this));
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.RectangularEmitter#onRemoved()
     */
    @Override
    public void onRemoved() {
        // remove animator
        if (mAnimator != null) {
            removeManipulator(mAnimator);
            // release it
            mFactory.releaseAnimator(mAnimator);
            mAnimator = null;
        }

        // remove the layers
        if (mLayers != null) {
            final int size = mLayers.size();
            for (int i = 0; i < size; i++) {
                mLayers.get(mLayers.keyAt(i)).removeFromParent();
            }
            mLayers.clear();
        }

        // check and remove motion trail
        removeMotionTrail();

        super.onRemoved();
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        // also remove trail if there is any because the trail looks weird by being alone.
        removeMotionTrail();
    }

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        if (mMotionTrail != null && mMotionTrail.getTarget() == null) {
            // initial position for trail
            mMotionTrail.setTarget(this);
        }
    }

    @Override
    public void onTimelineComplete(final Timeline timeline) {
        // timeline is done, finish now
        queueFinish();
    }

    /**
     * Timeline Action for emitting paricles
     * 
     * @author long
     */
    private class EmitAction extends Action {
        private NovaParticleVO mParticleVO;
        private int mEmitIndex = 0;

        public EmitAction(final NovaParticleVO vo) {
            super(vo.start_delay, vo.step_delay, vo.duration);

            mParticleVO = vo;
        }

        @Override
        public void run() {
            // mEmitter.queueEvent(new Runnable() {
            //
            // @Override
            // public void run() {
            // null check
            if (mParent != null) {
                // emit the particles
                Container layer;
                for (int n = 0; n < mParticleVO.step_quantity; n++) {
                    // find the layer
                    layer = mParticleVO.layer > 0 ? mLayers.get(mParticleVO.layer) : mParent;
                    // add to the layer
                    layer.addChild(mFactory.createParticle(NovaEmitter.this, mParticleVO, mEmitIndex++));
                }
            }
            // }
            // });

        }
    }

}
