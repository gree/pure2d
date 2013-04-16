/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.PointF;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.particles.nova.vo.AnimatorVO;
import com.funzio.pure2D.particles.nova.vo.EmitterVO;
import com.funzio.pure2D.particles.nova.vo.GroupAnimatorVO;
import com.funzio.pure2D.particles.nova.vo.NovaVO;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;
import com.funzio.pure2D.utils.ObjectPool;

/**
 * @author long
 */
public class NovaFactory {
    protected static final String TAG = NovaFactory.class.getSimpleName();

    protected NovaVO mNovaVO;
    protected SpriteDelegator mSpriteDelegator;
    // pools
    private int mPoolSize = 0;
    protected ObjectPool<NovaParticle> mParticlePool;
    protected Map<String, ObjectPool<Animator>> mAnimatorPools;

    public NovaFactory(final NovaVO novaVO, final SpriteDelegator spriteDelegator) {
        this(novaVO, spriteDelegator, novaVO.particle_pool_size);
    }

    public NovaFactory(final NovaVO novaVO, final SpriteDelegator spriteDelegator, final int poolSize) {
        mNovaVO = novaVO;
        mSpriteDelegator = spriteDelegator;

        // pool is optional
        if (poolSize > 0) {
            mPoolSize = poolSize;
            mParticlePool = new ObjectPool<NovaParticle>(poolSize);
            mAnimatorPools = new HashMap<String, ObjectPool<Animator>>();
        }
    }

    public NovaVO getNovaVO() {
        return mNovaVO;
    }

    /**
     * Create a list of Emitters with an initial position
     * 
     * @param position
     * @return
     */
    public List<NovaEmitter> createEmitters(final PointF position) {
        final int size = mNovaVO.emitters.size();
        final List<NovaEmitter> emitters = new ArrayList<NovaEmitter>();
        EmitterVO vo;
        for (int i = 0; i < size; i++) {
            vo = mNovaVO.emitters.get(i);
            for (int n = 0; n < vo.quantity; n++) {
                emitters.add(createEmitter(vo, position));
            }
        }

        return emitters;
    }

    /**
     * Create a emitter from a key with an initial position
     * 
     * @param name
     * @param position
     * @return
     */
    public NovaEmitter createEmitter(final String name, final PointF position) {
        final EmitterVO vo = mNovaVO.getEmitterVO(name);
        return vo == null ? null : createEmitter(vo, position);
    }

    protected NovaEmitter createEmitter(final EmitterVO emitterVO, final PointF pos) {
        // TODO use pool
        return new NovaEmitter(this, emitterVO, pos);
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
     * @param animationName
     * @return
     */
    public Animator createAnimator(final Manipulatable target, final String animationName) {
        AnimatorVO vo = mNovaVO.getAnimatorVO(animationName);
        // null check
        if (vo == null) {
            return null;
        }

        // check the pools
        if (mAnimatorPools != null) {
            ObjectPool<Animator> pool = mAnimatorPools.get(animationName);
            if (pool == null) {
                // no pool created yet, create one
                pool = new ObjectPool<Animator>(mPoolSize);
                mAnimatorPools.put(animationName, pool);
            } else {
                // there is a pool, try to acquire
                final Animator animator = pool.acquire();
                if (animator != null) {
                    // awesome, there is something, reset it!
                    vo.resetAnimator(target, animator);
                    // and return
                    return animator;
                }
            }
        }

        return createAnimatorInstance(target, vo);
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

    protected Animator createAnimatorInstance(final Manipulatable target, final AnimatorVO animatorVO) {
        // null check
        if (animatorVO == null) {
            return null;
        }

        if (animatorVO instanceof GroupAnimatorVO) {
            // group
            final GroupAnimatorVO groupVO = (GroupAnimatorVO) animatorVO;
            // create the child animators
            return groupVO.createAnimator(target, createChildAnimators(target, groupVO.animators));
        } else {
            return animatorVO.createAnimator(target);
        }
    }

    /**
     * Create multiple animators
     * 
     * @param vos
     * @return
     */
    protected Animator[] createChildAnimators(final Manipulatable target, final List<AnimatorVO> vos) {
        // null check
        if (vos == null) {
            return null;
        }

        final int size = vos.size();
        Animator[] animators = new Animator[size];
        for (int i = 0; i < size; i++) {
            animators[i] = createAnimatorInstance(target, vos.get(i));
        }

        return animators;
    }

    public SpriteDelegator getSpriteDelegator() {
        return mSpriteDelegator;
    }

    public void setSpriteDeletator(final SpriteDelegator frameMapper) {
        mSpriteDelegator = frameMapper;
    }

    public AtlasFrameSet getFrameSet(final String sprite) {
        return mSpriteDelegator == null ? null : mSpriteDelegator.getFrameSet(sprite);
    }

    public static interface SpriteDelegator {
        public AtlasFrameSet getFrameSet(String name);
    }

}
