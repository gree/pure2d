/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Animator.AnimatorListener;
import com.funzio.pure2D.animators.Manipulator;
import com.funzio.pure2D.animators.Timeline;
import com.funzio.pure2D.animators.Timeline.Action;
import com.funzio.pure2D.particles.HybridEmitter;
import com.funzio.pure2D.particles.nova.vo.EmitterVO;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;

/**
 * @author long
 */
public class NovaEmitter extends HybridEmitter implements AnimatorListener {

    protected EmitterVO mEmitterVO;
    protected Timeline mTimeline = new Timeline();
    protected NovaFactory mFactory;

    public NovaEmitter(final NovaFactory factory, final EmitterVO vo) {
        super();

        mFactory = factory;
        mEmitterVO = vo;

        // auto remove
        mRemoveOnFinish = true;

        createChildren();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.HybridEmitter#reset()
     */
    @Override
    public void reset() {
        super.reset();

        // stop timeline
        mTimeline.reset();

        // stop all other animators
        int size = mManipulators.size();
        for (int i = 0; i < size; i++) {
            final Manipulator manipulator = mManipulators.get(i);
            if (manipulator instanceof Animator) {
                ((Animator) manipulator).stop();
            }
        }
    }

    protected void createChildren() {
        // add lifespan to timeline
        if (mEmitterVO.lifespan > 0) {
            mTimeline.addAction(new FinishAction(this, mEmitterVO.lifespan));
        }

        // timline action for particles
        int size = mEmitterVO.particles.size();
        for (int i = 0; i < size; i++) {
            mTimeline.addAction(new EmitAction(this, mEmitterVO.particles.get(i)));
        }
        // add timeline
        addManipulator(mTimeline);
        // auto start
        mTimeline.start();

        // optional emitter animators
        if (mEmitterVO.animator != null) {
            final Animator animator = mFactory.createAnimator(mEmitterVO.animator);
            animator.setListener(this);
            addManipulator(animator);
            // auto start
            animator.start();
        }
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        if (mEmitterVO.lifespan <= 0) {
            queueFinish();
        }
    }

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        // TODO Auto-generated method stub

    }

    private static class EmitAction extends Action {
        private NovaEmitter mEmitter;
        private ParticleVO mParticleVO;

        public EmitAction(final NovaEmitter emitter, final ParticleVO vo) {
            super(vo.start_delay, vo.step_delay);

            mEmitter = emitter;
            mParticleVO = vo;
        }

        @Override
        public void run() {
            mEmitter.queueEvent(new Runnable() {

                @Override
                public void run() {
                    // null check
                    if (mEmitter.mParent != null) {
                        // emit the particles
                        for (int n = 0; n < mParticleVO.num_per_step; n++) {
                            mEmitter.mParent.addChild(mEmitter.mFactory.createParticle(mEmitter, mParticleVO));
                        }
                    }
                }
            });

        }
    }

    private static class FinishAction extends Action {
        private NovaEmitter mEmitter;

        public FinishAction(final NovaEmitter emitter, final int lifespan) {
            super(lifespan, 0);

            mEmitter = emitter;
        }

        @Override
        public void run() {
            mEmitter.queueFinish();
        }
    }
}
