/**
 * 
 */
package com.funzio.pure2D.samples;

import java.util.Random;

import com.funzio.pure2D.shapes.Rectangular;

/**
 * @author long
 */
public class Jumper extends Rectangular {

    private Random mRandom = new Random();

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        super.update(deltaTime);

        moveBy(mRandom.nextInt(41) - 20, mRandom.nextInt(41) - 20);

        return true;
    }
}
