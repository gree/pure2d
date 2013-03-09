/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import com.funzio.pure2D.particles.HybridEmitter;
import com.funzio.pure2D.particles.nova.vo.EmitterVO;

/**
 * @author long
 */
public class NovaEmitter extends HybridEmitter {

    protected EmitterVO mEmitterVO;

    protected int mLastDeltaTime = 0;
    protected int mElapsedTime = 0;

    public NovaEmitter(final EmitterVO vo) {
        super();

        mEmitterVO = vo;

        createChildren();
    }

    protected void createChildren() {

    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.RectangularEmitter#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (super.update(deltaTime)) {
            mLastDeltaTime = deltaTime;
            mElapsedTime += deltaTime;

            return true;
        }

        return false;
    }

}
