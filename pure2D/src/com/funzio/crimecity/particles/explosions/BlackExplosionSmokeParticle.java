/**
 * 
 */
package com.funzio.crimecity.particles.explosions;

import javax.microedition.khronos.opengles.GL10;

import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public class BlackExplosionSmokeParticle extends ExplosionParticle {

    private static final BlendFunc BLEND_FUNC = new BlendFunc(GL10.GL_ZERO, GL10.GL_ONE_MINUS_SRC_ALPHA);

    public BlackExplosionSmokeParticle(final Texture texture) {
        super(texture);

        setBlendFunc(BLEND_FUNC);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.ExplosionParticle#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mLifeStage == 0) {
            mVelocity.x -= .03;
            mVelocity.y += .03;
        } else if (mLifeStage == 1) {
            mVelocity.x += .03;
            mVelocity.y += .03;
            mDeltaScale.x *= .85;
            mDeltaScale.y *= .85;
        } else if (mLifeStage == 2) {
            if (mScale.x <= 0) {
                mVisible = false;
            } else {
                mAlpha -= .04;
                mVelocity.x += .03;
                mVelocity.y += .03;
                mDeltaScale.x -= .005;
                mDeltaScale.y -= .005;
            }
        }

        return super.update(deltaTime);
    }

}
