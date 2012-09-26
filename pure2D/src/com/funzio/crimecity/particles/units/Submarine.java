/**
 * 
 */
package com.funzio.crimecity.particles.units;

import android.graphics.PointF;

import com.funzio.crimecity.game.model.CCMapDirection;

/**
 * @author long
 */
public class Submarine extends SeaUnit {

    /**
     * @param textureKey
     * @param direction
     */
    public Submarine(final CCMapDirection direction) {
        super("Submarine_0", direction);

        setSpriteScale(1.5f);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.jobs.SeaAttacker#getFireReg()
     */
    @Override
    protected PointF getFireReg() {
        PointF p = new PointF();
        p.x = mSprite.getSize().x * mSpriteScale / 2;
        p.y = mSprite.getSize().y * mSpriteScale / 2 + 20;
        return p;
    }
}
