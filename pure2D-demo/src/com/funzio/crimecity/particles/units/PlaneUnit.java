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
public class PlaneUnit extends AirUnit {

    public PlaneUnit(final String textureKey) {
        // init with random direction
        super(textureKey, sRandom.nextBoolean() ? CCMapDirection.SOUTHWEST : CCMapDirection.SOUTHEAST);

        // and a fix speed
        setSpeed(10);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.jobs.Attacker#setTarget(android.graphics.PointF)
     */
    @Override
    public void setTarget(final PointF point) {
        super.setTarget(point);

        if (mDirection.equals(CCMapDirection.SOUTHEAST)) {
            setPosition(point.x - 500, point.y + 450);
        } else {
            setPosition(point.x + 300, point.y + 450);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.jobs.Attacker#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mFrame == 0) {
            // sound fx
            soundStart();
        } else if (mFrame < 20) {
            mAlpha += 0.05f;
        } else if (mFrame == 20) {
            mAlpha = 1.0f;
        } else if (mFrame == 50) {
            attackEnd();
        } else if (mFrame >= 130 && mFrame < 150) {
            mAlpha -= 0.05f;
        } else if (mFrame == 150) {
            // self remove
            finish();
        }

        // apply
        invalidate();

        return super.update(deltaTime);
    }

    @Override
    protected void attackEnd() {
        super.attackEnd();

        // XXX this is for testing only
        // ExplosionCombo explosion = new ExplosionCombo(ParticleAdapter.TEXTURE_MANAGER.mFireTexture, ParticleAdapter.TEXTURE_MANAGER.mSmokeTexture);
        // explosion.setPosition(mTargetPosition);
        // mParent.addChild(explosion);
    };

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.Unit#soundStart()
     */
    @Override
    protected void soundStart() {
        super.soundStart();
        // Play sound
        // [[RGSoundManager sharedInstance] playSoundWithPath:[[NSBundle mainBundle] pathForResource:@"fighter_flyby" ofType:@"mp3"]];
    }
}
