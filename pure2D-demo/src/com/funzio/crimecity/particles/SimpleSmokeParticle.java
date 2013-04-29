/**
 * 
 */
package com.funzio.crimecity.particles;

import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.SpriteParticle;

/**
 * @author long
 */
public class SimpleSmokeParticle extends SpriteParticle {

    public SimpleSmokeParticle() {
        super();
    }

    public SimpleSmokeParticle(final Texture texture) {
        super();
        setTexture(texture);
    }

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
        super.update(deltaTime);

        if (mAlpha < 0.1) {
            finish();
        } else {
            mVelocity.x *= 0.95;
            mVelocity.y *= 0.95;
            mPosition.x += mVelocity.x;
            mPosition.y += mVelocity.y;
            mScale.x *= 1.02;
            mScale.y *= 1.02;
            mAlpha *= 0.98;
            mRotation += (mVelocity.x > 0) ? -5 : 5;
            invalidate();
        }

        return true;
    }

}
