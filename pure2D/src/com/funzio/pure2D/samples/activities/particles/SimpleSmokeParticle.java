/**
 * 
 */
package com.funzio.pure2D.samples.activities.particles;

import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.SpriteParticle;

/**
 * @author long
 */
public class SimpleSmokeParticle extends SpriteParticle {
    // private static final BlendFunc BLEND_FUNC = new BlendFunc(GL10.GL_SRC_COLOR, GL10.GL_ONE_MINUS_SRC_ALPHA);

    public SimpleSmokeParticle() {
        super();
        // setBlendFunc(BLEND_FUNC);
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

}
