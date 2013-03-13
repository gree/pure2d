/**
 * 
 */
package com.funzio.crimecity.particles.units;

import android.graphics.PointF;

import com.funzio.crimecity.game.model.CCMapDirection;

/**
 * @author long
 */
public class Leopard extends GroundUnit {

    /**
     * @param textureKey
     * @param direction
     */
    public Leopard(final CCMapDirection direction) {
        super("Leopard2", direction);
    }

    @Override
    protected PointF getFireReg() {
        PointF p = super.getFireReg();

        if (mDirection.equals(CCMapDirection.SOUTHWEST) || mDirection.equals(CCMapDirection.SOUTHEAST)) {
            p.y += 25;
        }

        return p;
    }
}
