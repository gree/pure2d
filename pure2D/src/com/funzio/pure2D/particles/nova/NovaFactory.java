/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.gl.gl10.BlendFunc;
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
    protected FrameMapper mFrameMapper;
    // pools
    private int mPoolLimit = 0;
    protected ObjectPool<NovaParticle> mParticlePool;
    protected Map<String, ObjectPool<Animator>> mAnimatorPools;

    public NovaFactory(final NovaVO novaVO, final FrameMapper frameMapper, final int poolLimit) {
        mNovaVO = novaVO;
        mFrameMapper = frameMapper;

        // pool is optional
        if (poolLimit > 0) {
            mPoolLimit = poolLimit;
            mParticlePool = new ObjectPool<NovaParticle>(poolLimit);
            mAnimatorPools = new HashMap<String, ObjectPool<Animator>>();
        }
    }

    public NovaEmitter[] createEmitters() {
        final int size = mNovaVO.emitters.size();
        final NovaEmitter[] emitters = new NovaEmitter[size];
        for (int i = 0; i < size; i++) {
            emitters[i] = createEmitter(mNovaVO.emitters.get(i));
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
            particle = new NovaParticle(emitter, particleVO);
        } else {
            particle.reset(emitter, particleVO);
        }

        particle.setPosition(emitter.getNextPosition(particle.getPosition()));
        particle.setAtlasFrameSet(mFrameMapper.getFrameSet(NovaConfig.getRandomString(particleVO.sprites)));
        particle.setOriginAtCenter();
        // particle.setZ(particleVO.z);
        // particle.setAlphaTestEnabled(particleVO.z != 0);

        BlendFunc bf = NovaConfig.getBlendFunc(particleVO.blend_mode);
        if (bf != null) {
            particle.setBlendFunc(bf);
        } else {
            particle.setBlendFunc(null);
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

    protected void releaseAnimator(final String name, final Animator animator) {
        if (mAnimatorPools != null) {
            ObjectPool<Animator> pool = mAnimatorPools.get(name);
            if (pool != null) {
                pool.release(animator);
            }
        }
    }

    protected Animator createAnimatorInstance(final AnimatorVO animatorVO) {
        if (animatorVO instanceof TweenAnimatorVO) {
            return ((TweenAnimatorVO) animatorVO).createAnimator();
        } else if (animatorVO instanceof GroupAnimatorVO) {
            return ((GroupAnimatorVO) animatorVO).createAnimator(createAnimators(((GroupAnimatorVO) animatorVO).animators));
        }

        return null;
    }

    /**
     * Create multiple animators
     * 
     * @param vos
     * @return
     */
    protected Animator[] createAnimators(final List<AnimatorVO> vos) {
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

    public FrameMapper getFrameMapper() {
        return mFrameMapper;
    }

    public void setFrameMapper(final FrameMapper frameMapper) {
        mFrameMapper = frameMapper;
    }

    public static interface FrameMapper {
        public AtlasFrameSet getFrameSet(String name);
    }

}
