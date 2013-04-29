/**
 * 
 */
package com.funzio.crimecity.particles.units;

import android.graphics.PointF;

import com.funzio.crimecity.game.model.CCMapDirection;

/**
 * @author long
 */
abstract public class AirUnit extends Unit {

    public AirUnit(final String textureKey, final CCMapDirection direction) {
        // init with random direction
        super(textureKey, direction);

        // shadow effect
        mShadowOffset = new PointF(0, -200);

        // local offset
        // setOrigin(new PointF(48, -48));
    }
}
