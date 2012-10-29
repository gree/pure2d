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
    protected int mWidth;
    protected int mHeight;

    private AtlasFrameSet mMasterFrameSet = new AtlasFrameSet("");
    private Map<String, AtlasFrameSet> mSubFrameSets = new HashMap<String, AtlasFrameSet>();
    private int mNumSubFrameSets = 0;

    protected Listener mListener;

    public Atlas() {
    }

    public Atlas(final int width, final int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
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
        mSubFrameSets = new HashMap<String, AtlasFrameSet>();
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
