/**
 * 
 */
package com.funzio.crimecity.particles.gunshots;

import android.graphics.PointF;

import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.SpriteParticle;

/**
 * @author long
 */
public class GunshotSpotParticle extends SpriteParticle {
    private static final int SIZE = 8;
    private static final int TOTAL_FRAMES = 75;

    private int mFrame = 0;

    public GunshotSpotParticle(final Texture texture) {
        super();

        setTexture(texture);
        setSize(SIZE, SIZE);
        setBlendFunc(ParticleAdapter.BF_ADD);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.SpriteParticle#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {

        mFrame += ParticleAdapter.FRAME_THROTTLE ? 2 : 1;
        if (mFrame < TOTAL_FRAMES) {
            if (mEmitter != null) {
                // randomly jump
                final PointF start = mEmitter.getPosition();
                setPosition(start.x + mRandom.nextInt(101) - 50, start.y + mRandom.nextInt(101) - 50);
            }
        } else {
            finish();
        }

        return true;
    }
}
