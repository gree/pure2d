/**
 * 
 */
package com.funzio.pure2D.atlas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.PointF;

import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public class AtlasFrameSet {
    protected final String mName;
    protected List<AtlasFrame> mFrames = new ArrayList<AtlasFrame>();
    protected PointF mFrameMaxSize = new PointF();
    protected Texture mTexture;
    protected int mFps = 0;

    public AtlasFrameSet(final String name) {
        mName = name;
    }

    public void addFrame(final AtlasFrame frame) {
        mFrames.add(frame);

        // check and auto assign texture
        if (frame.mTexture == null) {
            frame.mTexture = mTexture;
        }

        if (frame.mSize.x > mFrameMaxSize.x) {
            mFrameMaxSize.x = frame.mSize.x;
        }
        if (frame.mSize.y > mFrameMaxSize.y) {
            mFrameMaxSize.y = frame.mSize.y;
        }
    }

    public boolean removeFrame(final AtlasFrame frame) {
        return mFrames.remove(frame);
    }

    public void removeAllFrames() {
        mFrames.clear();
    }

    public AtlasFrame getFrame(final int index) {
        return index < mFrames.size() ? mFrames.get(index) : null;
    }

    public AtlasFrame getFrame(final String name) {
        int len = mFrames.size();
        for (int i = 0; i < len; i++) {
            AtlasFrame frame = mFrames.get(i);
            if (frame.mName.equals(name)) {
                return frame;
            }
        }

        return null;
    }

    public void setTexture(final Texture texture) {
        mTexture = texture;

        // apply to all frames
        int len = mFrames.size();
        for (int i = 0; i < len; i++) {
            mFrames.get(i).mTexture = texture;
        }
    }

    public Texture getTexture() {
        return mTexture;
    }

    public int getNumFrames() {
        return mFrames.size();
    }

    public void reverseFrames() {
        Collections.reverse(mFrames);
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();
        int len = mFrames.size();
        for (int i = 0; i < len; i++) {
            AtlasFrame frame = mFrames.get(i);
            st.append(frame.toString());
            if (i < len - 1) {
                st.append("\n");
            }
        }
        return String.format("AtlasFrameSet( %s,\n%s )", mName, st.toString());
    }

    /**
     * Extract the frame set's name from a full frame name
     * 
     * @param full
     * @param delimiter
     * @return
     */
    public static String extractName(final String full, final String delimiter) {
        String[] tokens = full.split(delimiter);
        if (tokens.length <= 1) {
            return full;
        }

        try {
            int newLen = tokens.length - 1;
            Integer.parseInt(tokens[newLen]);
            StringBuffer name = new StringBuffer();
            for (int i = 0; i < newLen; i++) {
                name.append(tokens[i]);
                if (i < newLen - 1) {
                    name.append(delimiter);
                }
            }
            return name.toString();
        } catch (NumberFormatException e) {
            return full;
        }
    }

    public static String extractName(final String full) {
        return extractName(full, "_");
    }

    public PointF getFrameMaxSize() {
        return mFrameMaxSize;
    }

    public void setFrameMaxSize(final PointF frameMaxSize) {
        mFrameMaxSize = frameMaxSize;
    }

    public int getFps() {
        return mFps;
    }

    public void setFps(final int fps) {
        mFps = fps;
    }
}
