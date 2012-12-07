/**
 * 
 */
package com.funzio.pure2D.effects.fireworks;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.containers.Container;

/**
 * @author long
 */
public class Firework {
    public static void create(final Container container, final int numParticles, final Class<? extends DisplayObject> particleClass) {
        container.queueEvent(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < numParticles; i++) {
                    // TODO
                }

            }
        });
    }
}
