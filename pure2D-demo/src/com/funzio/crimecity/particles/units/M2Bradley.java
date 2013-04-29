/**
 * 
 */
package com.funzio.crimecity.particles.units;

import android.graphics.PointF;

import com.funzio.crimecity.game.model.CCMapDirection;

/**
 * @author long
 */
public class M2Bradley extends GroundUnit {

    /**
     * @param textureKey
     * @param direction
     */
    public M2Bradley(final CCMapDirection direction) {
        super("M2Bradley", direction);
    }

    @Override
    protected PointF getFireReg() {
        PointF p = super.getFireReg();

        if (mDirection.equals(CCMapDirection.NORTHWEST)) {
            p.x += 80;
            p.y -= 20;
        } else if (mDirection.equals(CCMapDirection.NORTHEAST)) {
            p.x -= 80;
            p.y -= 20;
        } else if (mDirection.equals(CCMapDirection.SOUTHWEST)) {
            p.x += 95;
            p.y += 105;
        } else if (mDirection.equals(CCMapDirection.SOUTHEAST)) {
            p.x -= 95;
            p.y += 105;
        }

        return p;
    }
}
