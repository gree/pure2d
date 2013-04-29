/**
 * 
 */
package com.funzio.pure2D.particles;

import android.graphics.PointF;

/**
 * @author long
 */
public interface ParticleEmitter extends Particle.Listener {
    public PointF getPosition();

    public PointF getSize();

    public int getNumParticles();

    public void setListener(Listener listener);

    public Listener getListener();

    public interface Listener {
        public void onEmitterFinish(ParticleEmitter emitter);
    }
}
