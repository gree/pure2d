/**
 * 
 */
package com.funzio.crimecity.particles.units;

import com.funzio.crimecity.game.model.CCMapDirection;

/**
 * @author long
 */
public class Humvee extends GroundUnit {

    /**
     * @param textureKey
     * @param direction
     */
    public Humvee(final CCMapDirection direction) {
        super("Humvee", direction);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.GroundUnit#attackStart()
     */
    @Override
    protected void attackStart() {
        // TODO nothing
    }
}
