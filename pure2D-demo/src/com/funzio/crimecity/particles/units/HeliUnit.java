/**
 * 
 */
package com.funzio.crimecity.particles.units;

import android.graphics.PointF;

import com.funzio.crimecity.game.model.CCMapDirection;

/**
 * @author long
 */
public class HeliUnit extends AirUnit {

    public HeliUnit(final String textureKey) {
        // init with random direction
        super(textureKey, sRandom.nextBoolean() ? CCMapDirection.SOUTHWEST : CCMapDirection.SOUTHEAST);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.jobs.Attacker#setTarget(android.graphics.PointF)
     */
    @Override
    public void setTarget(final PointF point) {
        super.setTarget(point);

        if (mDirection.equals(CCMapDirection.SOUTHEAST)) {
            setPosition(point.x - 600, point.y + 350);
        } else {
            setPosition(point.x + 400, point.y + 350);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.jobs.Attacker#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mFrame < 20) {
            mAlpha += 0.05f;
        } else if (mFrame == 20) {
            mAlpha = 1.0f;

            // sound fx
            soundStart();
        }

        if (mFrame < 30) {
            setSpeed(10);
        } else if (mFrame == 30) {
            attackEnd();
        } else {
            setSpeed(0);
        }

        if (mFrame < 130) {
            mPosition.y += Math.sin(mFrame / 10);
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

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.Unit#soundStart()
     */
    @Override
    protected void soundStart() {
        super.soundStart();
        // [[RGSoundManager sharedInstance] playSoundWithPath:[[NSBundle mainBundle] pathForResource:@"big_helicopter" ofType:@"mp3"]];
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.jobs.Attacker#attackEnd()
     */
    @Override
    protected void attackEnd() {
        super.attackEnd();
        // sound fx
        // [[RGSoundManager sharedInstance] playSoundWithPath:[[NSBundle mainBundle] pathForResource:@"attack_machinegun" ofType:@"mp3"] loopCount:2];

        // TODO show explosion at the target

        // XXX this is for testing only
        // Gunshots explosion = new Gunshots(ParticleAdapter.TEXTURE_MANAGER.mSmokeTexture);
        // explosion.setPosition(mTargetPosition);
        // mParent.addChild(explosion);
    }
}
