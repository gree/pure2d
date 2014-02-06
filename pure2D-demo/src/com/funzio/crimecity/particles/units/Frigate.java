/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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
