/**
 * 
 */
package com.funzio.pure2D.sounds;

/**
 * @author sajjadtabib
 *
 */
public abstract class AbstractMedia implements Media {

    protected int mKey;
    protected boolean mIsLooping;

    public AbstractMedia(final int key) {
        mKey = key;
        mIsLooping = false;
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.sounds.Media#getKey()
     */
    @Override
    public int getKey() {

        return mKey;
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.sounds.Media#isLooping()
     */
    @Override
    public boolean isLooping() {

        return mIsLooping;
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.sounds.Media#setLooping(boolean)
     */
    @Override
    public void setLooping(final boolean loop) {
        mIsLooping = loop;

    }

}
