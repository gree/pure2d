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
package com.funzio.pure2D.atlas;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author long
 */
public class Atlas {
    protected String mImage;
    protected float mWidth;
    protected float mHeight;

    protected final AtlasFrameSet mMasterFrameSet = new AtlasFrameSet("");
    protected final Map<String, AtlasFrameSet> mSubFrameSets = new HashMap<String, AtlasFrameSet>();
    protected int mNumSubFrameSets = 0;

    protected Listener mListener;

    public Atlas() {
    }

    public Atlas(final float width, final float height) {
        mWidth = width;
        mHeight = height;
    }

    public void setSize(final float width, final float height) {
        mWidth = width;
        mHeight = height;
    }

    public String getImage() {
        return mImage;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public AtlasFrame getFrame(final int index) {
        return mMasterFrameSet.getFrame(index);
    }

    public AtlasFrame getFrame(final String name) {
        return mMasterFrameSet.getFrame(name);
    }

    public void addFrame(final AtlasFrame frame) {
        mMasterFrameSet.addFrame(frame);

        // this frame belongs to a set?
        String setName = AtlasFrameSet.extractName(frame.getName());

        if (!setName.equals(frame.getName())) {
            AtlasFrameSet set = mSubFrameSets.get(setName);
            if (set == null) {
                // create a new set
                set = new AtlasFrameSet(setName);
                // add to the list
                mSubFrameSets.put(setName, set);
                mNumSubFrameSets++;
            }
            // add the frame to the set
            set.addFrame(frame);
        }
    }

    public boolean removeFrame(final AtlasFrame frame) {
        if (mMasterFrameSet.removeFrame(frame)) {
            Set<String> keys = mSubFrameSets.keySet();
            for (String key : keys) {
                AtlasFrameSet frameSet = mSubFrameSets.get(key);
                if (frameSet != null) {
                    if (frameSet.removeFrame(frame)) {
                        // remove the empty subset
                        if (frameSet.getNumFrames() == 0) {
                            mSubFrameSets.remove(key);
                            mNumSubFrameSets--;
                        }
                        break;
                    }
                }
            }
            return true;
        }

        return false;
    }

    public void removeAllFrames() {
        mMasterFrameSet.removeAllFrames();
        mSubFrameSets.clear();
        mNumSubFrameSets = 0;
    }

    public AtlasFrameSet getMasterFrameSet() {
        return mMasterFrameSet;
    }

    public Map<String, AtlasFrameSet> getSubFrameSets() {
        return mSubFrameSets;
    }

    public AtlasFrameSet getSubFrameSet(final String name) {
        return mSubFrameSets.get(name);
    }

    public boolean addSubFrameSet(final AtlasFrameSet newSet) {
        if (!mSubFrameSets.containsKey(newSet.mName)) {
            mSubFrameSets.put(newSet.mName, newSet);
            mNumSubFrameSets++;

            return true;
        } else {
            return false;
        }
    }

    public int getNumSubFrameSets() {
        return mNumSubFrameSets;
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public static interface Listener {
        public void onAtlasLoad(Atlas atlas);
    }
}
