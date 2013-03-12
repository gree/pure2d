/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Manipulator;
import com.funzio.pure2D.animators.Timeline;
import com.funzio.pure2D.animators.Timeline.Action;
import com.funzio.pure2D.particles.ClipParticle;
import com.funzio.pure2D.particles.HybridEmitter;
import com.funzio.pure2D.particles.nova.vo.EmitterVO;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;

/**
 * @author long
 */
public class NovaEmitter extends HybridEmitter {

    protected EmitterVO mEmitterVO;
    protected Timeline mTimeline = new Timeline();
    protected NovaFactory mFactory;

    public NovaEmitter(final NovaFactory factory, final EmitterVO vo) {
        super();

        mFactory = factory;
        mEmitterVO = vo;

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
        // emitter animators
        int size = mEmitterVO.animators.size();
        for (int i = 0; i < size; i++) {
            final Animator animator = mFactory.createAnimator(mEmitterVO.animators.get(i));
            addManipulator(animator);
            // auto start
            animator.start();
        }

        // action for particles
        size = mEmitterVO.particles.size();
        for (int i = 0; i < size; i++) {
            mTimeline.addAction(new EmitAction(mEmitterVO.particles.get(i)));
        }
        // timeline for particles
        addManipulator(mTimeline);
        // auto start
        mTimeline.start();
    }

    private class EmitAction extends Action {
        private ParticleVO mParticleVO;

        public EmitAction(final ParticleVO vo) {
            super(vo.start_delay, vo.step_delay);

            mParticleVO = vo;
        }

        @Override
        public void run() {
            queueEvent(new Runnable() {

                @Override
                public void run() {
                    // null check
                    if (mParent != null) {
                        // emit the particles
                        for (int n = 0; n < mParticleVO.num_per_step; n++) {
                            final ClipParticle particle = mFactory.createParticle(mParticleVO);
                            particle.setPosition(mPosition);
                            mParent.addChild(particle);
                        }
                    }
                }
            });

        }
    }
}
