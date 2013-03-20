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
