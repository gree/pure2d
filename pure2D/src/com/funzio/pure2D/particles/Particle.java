/**
 * 
 */
package com.funzio.pure2D.particles;

import java.util.Random;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.utils.Reusable;

/**
 * @author long
 */
public interface Particle extends DisplayObject, Reusable {

    public static final Random RANDOM = new Random();

    public void finish();

    public boolean isFinished();

    public void setEmitter(ParticleEmitter emitter);

    public ParticleEmitter getEmitter();

    public void setParticleListener(Listener listener);

    public Listener getParticleListener();

    public interface Listener {
        public void onParticleFinish(Particle particle);
    }
}
