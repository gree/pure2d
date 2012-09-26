/**
 * 
 */
package com.funzio.crimecity.particles.units;

import android.graphics.PointF;

import com.funzio.crimecity.game.model.CCMapDirection;

/**
 * @author long
 */
public class Frigate extends SeaUnit {

    /**
     * @param textureKey
     * @param direction
     */
    public Frigate(final CCMapDirection direction) {
        super("Frigate_0", direction);

        setSpriteScale(2f);
    }

    @Override
    protected PointF getFireReg() {
        PointF p = super.getFireReg();

        if (mDirection.equals(CCMapDirection.SOUTHWEST)) {
            p.x += 25 * mSpriteScale;
            p.y += 35 * mSpriteScale;
        } else if (mDirection.equals(CCMapDirection.SOUTHEAST)) {
            p.x -= 25 * mSpriteScale;
            p.y += 35 * mSpriteScale;
        } else if (mDirection.equals(CCMapDirection.NORTHWEST)) {
            p.x += 25 * mSpriteScale;
            p.y -= 10 * mSpriteScale;
        } else if (mDirection.equals(CCMapDirection.NORTHEAST)) {
            p.x -= 25 * mSpriteScale;
            p.y -= 10 * mSpriteScale;
        }

        return p;
    }
}
