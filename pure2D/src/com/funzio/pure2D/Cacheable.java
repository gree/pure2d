/**
 * 
 */
package com.funzio.pure2D;

/**
 * @author long
 */
public interface Cacheable {
    public void setCacheEnabled(boolean cacheEnabled);

    public boolean isCacheEnabled();

    public void clearCache();
}
