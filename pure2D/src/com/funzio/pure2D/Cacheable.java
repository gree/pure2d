/**
 * 
 */
package com.funzio.pure2D;

/**
 * @author long
 */
public interface Cacheable {
    // policies
    public static final int CACHE_WHEN_CHILDREN_STABLE = 0; // Best perf but might result some pixel-glitch in Perspective mode
    public static final int CACHE_WHEN_CHILDREN_CHANGED = 1;

    public void setCacheEnabled(boolean cacheEnabled);

    public boolean isCacheEnabled();

    public void clearCache();
}
