/**
 * 
 */
package com.funzio.pure2D.particles.nova;

/**
 * @author long
 */
public interface NovaDelegator {
    public void delegateEmitter(final NovaEmitter emitter, final Object... params);

    public void delegateParticle(final NovaParticle particle, final Object... params);
}
