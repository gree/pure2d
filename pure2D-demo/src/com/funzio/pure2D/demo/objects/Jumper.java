/**
 * 
 */
package com.funzio.pure2D.demo.objects;

import java.util.Random;

import com.funzio.pure2D.uni.UniRect;

/**
 * @author long
 */
public class Jumper extends UniRect {

    private Random mRandom = new Random();

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        super.update(deltaTime);

        move(mRandom.nextInt(41) - 20, mRandom.nextInt(41) - 20);

        return true;
    }
}
