/**
 * 
 */
package com.funzio.crimecity.particles.units;

import android.graphics.PointF;

import com.funzio.crimecity.game.model.CCMapDirection;
import com.funzio.pure2D.containers.Container;

/**
 * @author long
 */
public class AircraftCarrier extends SeaUnit implements Unit.Listener {

    private Container mAirContainer;

    /**
     * @param textureKey
     * @param direction
     */
    public AircraftCarrier(final CCMapDirection direction, final Container airContainer) {
        super("AircraftCarrier_0", direction);

        mAirContainer = airContainer;
        setSpriteScale(1.5f);
    }

    @Override
    protected void attackStart() {
        F15 aircraft = new F15();
        aircraft.setListener(this);
        PointF target = mTargetPosition;
        if (target.length() == 0) {
            // this is for testing purposes
            target = new PointF(mPosition.x, mPosition.y);
        }
        aircraft.setTarget(target);
        mAirContainer.addChild(aircraft);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.SeaUnit#attackEnd()
     */
    @Override
    protected void attackEnd() {
        // TODO nothing, let the F15 handles
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.Unit.Listener#onAnimationUpdate()
     */
    @Override
    public void onAnimationUpdate() {
        // TODO nothing
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.Unit.Listener#onAttackStart()
     */
    @Override
    public void onAttackStart() {
        // TODO nothing

    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.Unit.Listener#onAttackEnd()
     */
    @Override
    public void onAttackEnd() {
        if (mListener != null) {
            mListener.onAttackEnd();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.Unit.Listener#onSoundStart()
     */
    @Override
    public void onSoundStart() {
        // soundStart();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.Unit.Listener#onAnimationComplete()
     */
    @Override
    public void onAnimationComplete() {
        // TODO nothing
    }
}
