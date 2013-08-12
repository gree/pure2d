/**
 * 
 */
package com.funzio.pure2D.utils;

import java.util.Vector;

/**
 * @author long
 */
public class ObjectPool<T extends Reusable> {
    private Vector<T> mReusables = new Vector<T>();
    private int mMaxSize;

    public ObjectPool(final int maxSize) {
        mMaxSize = maxSize;
    }

    public int getMaxSize() {
        return mMaxSize;
    }

    public void setMaxSize(int maxSize) {
        mMaxSize = maxSize;
    }

    public T acquire() {
        if (mReusables.size() > 0) {
            return mReusables.remove(0);
        }

        return null;
    }

    public boolean release(final T reusable) {
        if (mReusables.size() < mMaxSize) {
            mReusables.add(reusable);
            return true;
        }

        return false;
    }

    public void clear() {
        mReusables.clear();
    }
}
