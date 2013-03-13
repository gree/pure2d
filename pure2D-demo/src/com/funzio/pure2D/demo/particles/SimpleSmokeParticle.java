/**
 * 
 */
package com.funzio.pure2D.demo.particles;

import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.SpriteParticle;
import com.funzio.pure2D.utils.ObjectPool;

/**
 * @author long
 */
public class SimpleSmokeParticle extends SpriteParticle {
    private static final BlendFunc BLEND_FUNC = BlendFunc.getScreen();
    private static final ObjectPool<SimpleSmokeParticle> POOL = new ObjectPool<SimpleSmokeParticle>(1000);

    public static SimpleSmokeParticle newInstance() {
        SimpleSmokeParticle particle = POOL.acquire();
        if (particle != null) {
            particle.reset();
        } else {
            particle = new SimpleSmokeParticle();
        }

        return particle;
    }

    public static void clearPool() {
        POOL.clear();
    }

    private SimpleSmokeParticle() {
        super();
        setBlendFunc(BLEND_FUNC);
    }

    public SimpleSmokeParticle(final Texture texture) {
        super();
        setTexture(texture);
    }

    @Override
    public void reset() {
        mAlpha = 1;
        mRotation = 0;
        mScale.x = mScale.y = 1;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mAlpha < 0.1) {
            finish();
        } else {
            mVelocity.x *= 0.95;
            mVelocity.y *= 0.95;
            mScale.x *= 1.02;
            mScale.y *= 1.02;
            mAlpha *= 0.98;
            mRotation += (mVelocity.x > 0) ? -5 : 5;
        }

        return super.update(deltaTime);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onRemoved()
     */
    @Override
    public void onRemoved() {
        super.onRemoved();

        // recycle
        POOL.release(this);
    }

}
