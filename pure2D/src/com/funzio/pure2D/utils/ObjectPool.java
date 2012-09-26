/**
 * 
 */
package com.funzio.pure2D.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author long
 */
public class ObjectPool<T extends Reusable> {
    private List<T> mReusables = new ArrayList<T>();
    private int mMaxSize;

    public ObjectPool(final int maxSize) {
        mMaxSize = maxSize;
    }

    synchronized public T acquire() {
        if (mReusables.size() > 0) {
            return mReusables.remove(0);
        }

        return null;
    }

    synchronized public boolean release(final T reusable) {
        if (mReusables.size() < mMaxSize) {
            mReusables.add(reusable);
            return true;
        }

        return false;
    }

    synchronized public void clear() {
        mReusables.clear();
    }
}
