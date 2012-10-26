/**
 * 
 */
package com.funzio.pure2D;

/**
 * @author long
 */
public interface Maskable {
    public void enableMask();

    public void enableMask(final int func, final int ref, final int mask);

    public void disableMask();
}
