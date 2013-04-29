/**
 * 
 */
package com.funzio.crimecity.particles.units;

import android.graphics.PointF;

import com.funzio.crimecity.game.model.CCMapDirection;

/**
 * @author long
 */
public class Abrams extends GroundUnit {

    /**
     * @param textureKey
     * @param direction
     */
    public Abrams(final CCMapDirection direction) {
        super("AbramsM1A1_0", direction);
    }

    @Override
    protected PointF getFireReg() {
        PointF p = super.getFireReg();

        if (mDirection.equals(CCMapDirection.SOUTHWEST) || mDirection.equals(CCMapDirection.SOUTHEAST)) {
            p.y += 40;
        }

        return p;
    }
}
