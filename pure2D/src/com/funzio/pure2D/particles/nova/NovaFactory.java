/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.particles.nova.vo.AnimatorVO;
import com.funzio.pure2D.particles.nova.vo.EmitterVO;
import com.funzio.pure2D.particles.nova.vo.GroupAnimatorVO;
import com.funzio.pure2D.particles.nova.vo.NovaVO;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;
import com.funzio.pure2D.particles.nova.vo.TweenAnimatorVO;
import com.funzio.pure2D.utils.ObjectPool;

/**
 * @author long
 */
public class NovaFactory {
    protected static final String TAG = NovaFactory.class.getSimpleName();

    protected NovaVO mNovaVO;
    protected SpriteDelegator mSpriteDelegator;
    // pools
    private int mPoolLimit = 0;
    protected ObjectPool<NovaParticle> mParticlePool;
    protected Map<String, ObjectPool<Animator>> mAnimatorPools;

    public NovaFactory(final NovaVO novaVO, final SpriteDelegator spriteDelegator, final int poolLimit) {
        mNovaVO = novaVO;
        mSpriteDelegator = spriteDelegator;

        // pool is optional
        if (poolLimit > 0) {
            mPoolLimit = poolLimit;
            mParticlePool = new ObjectPool<NovaParticle>(poolLimit);
            mAnimatorPools = new HashMap<String, ObjectPool<Animator>>();
        }
    }

    public List<NovaEmitter> createEmitters() {
        final int size = mNovaVO.emitters.size();
        final List<NovaEmitter> emitters = new ArrayList<NovaEmitter>();
        EmitterVO vo;
        for (int i = 0; i < size; i++) {
            vo = mNovaVO.emitters.get(i);
            for (int n = 0; n < vo.quantity; n++) {
                emitters.add(createEmitter(vo));
            }
        }

        return emitters;
    }

    protected NovaEmitter createEmitter(final EmitterVO emitterVO) {
        // TODO use pool
        return new NovaEmitter(this, emitterVO);
    }

    protected NovaParticle createParticle(final NovaEmitter emitter, final ParticleVO particleVO) {
        // use pool
        NovaParticle particle = null;
        if (mParticlePool != null) {
            particle = mParticlePool.acquire();
        }
        if (particle == null) {
            // new instance
            particle = new NovaParticle(emitter, particleVO);
        } else {
            // reset and reuse
            particle.reset(emitter, particleVO);
        }

        return particle;
    }

    /**
     * Clear everything!
     */
    public void dispose() {
        if (mParticlePool != null) {
            mParticlePool.clear();
            mParticlePool = null;
        }

        if (mAnimatorPools != null) {
            mAnimatorPools.clear();
            mParticlePool = null;
        }
    }

    /**
     * Create an animator by using name as a key
     * 
     * @param name
     * @return
     */
    public Animator createAnimator(final String name) {
        AnimatorVO vo = mNovaVO.getAnimatorVO(name);
        // null check
        if (vo == null) {
            return null;
        }

        // check the pools
        if (mAnimatorPools != null) {
            ObjectPool<Animator> pool = mAnimatorPools.get(name);
            if (pool == null) {
                // no pool created yet, create one
                pool = new ObjectPool<Animator>(mPoolLimit);
                mAnimatorPools.put(name, pool);
            } else {
                // there is a pool, try to acquire
                final Animator animator = pool.acquire();
                if (animator != null) {
                    // awesome, there is something, reset it!
                    vo.resetAnimator(animator);
                    // and return
                    return animator;
                }
            }
        }

        return createAnimatorInstance(vo);
    }

    protected void releaseAnimator(final Animator animator) {
        if (mAnimatorPools != null && animator.getData() instanceof AnimatorVO) {
            final AnimatorVO vo = (AnimatorVO) animator.getData();
            ObjectPool<Animator> pool = mAnimatorPools.get(vo.name);
            if (pool != null) {
                pool.release(animator);
            }
        }
    }

    protected Animator createAnimatorInstance(final AnimatorVO animatorVO) {
        if (animatorVO instanceof TweenAnimatorVO) {
            return ((TweenAnimatorVO) animatorVO).createAnimator();
        } else if (animatorVO instanceof GroupAnimatorVO) {
            // group
            final GroupAnimatorVO groupVO = (GroupAnimatorVO) animatorVO;
            // create the child animators
            return groupVO.createAnimator(createChildAnimators(groupVO.animators));
        }

        return null;
    }

    /**
     * Create multiple animators
     * 
     * @param vos
     * @return
     */
    protected Animator[] createChildAnimators(final List<AnimatorVO> vos) {
        // null check
        if (vos == null) {
            return null;
        }

        final int size = vos.size();
        Animator[] animators = new Animator[size];
        for (int i = 0; i < size; i++) {
            animators[i] = createAnimatorInstance(vos.get(i));
        }

        return animators;
    }

    public SpriteDelegator getSpriteDelegator() {
        return mSpriteDelegator;
    }

    public void setSpriteDeletator(final SpriteDelegator frameMapper) {
        mSpriteDelegator = frameMapper;
    }

    public static interface SpriteDelegator {
        public AtlasFrameSet getFrameSet(String name);
    }

}
