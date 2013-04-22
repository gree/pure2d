/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.graphics.PointF;
import android.util.Log;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.effects.trails.MotionTrail;
import com.funzio.pure2D.particles.nova.vo.AnimatorVO;
import com.funzio.pure2D.particles.nova.vo.EmitterVO;
import com.funzio.pure2D.particles.nova.vo.GroupAnimatorVO;
import com.funzio.pure2D.particles.nova.vo.MotionTrailVO;
import com.funzio.pure2D.particles.nova.vo.NovaVO;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;
import com.funzio.pure2D.utils.ObjectPool;

/**
 * @author long
 */
public class NovaFactory {
    protected static final String TAG = NovaFactory.class.getSimpleName();

    protected NovaVO mNovaVO;
    protected NovaDelegator mNovaDelegator;
    // pools
    private int mPoolSize = 0;
    protected ObjectPool<NovaParticle> mParticlePool;
    protected HashMap<String, ObjectPool<Animator>> mAnimatorPools;
    protected HashMap<String, ObjectPool<MotionTrail>> mMotionTrailPools;

    public NovaFactory(final NovaVO novaVO, final NovaDelegator NovaDelegator) {
        this(novaVO, NovaDelegator, novaVO.pool_size);
    }

    public NovaFactory(final NovaVO novaVO, final NovaDelegator NovaDelegator, final int poolSize) {
        Log.v(TAG, "NovaFactory(): " + novaVO + ", " + poolSize);

        mNovaVO = novaVO;
        mNovaDelegator = NovaDelegator;

        // pool is optional
        if (poolSize > 0) {
            mPoolSize = poolSize;
            mParticlePool = new ObjectPool<NovaParticle>(poolSize);
            mAnimatorPools = new HashMap<String, ObjectPool<Animator>>();
            mMotionTrailPools = new HashMap<String, ObjectPool<MotionTrail>>();
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
    public ArrayList<NovaEmitter> createEmitters(final PointF position, final Object... params) {
        Log.v(TAG, "createEmitters(): " + params);

        final int size = mNovaVO.emitters.size();
        final ArrayList<NovaEmitter> emitters = new ArrayList<NovaEmitter>();
        EmitterVO vo;
        for (int i = 0; i < size; i++) {
            vo = mNovaVO.emitters.get(i);
            for (int n = 0; n < vo.quantity; n++) {
                emitters.add(createEmitter(vo, position, params));
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
    public NovaEmitter createEmitter(final String name, final PointF position, final Object... params) {
        Log.v(TAG, "createEmitters(): " + name + ", " + params);

        final EmitterVO vo = mNovaVO.getEmitterVO(name);
        return vo == null ? null : createEmitter(vo, position, params);
    }

    protected NovaEmitter createEmitter(final EmitterVO emitterVO, final PointF pos, final Object... params) {
        // TODO use pool
        return new NovaEmitter(this, emitterVO, pos, params);
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
     * Clear the pools. This might be useful when you need more memory.
     */
    public void clearPools() {
        Log.v(TAG, "clearPools()");

        if (mParticlePool != null) {
            mParticlePool.clear();
        }

        if (mAnimatorPools != null) {
            final Set<String> keys = mAnimatorPools.keySet();
            for (String key : keys) {
                mAnimatorPools.get(key).clear();
            }
        }

        if (mMotionTrailPools != null) {
            final Set<String> keys = mMotionTrailPools.keySet();
            for (String key : keys) {
                mMotionTrailPools.get(key).clear();
            }
        }
    }

    /**
     * Clear everything! Call when this object is no longer being used.
     */
    public void dispose() {
        Log.v(TAG, "dispose()");

        if (mParticlePool != null) {
            mParticlePool.clear();
            mParticlePool = null;
        }

        if (mAnimatorPools != null) {
            mAnimatorPools.clear();
            mAnimatorPools = null;
        }

        if (mMotionTrailPools != null) {
            mMotionTrailPools.clear();
            mMotionTrailPools = null;
        }
    }

    /**
     * Create an animator by using name as a key
     * 
     * @param animationName
     * @return
     */
    public Animator createAnimator(final Manipulatable target, final AnimatorVO vo) {
        // Log.v(TAG, "createAnimator(): " + animationName);

        // null check
        if (vo == null) {
            return null;
        }
        final String animationName = vo.name;

        // check the pools
        if (mAnimatorPools != null) {
            ObjectPool<Animator> pool = mAnimatorPools.get(animationName);
            if (pool == null) {
                // no pool created yet, create one
                pool = new ObjectPool<Animator>(mPoolSize);
                mAnimatorPools.put(animationName, pool); // use name as key, works better for GroupAnimators
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
            ObjectPool<Animator> pool = mAnimatorPools.get(vo.name); // use name as key
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
    protected Animator[] createChildAnimators(final Manipulatable target, final ArrayList<AnimatorVO> vos) {
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

    public MotionTrail createMotionTrail(final DisplayObject target, final MotionTrailVO trailVO) {
        // Log.v(TAG, "createMotionTrail(): " + trailName);

        // null check
        if (trailVO == null) {
            return null;
        }
        final String type = trailVO.type;

        // check the pools
        if (mMotionTrailPools != null) {
            ObjectPool<MotionTrail> pool = mMotionTrailPools.get(type); // use type as key
            if (pool == null) {
                // no pool created yet, create one
                pool = new ObjectPool<MotionTrail>(mPoolSize);
                mMotionTrailPools.put(type, pool);
            } else {
                // there is a pool, try to acquire
                final MotionTrail trail = pool.acquire();
                if (trail != null) {
                    // awesome, there is something, reset it!
                    trailVO.resetTrail(target, trail);
                    // and return
                    return trail;
                }
            }
        }

        return trailVO.createTrail(target);
    }

    protected void releaseMotionTrail(final MotionTrail trail) {
        if (mMotionTrailPools != null && trail.getData() instanceof MotionTrailVO) {
            final MotionTrailVO vo = (MotionTrailVO) trail.getData();
            ObjectPool<MotionTrail> pool = mMotionTrailPools.get(vo.type); // use type as key
            if (pool != null) {
                pool.release(trail);
            }
        }
    }

    public NovaDelegator getNovaDelegator() {
        return mNovaDelegator;
    }

    public void setNovaDeletator(final NovaDelegator delegator) {
        mNovaDelegator = delegator;
    }

}
