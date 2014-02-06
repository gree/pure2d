/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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
