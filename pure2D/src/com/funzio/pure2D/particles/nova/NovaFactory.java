/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import java.util.List;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.particles.nova.vo.AnimatorVO;
import com.funzio.pure2D.particles.nova.vo.EmitterVO;
import com.funzio.pure2D.particles.nova.vo.GroupAnimatorVO;
import com.funzio.pure2D.particles.nova.vo.NovaVO;
import com.funzio.pure2D.particles.nova.vo.ParticleVO;
import com.funzio.pure2D.particles.nova.vo.TweenAnimatorVO;

/**
 * @author long
 */
public class NovaFactory {
    protected static final String TAG = NovaFactory.class.getSimpleName();

    protected NovaVO mNovaVO;
    protected FrameMapper mFrameMapper;

    public NovaFactory(final NovaVO novaVO, final FrameMapper frameMapper) {
        mNovaVO = novaVO;
        mFrameMapper = frameMapper;
    }

    public NovaEmitter[] createEmitters() {
        final int size = mNovaVO.emitters.size();
        final NovaEmitter[] emitters = new NovaEmitter[size];
        for (int i = 0; i < size; i++) {
            emitters[i] = createEmitter(mNovaVO.emitters.get(i));
        }

        return emitters;
    }

    public NovaEmitter createEmitter(final EmitterVO emitterVO) {
        // TODO use pool
        return new NovaEmitter(this, emitterVO);
    }

    public NovaParticle createParticle(final NovaEmitter emitter, final ParticleVO particleVO) {
        // TODO use pool
        final NovaParticle particle = new NovaParticle(emitter, particleVO);
        particle.setPosition(emitter.getNextPosition(particle.getPosition()));
        particle.setAtlasFrameSet(mFrameMapper.getFrameSet(particleVO.sprite));
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
     * Create an animator by using name as a key
     * 
     * @param name
     * @return
     */
    public Animator createAnimator(final String name) {
        return createAnimator(mNovaVO.getAnimatorVO(name));
    }

    protected Animator createAnimator(final AnimatorVO animatorVO) {
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
            animators[i] = createAnimator(vos.get(i));
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
