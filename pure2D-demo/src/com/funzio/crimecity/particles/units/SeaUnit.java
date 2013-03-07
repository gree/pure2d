/**
 * 
 */
package com.funzio.crimecity.particles.units;

import android.graphics.PointF;

import com.funzio.crimecity.game.model.CCMapDirection;
import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.crimecity.particles.fire.TankFire;

/**
 * @author long
 */
public class SeaUnit extends Unit {

    public SeaUnit(final String textureKey, final CCMapDirection direction) {
        super(textureKey, direction);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.Unit#setSpriteScale(float)
     */
    @Override
    protected void setSpriteScale(final float value) {
        super.setSpriteScale(value);

        // adjust shadow
        mShadowOffset = new PointF(-mSprite.getSize().x * mSpriteScale / 15, mSprite.getSize().y * mSpriteScale / 15);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.jobs.Attacker#setDirection(com.funzio.crimecity.game.model.CCMapDirection)
     */
    @Override
    public void setDirection(final CCMapDirection direction) {
        super.setDirection(direction);

        // local offset
        // if (direction.equals(CCMapDirection.NORTHWEST)) {
        // setOrigin(new PointF(96 * mSpriteScale, 96 * mSpriteScale));
        // } else if (direction.equals(CCMapDirection.NORTHEAST)) {
        // setOrigin(new PointF(96 * mSpriteScale, 72 * mSpriteScale));
        // } else if (direction.equals(CCMapDirection.SOUTHWEST)) {
        // setOrigin(new PointF(96 * mSpriteScale, 72 * mSpriteScale));
        // } else if (direction.equals(CCMapDirection.SOUTHEAST)) {
        // setOrigin(new PointF(96 * mSpriteScale, 96 * mSpriteScale));
        // }

        setOrigin(new PointF(96 * mSpriteScale, 72 * mSpriteScale));
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
        } else if (mFrame >= 20 && mFrame < 50) {
            setSpeed(2);
        } else if (mFrame >= 50 && mFrame < 60) {
            // Just sit there for a while
            setSpeed(0);
        } else if (mFrame == 60) {
            // fireeeeee
            attackStart();
        } else if (mFrame >= 75 && mFrame < 130) {
            // Just sit there for a while
        } else if (mFrame >= 130 && mFrame < 150) {
            mAlpha -= 0.05f;
        } else if (mFrame == 150) {
            // self remove
            finish();
        }

        // Give some delay to the target explosion
        if (mFrame == 84) {
            attackEnd();
        }

        // apply
        invalidate();

        return super.update(deltaTime);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.jobs.Attacker#attackStart()
     */
    @Override
    protected void attackStart() {
        super.attackStart();

        TankFire fire = new TankFire(ParticleAdapter.TEXTURE_MANAGER.mFireGreyTexture, ParticleAdapter.TEXTURE_MANAGER.mSmokeTexture);
        fire.setPosition(getFireReg());
        addChild(fire);

        // sound fx
        soundStart();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.jobs.Attacker#attackEnd()
     */
    @Override
    protected void attackEnd() {
        super.attackEnd();

        // TODO add explosion to the target

        // XXX this is for testing only
        // ExplosionCombo explosion = new ExplosionCombo(ParticleAdapter.TEXTURE_MANAGER.mFireTexture, ParticleAdapter.TEXTURE_MANAGER.mSmokeTexture);
        // explosion.setPosition(mTargetPosition);
        // mParent.addChild(explosion);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.Unit#soundStart()
     */
    @Override
    protected void soundStart() {
        super.soundStart();

        // if (seaAnimationType == SeaAttackAnimationCarrier)
        // {
        // aircraftCarrierPlaneAttack = [[StandardPlaneAttack alloc] initWithJob:job planeType:PlaneAttackAnimationF15Fighter andDelegate:jobPerform];
        // }
        // else
        // {
        // [[RGSoundManager sharedInstance] playSoundWithPath:[[NSBundle mainBundle] pathForResource:@"explosion_small" ofType:@"mp3"]];
        //
        // [self setupShipFireEffect];
        // }
    }

    /**
     * @return the registration point for Fire
     */
    protected PointF getFireReg() {
        PointF p = new PointF();
        p.x = (mDirection.equals(CCMapDirection.NORTHWEST) || mDirection.equals(CCMapDirection.SOUTHWEST)) ? 0 : mSprite.getSize().x * mSpriteScale;
        p.y = (mDirection.equals(CCMapDirection.NORTHWEST) || mDirection.equals(CCMapDirection.NORTHEAST)) ? mSprite.getSize().y * mSpriteScale : 0;

        return p;
    }
}
