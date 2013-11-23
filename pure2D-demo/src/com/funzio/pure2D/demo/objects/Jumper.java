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

    @Override
    public boolean update(final int deltaTime) {
        move(mRandom.nextInt(41) - 20, mRandom.nextInt(41) - 20);

        return super.update(deltaTime);
    }
}
