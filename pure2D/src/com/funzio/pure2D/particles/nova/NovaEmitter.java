/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Animator.AnimatorListener;
import com.funzio.pure2D.animators.Manipulator;
import com.funzio.pure2D.animators.Timeline;
import com.funzio.pure2D.animators.Timeline.Action;
import com.funzio.pure2D.particles.RectangularEmitter;
import com.funzio.pure2D.particles.nova.vo.EmitterVO;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;
import com.funzio.pure2D.utils.Reusable;

/**
 * @author long
 */
public class NovaEmitter extends RectangularEmitter implements AnimatorListener, Reusable {

    protected EmitterVO mEmitterVO;
    protected Timeline mTimeline = new Timeline();
    protected NovaFactory mFactory;

    public NovaEmitter(final NovaFactory factory, final EmitterVO vo) {
        super();

        mFactory = factory;
        mEmitterVO = vo;

        // auto remove
        mRemoveOnFinish = true;

        // define the area size
        setSize(vo.width, vo.height);

        createAnimators();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.HybridEmitter#reset()
     */
    @Override
    public void reset() {
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

    protected void createAnimators() {
        // add lifespan to timeline if there is
        if (mEmitterVO.lifespan > 0) {
            mTimeline.addAction(new FinishAction(this, mEmitterVO.lifespan));
        }

        // emitting action for particles
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
        // only finish when lifespan is not set
        if (mEmitterVO.lifespan <= 0) {
            queueFinish();
        }
    }

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        // TODO Auto-generated method stub

    }

    /**
     * Timeline Action for emitting paricles
     * 
     * @author long
     */
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

    /**
     * Timeline Action for finishing this emitter
     * 
     * @author long
     */
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
